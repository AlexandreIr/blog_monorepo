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

@Service
@RequiredArgsConstructor
public class MailService {

    private static final long MAX_FILE_SIZE_BYTES = 100L * 1024 * 1024; // 100MB
    private static final int[] CNPJ_FIRST_DIGIT_WEIGHTS = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] CNPJ_SECOND_DIGIT_WEIGHTS = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;


    @Value("${app.mail.commercial-to}")
    private String commercialTo;

    @Value("${spring.mail.username:}")
    private String defaultFrom;

    public EmailResposeDto sendLeadEmail(LeadEmailRequestDto dto) throws Exception {
        List<String> validationErrors = validateLeadData(dto);
        if (!validationErrors.isEmpty()) {
            return new EmailResposeDto(false, "Dados inválidos: " + String.join(" ", validationErrors));
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

        mailSender.send(mimeMessage);

        return new EmailResposeDto(true, "E-mail enviado com sucesso.");
    }

    private List<String> validateLeadData(LeadEmailRequestDto dto) {
        List<String> errors = new ArrayList<>();

        if (!isValidCnpj(dto.getCnpj())) {
            errors.add("CNPJ inválido.");
        }

        if (!isValidBrazilianPhone(dto.getTelefone())) {
            errors.add("Telefone inválido.");
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



    private String onlyDigits(String value) {
        if (value == null) return "";
        return value.replaceAll("\\D", "");
    }

    private boolean hasAllEqualDigits(String digits) {
        return digits.chars().distinct().count() == 1;
    }
}
