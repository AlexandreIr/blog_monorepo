package br.com.libertadfacilites.services;

import br.com.libertadfacilites.dtos.OuvidoriaRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class OuvidoriaMailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.ouvidoria-to}")
    private String ouvidoriaTo;

    @Value("${spring.mail.username}")
    private String mailFrom;

    public OuvidoriaMailService(
            JavaMailSender mailSender,
            TemplateEngine templateEngine
    ) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void enviarManifestacao(OuvidoriaRequest request) {
        validarIdentificacao(request);

        Context context = new Context(new Locale("pt", "BR"));

        boolean identificada = "identificada".equalsIgnoreCase(normalize(request.identificacao()));

        context.setVariable("tipoManifestacao", labelTipoManifestacao(request.tipoManifestacao()));
        context.setVariable("identificacao", identificada ? "Identificada" : "Anônima");
        context.setVariable("identificada", identificada);

        context.setVariable("nome", valueOrDefault(request.nome(), "Não informado"));
        context.setVariable("email", valueOrDefault(request.email(), "Não informado"));
        context.setVariable("telefone", valueOrDefault(request.telefone(), "Não informado"));

        context.setVariable("vinculo", labelVinculo(request.vinculo()));
        context.setVariable("departamento", valueOrDefault(request.departamento(), "Não informado"));
        context.setVariable("assunto", valueOrDefault(request.assunto(), "Sem assunto"));
        context.setVariable("descricao", valueOrDefault(request.descricao(), "Não informado"));
        context.setVariable("dataOcorrencia", valueOrDefault(request.dataOcorrencia(), "Não informada"));
        context.setVariable("localOcorrencia", valueOrDefault(request.localOcorrencia(), "Não informado"));
        context.setVariable("envolvidos", emptyToNull(request.envolvidos()));
        context.setVariable("evidencias", emptyToNull(request.evidencias()));

        context.setVariable("dataEnvio", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        String html = templateEngine.process(
                "ouvidoria-template",
                context
        );

        enviarEmailHtml(request, html);
    }

    private void enviarEmailHtml(OuvidoriaRequest request, String html) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    true,
                    "UTF-8"
            );

            helper.setFrom(mailFrom);
            helper.setTo(ouvidoriaTo);
            helper.setSubject(criarAssuntoEmail(request));
            helper.setText(html, true);

            if (isNotBlank(request.email()) && isEmailValidoBasico(request.email())) {
                helper.setReplyTo(request.email());
            }

            mailSender.send(mimeMessage);
        } catch (MessagingException exception) {
            throw new IllegalStateException(
                    "Erro ao montar o e-mail da Ouvidoria.",
                    exception
            );
        }
    }

    private String criarAssuntoEmail(OuvidoriaRequest request) {
        String tipo = labelTipoManifestacao(request.tipoManifestacao());
        String assunto = valueOrDefault(request.assunto(), "Sem assunto");

        return "[Ouvidoria LCS] " + tipo + " - " + assunto;
    }

    private void validarIdentificacao(OuvidoriaRequest request) {
        boolean identificada = "identificada".equalsIgnoreCase(
                normalize(request.identificacao())
        );

        if (!identificada) {
            return;
        }

        if (!isNotBlank(request.nome())) {
            throw new IllegalArgumentException(
                    "Informe o nome para manifestação identificada."
            );
        }

        if (!isNotBlank(request.email())) {
            throw new IllegalArgumentException(
                    "Informe o e-mail para manifestação identificada."
            );
        }
    }

    private String labelTipoManifestacao(String value) {
        return switch (normalize(value)) {
            case "reclamacao" -> "Reclamação";
            case "sugestao" -> "Sugestão";
            case "elogio" -> "Elogio";
            case "duvida" -> "Dúvida";
            case "denuncia" -> "Denúncia ou relato sensível";
            case "outro" -> "Outro";
            default -> valueOrDefault(value, "Não informado");
        };
    }

    private String labelVinculo(String value) {
        return switch (normalize(value)) {
            case "colaborador" -> "Colaborador";
            case "cliente" -> "Cliente";
            case "fornecedor" -> "Fornecedor";
            case "parceiro" -> "Parceiro";
            case "candidato" -> "Candidato";
            case "outro" -> "Outro";
            default -> valueOrDefault(value, "Não informado");
        };
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value.trim().toLowerCase();
    }

    private String valueOrDefault(String value, String defaultValue) {
        if (!isNotBlank(value)) {
            return defaultValue;
        }

        return value.trim();
    }

    private String emptyToNull(String value) {
        if (!isNotBlank(value)) {
            return null;
        }

        return value.trim();
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean isEmailValidoBasico(String email) {
        return email.contains("@") && email.contains(".");
    }
}
