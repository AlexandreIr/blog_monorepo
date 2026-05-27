package br.com.libertadfacilities.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest (
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String name

//        @NotBlank(message = "Descrição é obrigatória")
//        @Size(min = 2, message = "Descrição deve ter no minimo 2 caracteres")
//        String description
){
}
