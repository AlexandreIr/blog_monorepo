package br.com.libertadfacilites.dtos;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OuvidoriaRequest(

        @NotBlank(message = "Selecione o tipo de manifestação.")
        String tipoManifestacao,

        @NotBlank(message = "Informe o tipo de identificação.")
        String identificacao,

        @Size(max = 160, message = "O nome deve ter no máximo 160 caracteres.")
        String nome,

        @Email(message = "Informe um e-mail válido.")
        @Size(max = 180, message = "O e-mail deve ter no máximo 180 caracteres.")
        String email,

        @Size(max = 40, message = "O telefone deve ter no máximo 40 caracteres.")
        String telefone,

        @Size(max = 80, message = "O vínculo deve ter no máximo 80 caracteres.")
        String vinculo,

        @Size(max = 160, message = "O departamento deve ter no máximo 160 caracteres.")
        String departamento,

        @NotBlank(message = "Informe o assunto.")
        @Size(min = 3, max = 180, message = "O assunto deve ter entre 3 e 180 caracteres.")
        String assunto,

        @NotBlank(message = "Descreva a manifestação.")
        @Size(min = 20, max = 5000, message = "A descrição deve ter entre 20 e 5000 caracteres.")
        String descricao,

        String dataOcorrencia,

        @Size(max = 180, message = "O local da ocorrência deve ter no máximo 180 caracteres.")
        String localOcorrencia,

        @Size(max = 3000, message = "Os envolvidos devem ter no máximo 3000 caracteres.")
        String envolvidos,

        @Size(max = 3000, message = "As evidências devem ter no máximo 3000 caracteres.")
        String evidencias,

        String source,

        @NotNull(message = "Confirme o envio da manifestação.")
        @AssertTrue(message = "Você precisa confirmar o envio da manifestação.")
        Boolean aceite
) {
}
