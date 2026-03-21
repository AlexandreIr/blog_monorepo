package br.com.libertadfacilities.blog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {

    @NotBlank(message = "O e-mail não pode estar vazio.")
    @Email(message = "O formato do e-mail deve ser valido.")
    private String email;

    @NotBlank(message = "A senha não pode estar em branco.")
    private String password;
}
