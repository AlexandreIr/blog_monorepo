package br.com.libertadfacilites.services;

import br.com.libertadfacilites.dtos.EmailResposeDto;
import br.com.libertadfacilites.dtos.LeadEmailRequestDto;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MailService {

    private static final long MAX_FILE_SIZE_BYTES = 50L * 1024 * 1024; // 50MB

    private static final int[] CNPJ_FIRST_DIGIT_WEIGHTS = {
            5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2
    };

    private static final int[] CNPJ_SECOND_DIGIT_WEIGHTS = {
            6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2
    };

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.commercial-to}")
    private String commercialTo;

    @Value("${spring.mail.username:}")
    private String defaultFrom;

    public EmailResposeDto sendLeadEmail(LeadEmailRequestDto dto) {
        try {
            List<String> validationErrors = validateLeadData(dto);

            if (!validationErrors.isEmpty()) {
                return new EmailResposeDto(
                        false,
                        "Dados inválidos: " + String.join(" ", validationErrors)
                );
            }

            MultipartFile file = dto.getArquivo();

            if (file != null && !file.isEmpty() && file.getSize() > MAX_FILE_SIZE_BYTES) {
                return new EmailResposeDto(
                        false,
                        "Arquivo excede o limite de 50MB."
                );
            }

            List<String> tiposServicos = normalizeServices(dto.getTiposServicos());
            String servicosStr = String.join(", ", tiposServicos);

            Context ctx = new Context(new Locale("pt", "BR"));

            ctx.setVariable("nomeCliente", safe(dto.getNomeCliente()));
            ctx.setVariable("nomeEmpresa", safe(dto.getNomeEmpresa()));
            ctx.setVariable("cnpj", safe(dto.getCnpj()));
            ctx.setVariable("telefone", safe(dto.getTelefone()));
            ctx.setVariable("email", safe(dto.getEmail()));
            ctx.setVariable("tiposServicos", tiposServicos);
            ctx.setVariable("tiposServicosStr", servicosStr);
            ctx.setVariable("quantidade", dto.getQuantidade());
            ctx.setVariable("funcaoNaEmpresa", safe(dto.getFuncaoNaEmpresa()));
            ctx.setVariable("colaboradoresNecessarios", dto.getColaboradoresNecessarios());
            ctx.setVariable("quantidadeColaboradoresDoLead", dto.getQuantidadeColaboradoresDoLead());
            ctx.setVariable("mensagemPersonalizada", safe(dto.getMensagemPersonalizada()));
            ctx.setVariable("temAnexo", file != null && !file.isEmpty());

            String html = templateEngine.process("lead-email", ctx);

            MimeMessage mimeMessage = mailSender.createMimeMessage();

            boolean hasAttachment = file != null && !file.isEmpty();

            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    hasAttachment,
                    StandardCharsets.UTF_8.name()
            );

            String[] toAddresses = commercialTo.split("\\s*,\\s*");

            helper.setTo(toAddresses);
            helper.setSubject(buildSubject(dto, servicosStr));
            helper.setText(html, true);

            if (hasText(defaultFrom)) {
                helper.setFrom(defaultFrom.trim());
            }

            if (hasText(dto.getEmail())) {
                helper.setReplyTo(dto.getEmail().trim());
            }

            if (hasAttachment) {
                String filename = hasText(file.getOriginalFilename())
                        ? file.getOriginalFilename()
                        : "anexo";

                InputStreamSource source = file::getInputStream;

                helper.addAttachment(filename, source);
            }

            mailSender.send(mimeMessage);

            return new EmailResposeDto(
                    true,
                    "E-mail enviado com sucesso."
            );
        } catch (Exception ex) {
            return new EmailResposeDto(
                    false,
                    "Falha ao enviar e-mail: " + ex.getMessage()
            );
        }
    }

    private List<String> validateLeadData(LeadEmailRequestDto dto) {
        List<String> errors = new ArrayList<>();

        if (dto == null) {
            errors.add("Dados do formulário não foram enviados.");
            return errors;
        }

        if (!hasText(dto.getNomeCliente())) {
            errors.add("Nome do cliente é obrigatório.");
        }

        if (!hasText(dto.getNomeEmpresa())) {
            errors.add("Nome da empresa é obrigatório.");
        }

        if (!isValidCnpj(dto.getCnpj())) {
            errors.add("CNPJ inválido.");
        }

        if (!isValidBrazilianPhone(dto.getTelefone())) {
            errors.add("Telefone inválido.");
        }

        if (!hasText(dto.getFuncaoNaEmpresa())) {
            errors.add("Cargo ou função é obrigatório.");
        }

        if (normalizeServices(dto.getTiposServicos()).isEmpty()) {
            errors.add("Selecione pelo menos um serviço desejado.");
        }

        return errors;
    }

    private String buildSubject(LeadEmailRequestDto dto, String servicosStr) {
        String nomeCliente = hasText(dto.getNomeCliente())
                ? dto.getNomeCliente().trim()
                : "Cliente não informado";

        String servicos = hasText(servicosStr)
                ? servicosStr
                : "Serviço não informado";

        return "Novo lead: " + nomeCliente + " - " + servicos;
    }

    private List<String> normalizeServices(List<String> services) {
        if (services == null) {
            return List.of();
        }

        return services.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(service -> !service.isBlank())
                .distinct()
                .toList();
    }

    private boolean isValidCnpj(String cnpj) {
        String digits = onlyDigits(cnpj);

        if (digits.length() != 14 || hasAllEqualDigits(digits)) {
            return false;
        }

        int d1 = calculateCnpjDigit(
                digits.substring(0, 12),
                CNPJ_FIRST_DIGIT_WEIGHTS
        );

        int d2 = calculateCnpjDigit(
                digits.substring(0, 12) + d1,
                CNPJ_SECOND_DIGIT_WEIGHTS
        );

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

        if (digits.startsWith("55") && (digits.length() == 12 || digits.length() == 13)) {
            digits = digits.substring(2);
        }

        if ((digits.length() != 10 && digits.length() != 11) || hasAllEqualDigits(digits)) {
            return false;
        }

        int ddd = Integer.parseInt(digits.substring(0, 2));

        if (ddd < 11 || ddd > 99) {
            return false;
        }

        char firstSubscriberDigit = digits.charAt(2);

        if (digits.length() == 11) {
            return firstSubscriberDigit == '9';
        }

        return firstSubscriberDigit >= '2' && firstSubscriberDigit <= '5';
    }

    private String onlyDigits(String value) {
        if (value == null) {
            return "";
        }

        return value.replaceAll("\\D", "");
    }

    private boolean hasAllEqualDigits(String digits) {
        return digits.chars().distinct().count() == 1;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}