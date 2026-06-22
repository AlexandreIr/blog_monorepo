package br.com.libertadfacilities.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreatePostRequest (
        @NotBlank(message = "Título é obrigatório")
        @Size(max = 100, min = 2, message = "Título dever ter entre 2 e 100 caracteres")
        String title,

        @NotBlank(message = "Resumo é obrigatório")
        @Size(max = 300, message = "Resumo deve ter no máximo 300 caracteres")
        String summary,

        @NotBlank(message = "Conteúdo é obrigatório")
        String content,

        @Size(max = 500, message = "URL da imagem deve ter no máximo 500 caracteres")
        String coverImageUrl,



        @Size(max = 255, message = "Meta title deve ter no máximo 255 caracteres")
        String metaTitle,

        @Size(max = 300, message = "Meta description deve ter no máximo 300 caracteres")
        String metaDescription,

        @NotEmpty(message = "Informe pelo menos uma categoria")
        Set<Long> categoryIds

) {
}
