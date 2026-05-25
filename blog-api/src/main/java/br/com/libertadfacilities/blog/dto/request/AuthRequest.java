package br.com.libertadfacilities.blog.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest (

    @NotBlank(message = "O e-mail não pode estar vazio.")
    @Email(message = "O formato do e-mail deve ser valido.")
    String email,

    @NotBlank(message = "A senha não pode estar em branco.")
    String password
){
        }
