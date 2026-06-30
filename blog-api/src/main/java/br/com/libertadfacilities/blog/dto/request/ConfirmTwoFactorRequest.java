package br.com.libertadfacilities.blog.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ConfirmTwoFactorRequest(
        @NotBlank(message = "Código é obrigatório")
        String code
) {
}
