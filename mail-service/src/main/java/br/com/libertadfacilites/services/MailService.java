package br.com.libertadfacilites.services;

import br.com.libertadfacilites.dtos.EmailResposeDto;
import br.com.libertadfacilites.dtos.LeadEmailRequestDto;
import br.com.libertadfacilites.exceptions.TooManyRequestsException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MailService {

    private static final long MAX_FILE_SIZE_BYTES = 100L * 1024 * 1024; // 100MB
    private static final Duration CNPJ_API_TIMEOUT = Duration.ofSeconds(10);
    private static final String CNPJ_PLACEHOLDER = "{cnpj}";
    private static final int[] CNPJ_FIRST_DIGIT_WEIGHTS = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] CNPJ_SECOND_DIGIT_WEIGHTS = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final long RATE_LIMIT_WINDOW_MILLIS = 60_000L; // 1 minuto
    private static final int RATE_LIMIT_MAX_REQUESTS = 3;

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConcurrentHashMap<String, Deque<Long>> rateLimiter = new ConcurrentHashMap<>();

    @Value("${app.mail.commercial-to}")
    private String commercialTo;

    @Value("${spring.mail.username:}")
    private String defaultFrom;

    // Padrão atualizado para OpenCNPJ
    @Value("${app.cnpj.api-url:https://opencnpj.org/{cnpj}}")
    private String cnpjApiUrl;

    public EmailResposeDto sendLeadEmail(LeadEmailRequestDto dto) throws Exception {
        // 0) Rate limiting por usuário (email -> telefone -> cnpj)
        String rateKey = Optional.ofNullable(dto.getEmail()).filter(s -> s != null && !s.isBlank())
                .orElseGet(() -> Optional.ofNullable(dto.getTelefone()).filter(s -> s != null && !s.isBlank())
                        .orElseGet(() -> onlyDigits(dto.getCnpj())));
        checkRateLimit(rateKey);

        // 1) Validações locais
        List<String> validationErrors = validateLeadData(dto);
        if (!validationErrors.isEmpty()) {
            return new EmailResposeDto(false, "Dados inválidos: " + String.join(" ", validationErrors));
        }

        // 2) Validação externa do CNPJ (situação ATIVA e nome fantasia OU razão social correspondente)
        Optional<String> externalError = validateCnpjByExternalApi(dto);
        if (externalError.isPresent()) {
            return new EmailResposeDto(false, "Dados inválidos: " + externalError.get());
        }

        // 3) Validação de tamanho do arquivo
        MultipartFile file = dto.getArquivo();
        if (file != null && !file.isEmpty() && file.getSize() > MAX_FILE_SIZE_BYTES) {
            return new EmailResposeDto(false, "Arquivo excede o limite de 100MB.");
        }

        // Monta contexto do Thymeleaf
        Context ctx = new Context(new Locale("pt", "BR"));
        String servicosStr = String.join(", ", dto.getTiposServicos());

        ctx.setVariable("nomeCliente", dto.getNomeCliente());
        ctx.setVariable("nomeEmpresa", dto.getNomeEmpresa());
        ctx.setVariable("cnpj", dto.getCnpj());
        ctx.setVariable("telefone", dto.getTelefone());
        ctx.setVariable("email", dto.getEmail());
        ctx.setVariable("tiposServicos", dto.getTiposServicos());
        ctx.setVariable("tiposServicosStr", servicosStr);
        ctx.setVariable("quantidade", dto.getQuantidade());
        ctx.setVariable("funcaoNaEmpresa", dto.getFuncaoNaEmpresa());
        ctx.setVariable("colaboradoresNecessarios", dto.getColaboradoresNecessarios());
        ctx.setVariable("quantidadeColaboradoresDoLead", dto.getQuantidadeColaboradoresDoLead());
        ctx.setVariable("mensagemPersonalizada", dto.getMensagemPersonalizada());
        ctx.setVariable("temAnexo", file != null && !file.isEmpty());

        String html = templateEngine.process("lead-email", ctx);

        // Monta e envia o e-mail
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        boolean multipart = file != null && !file.isEmpty();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, multipart, StandardCharsets.UTF_8.name());

        String[] toAddresses = commercialTo.split("\\s*,\\s*");
        helper.setTo(toAddresses);

        String subject = "Novo lead: " + dto.getNomeCliente() + " - " + servicosStr;
        helper.setSubject(subject);
        helper.setText(html, true);

        // From padrão do smtp configurado; define reply-to para o e-mail do lead
        if (defaultFrom != null && !defaultFrom.isBlank()) {
            helper.setFrom(defaultFrom);
        }
        helper.setReplyTo(dto.getEmail());

        if (multipart) {
            String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "anexo";
            InputStreamSource source = file::getInputStream;
            helper.addAttachment(filename, source);
        }

        try {
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            System.err.println("Falha ao enviar e-mail: " + e.getMessage());
            return new EmailResposeDto(false, "Falha ao enviar e-mail. Tente novamente mais tarde.");
        }

        return new EmailResposeDto(true, "E-mail enviado com sucesso.");
    }

    private void checkRateLimit(String key) {
        long now = System.currentTimeMillis();
        Deque<Long> deque = rateLimiter.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (deque) {
            while (!deque.isEmpty() && (now - deque.peekFirst()) > RATE_LIMIT_WINDOW_MILLIS) {
                deque.pollFirst();
            }
            if (deque.size() >= RATE_LIMIT_MAX_REQUESTS) {
                throw new TooManyRequestsException("Muitas requisições. Tente novamente em instantes.");
            }
            deque.addLast(now);
        }
    }

    private List<String> validateLeadData(LeadEmailRequestDto dto) {
        List<String> errors = new ArrayList<>();

        if (!isValidCnpj(dto.getCnpj())) {
            errors.add("CNPJ inválido.");
        }

        if (!isValidBrazilianPhone(dto.getTelefone())) {
            errors.add("Telefone inválido.");
        }

        if (isLikelyInvalidText(dto.getNomeCliente())) {
            errors.add("Nome inválido.");
        }

        if (isLikelyInvalidText(dto.getFuncaoNaEmpresa())) {
            errors.add("Cargo inválido.");
        }

        return errors;
    }

    private boolean isValidCnpj(String cnpj) {
        String digits = onlyDigits(cnpj);
        if (digits.length() != 14 || hasAllEqualDigits(digits)) {
            return false;
        }

        int d1 = calculateCnpjDigit(digits.substring(0, 12), CNPJ_FIRST_DIGIT_WEIGHTS);
        int d2 = calculateCnpjDigit(digits.substring(0, 12) + d1, CNPJ_SECOND_DIGIT_WEIGHTS);

        return Character.getNumericValue(digits.charAt(12)) == d1
                && Character.getNumericValue(digits.charAt(13)) == d2;
    }

    private int calculateCnpjDigit(String digits, int[] weights) {
        int sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += Character.getNumericValue(digits.charAt(i)) * weights[i];
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    private boolean isValidBrazilianPhone(String phone) {
        String digits = onlyDigits(phone);
        // Remove código do país opcional
        if (digits.startsWith("55") && (digits.length() == 12 || digits.length() == 13)) {
            digits = digits.substring(2);
        }

        // Fixo: 10 dígitos (DD + 8), Móvel: 11 dígitos (DD + 9)
        if ((digits.length() != 10 && digits.length() != 11) || hasAllEqualDigits(digits)) {
            return false;
        }

        // DDD simples entre 11 e 99
        int ddd = Integer.parseInt(digits.substring(0, 2));
        if (ddd < 11 || ddd > 99) {
            return false;
        }

        char firstSubscriberDigit = digits.charAt(2);
        // Móvel precisa iniciar com 9
        if (digits.length() == 11) {
            return firstSubscriberDigit == '9';
        }
        // Fixo geralmente inicia entre 2 e 5
        return firstSubscriberDigit >= '2' && firstSubscriberDigit <= '5';
    }

    // ===================== Validação externa do CNPJ =====================

    private Optional<String> validateCnpjByExternalApi(LeadEmailRequestDto dto) {
        String cnpjDigits = onlyDigits(dto.getCnpj());
        String providedCompanyName = normalizeText(dto.getNomeEmpresa());
        if (providedCompanyName.isBlank()) {
            return Optional.of("nome da empresa não informado para conferência com CNPJ.");
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(buildCnpjApiUri(cnpjDigits))
                    .timeout(CNPJ_API_TIMEOUT)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return Optional.of("não foi possível validar o CNPJ na API externa.");
            }

            JsonNode body = objectMapper.readTree(response.body());

            // Algumas APIs retornam "status":"ERROR" para CNPJ inválido/não encontrado
            String status = firstText(body, "status");
            if ("ERROR".equalsIgnoreCase(status)) {
                return Optional.of("CNPJ não encontrado ou recusado pela API externa.");
            }

            // Campo da situação pode variar conforme a API
            String situacao = firstText(body, "situacao", "descricao_situacao_cadastral", "situacaoCadastral", "situacao_cadastral", "descricao_situacao");
            if (!situacao.isBlank() && !isActiveCompany(situacao)) {
                return Optional.of("CNPJ não está ativo. Situação retornada pela API: " + situacao + ".");
            }

            // Nome fantasia e razão social (aceita qualquer um que coincida)
            String nomeFantasia = firstText(body, "fantasia", "nome_fantasia", "nomeFantasia");
            String razaoSocial = firstText(body, "razao_social", "razaoSocial", "razao", "nome");

            if (nomeFantasia.isBlank() && razaoSocial.isBlank()) {
                return Optional.of("não foi possível confirmar nome fantasia/razão social do CNPJ na API externa.");
            }

            String fantasiaNorm = normalizeText(nomeFantasia);
            String razaoNorm = normalizeText(razaoSocial);

            if (!providedCompanyName.equals(fantasiaNorm) && !providedCompanyName.equals(razaoNorm)) {
                return Optional.of("nome fantasia ou razão social não conferem com o CNPJ informado.");
            }

            return Optional.empty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.of("validação do CNPJ na API externa foi interrompida.");
        } catch (Exception e) {
            return Optional.of("não foi possível validar o CNPJ na API externa.");
        }
    }

    private URI buildCnpjApiUri(String cnpjDigits) {
        String encodedCnpj = URLEncoder.encode(cnpjDigits, StandardCharsets.UTF_8);
        String url = cnpjApiUrl.contains(CNPJ_PLACEHOLDER)
                ? cnpjApiUrl.replace(CNPJ_PLACEHOLDER, encodedCnpj)
                : appendPath(cnpjApiUrl, encodedCnpj);
        return URI.create(url);
    }

    private String appendPath(String baseUrl, String path) {
        if (baseUrl.endsWith("/")) return baseUrl + path;
        return baseUrl + "/" + path;
    }

    private boolean isActiveCompany(String situacao) {
        String s = normalizeText(situacao);
        return "ATIVA".equals(s) || "ATIVO".equals(s);
    }

    private boolean isLikelyInvalidText(String value) {
        String norm = normalizeLetters(value);
        if (norm.length() < 2) return true;
        if (allSame(norm)) return true;
        if (hasSequentialRun(norm, 4)) return true;
        if (norm.length() >= 6 && norm.chars().distinct().count() <= 2) return true;
        return false;
    }

    private String normalizeLetters(String value) {
        if (value == null) return "";
        String n = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return n.replaceAll("[^A-Za-z]", "").toLowerCase(Locale.ROOT);
    }

    private String firstText(JsonNode node, String... fieldNames) {
        for (String f : fieldNames) {
            if (node.hasNonNull(f)) {
                String v = node.get(f).asText();
                if (v != null && !v.isBlank()) return v.trim();
            }
        }
        return "";
    }

    private String onlyDigits(String value) {
        if (value == null) return "";
        return value.replaceAll("\\D", "");
    }

    private boolean hasAllEqualDigits(String digits) {
        return digits.chars().distinct().count() == 1;
    }

    private boolean allSame(String s) {
        return !s.isEmpty() && s.chars().distinct().count() == 1;
    }

    private boolean hasSequentialRun(String s, int runLength) {
        if (s.length() < runLength) return false;
        int inc = 1, dec = 1;
        for (int i = 1; i < s.length(); i++) {
            char c = s.charAt(i);
            char p = s.charAt(i - 1);
            if (c == p + 1) {
                inc++; dec = 1;
            } else if (c == p - 1) {
                dec++; inc = 1;
            } else {
                inc = dec = 1;
            }
            if (inc >= runLength || dec >= runLength) return true;
        }
        return false;
    }

    private String normalizeText(String value) {
        if (value == null) return "";
        String n = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return n.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9 ]", " ").replaceAll("\\s+", " ").trim();
    }
}
