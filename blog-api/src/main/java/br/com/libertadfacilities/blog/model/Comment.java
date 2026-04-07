package br.com.libertadfacilities.blog.model;


import br.com.libertadfacilities.blog.model.enums.CommentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_comment")
@Getter
@Setter
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do autor não pode estar em branco.")
    @Column(nullable = false, length = 100)
    private String authorName;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O formato do e-mail é inválido.")
    @Column(nullable = false, length = 100)
    private String authorEmail;

    @NotBlank(message = "O conteúdo do comentário não pode estar vazio.")
    @Size(min = 3,max = 500, message = "O comentário não pode ter mais de 500 caracteres nem menos que 3.")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(updatable = false, insertable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private CommentStatus status = CommentStatus.PENDING;

    @Column(nullable = false)
    private int likesCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parentComment;
}
