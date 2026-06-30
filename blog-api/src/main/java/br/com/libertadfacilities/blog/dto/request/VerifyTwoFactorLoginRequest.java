package br.com.libertadfacilities.blog.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyTwoFactorLoginRequest(
        @NotBlank(message = "Token temporário obrigatório")
        String temporaryToken,

        @NotBlank(message = "código é obrigatorio")
        String code
) {
}
