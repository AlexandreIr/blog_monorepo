package br.com.libertadfacilities.blog.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try{
            mailSender.send(message);
            log.warn("Email enviado com sucesso para: "+to);
        } catch (Exception e){
            log.error("Não foi possível enviar o email por: "+e.getMessage());
        }
    }

    public void sendApprovalEmail(String email, String name){
        sendEmail(email, "Seu comentario foi aprovado",
                "Olá "+name+", seu comentário acabou de ser aprovado e já está publicado no blog.");
    }

    public void sendRejectionEmail(String email, String name) {
        sendEmail(email, "Atualização sobre seu comentário",
                "Olá " + name + ", seu comentário não pôde ser aprovado de acordo com as regras da nossa comunidade.");
    }

    public void sendReplyEmail(String email, String name, String replierName) {
        sendEmail(email, "Você recebeu uma resposta!",
                "Olá " + name + ", " + replierName + " acabou de responder ao seu comentário.");
    }
}
