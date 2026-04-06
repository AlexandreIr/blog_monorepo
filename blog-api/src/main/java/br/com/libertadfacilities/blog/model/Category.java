package br.com.libertadfacilities.blog.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_category")
@Data
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, unique = true, length = 120)
    private String slug;
}
