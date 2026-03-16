package br.com.libertadfacilities.blog.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_comment")
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do autor não pode estar em branco.")
    @Column(nullable = false)
    private String authorName;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O formato do e-mail é inválido.")
    @Column(nullable = false)
    private String authorEmail;

    @NotBlank(message = "O conteúdo do comentário não pode estar vazio.")
    @Size(min = 3,max = 500, message = "O comentário não pode ter mais de 500 caracteres nem menos que 3.")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(updatable = false, insertable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false)
    private Boolean approved = false;
}
