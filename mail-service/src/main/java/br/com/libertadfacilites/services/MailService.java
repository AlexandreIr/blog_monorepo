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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MailService {

    private static final long MAX_FILE_SIZE_BYTES = 100L * 1024 * 1024; // 100MB

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.commercial-to}")
    private String commercialTo;

    @Value("${spring.mail.username:}")
    private String defaultFrom;

    public EmailResposeDto sendLeadEmail(LeadEmailRequestDto dto) throws Exception {
        // Validação de tamanho do arquivo
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
}
