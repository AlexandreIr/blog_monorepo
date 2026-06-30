package br.com.libertadfacilities.blog.entity;

import br.com.libertadfacilities.blog.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Entity
@Table(name="tb_users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.EDITOR;

    @Column(nullable = false)
    private boolean twoFactorEnabled = false;

    @Column(length = 120)
    private String twoFactorSecret;

    @Column(length = 120)
    private String twoFactorPendingSecret;
}
