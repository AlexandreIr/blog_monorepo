package br.com.libertadfacilites.controllers;

import br.com.libertadfacilites.dtos.EmailResposeDto;
import br.com.libertadfacilites.dtos.LeadEmailRequestDto;
import br.com.libertadfacilites.exceptions.TooManyRequestsException;
import br.com.libertadfacilites.services.MailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailController {

    private final MailService mailService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmailResposeDto> sendEmail(@Valid @ModelAttribute LeadEmailRequestDto request,
                                                     BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                StringBuilder sb = new StringBuilder("Erro de validação: ");
                bindingResult.getFieldErrors().forEach(err ->
                        sb.append("[")
                          .append(err.getField())
                          .append(": ")
                          .append(err.getDefaultMessage())
                          .append("] "));
                return ResponseEntity.badRequest().body(new EmailResposeDto(false, sb.toString().trim()));
            }

            EmailResposeDto response = mailService.sendLeadEmail(request);
                        if (response.success()) {
                            return ResponseEntity.ok(response);
                        }
                        return ResponseEntity.badRequest().body(response);
        } catch (TooManyRequestsException ex) {
            return ResponseEntity.status(429)
                    .body(new EmailResposeDto(false, ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .body(new EmailResposeDto(false, "Falha ao enviar e-mail: " + ex.getMessage()));
        }
    }
}
