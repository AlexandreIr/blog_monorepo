package br.com.libertadfacilities.blog.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size (max = 120, message = "Nome deve ter no máximo 120 caracteres")
        String authorName,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 150, message = "Email deve ter no máximo 150 caracteres")
        String authorEmail,

        @NotBlank(message = "Comentário é obrigatório")
        @Size(max = 3000, message = "Comentário deve ter no máximo 3000 caracteres")
        String content
) {
}
