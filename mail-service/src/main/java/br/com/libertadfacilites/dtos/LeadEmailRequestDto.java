package br.com.libertadfacilites.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LeadEmailRequestDto {

    @NotBlank(message = "Nome do cliente é obrigatório")
    private String nomeCliente;

    @NotBlank(message = "Nome da empresa é obrigatório")
    private String nomeEmpresa;

    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(
        regexp = "^(\\d{14}|\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2})$",
        message = "CNPJ inválido"
    )
    private String cnpj;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotEmpty(message = "Pelo menos um tipo de serviço é obrigatório")
    private List<String> tiposServicos = new ArrayList<>();

    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser maior que zero")
    private Integer quantidade;

    @NotBlank(message = "Função na empresa é obrigatória")
    private String funcaoNaEmpresa;

    // Opcionais
    @Positive(message = "Colaboradores necessários deve ser positivo")
    private Integer colaboradoresNecessarios;

    @PositiveOrZero(message = "Quantidade de colaboradores do lead deve ser zero ou positivo")
    private Integer quantidadeColaboradoresDoLead;

    private String mensagemPersonalizada;

    private MultipartFile arquivo;

    // Suporte a envio de tiposServicos como CSV em um único campo
    public void setTiposServicos(List<String> tiposServicos) {
        this.tiposServicos = new ArrayList<>();
        if (tiposServicos == null) return;

        if (tiposServicos.size() == 1 && tiposServicos.get(0) != null && tiposServicos.get(0).contains(",")) {
            String[] split = tiposServicos.get(0).split("\\s*,\\s*");
            this.tiposServicos.addAll(Arrays.asList(split));
        } else {
            for (String s : tiposServicos) {
                if (s != null && !s.isBlank()) {
                    this.tiposServicos.add(s.trim());
                }
            }
        }
    }
}
