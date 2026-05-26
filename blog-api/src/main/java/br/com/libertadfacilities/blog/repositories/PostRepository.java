package br.com.libertadfacilities.blog.repositories;

import br.com.libertadfacilities.blog.entity.Post;
import br.com.libertadfacilities.blog.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<Post> findByStatus(PostStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "categories"})
    Optional<Post> findBySlugAndStatus(String slug, PostStatus status);

    @EntityGraph(attributePaths = {"author", "categories"})
    Page<Post> findAllByStatus(PostStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "categories"})
    @Query("""
           SELECT DISTINCT p
           FROM Post p
           JOIN p.categories c
           WHERE p.status = :status
             AND c.slug = :categorySlug
           """)
    Page<Post> findPublishedByCategorySlug(@Param("categorySlug") String categorySlug,
                                           @Param("status") PostStatus status,
                                           Pageable pageable);

    @EntityGraph(attributePaths = {"author", "categories"})
    @Query("""
           SELECT DISTINCT p
           FROM Post p
           WHERE p.status = :status
             AND (
                 LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%'))
                 OR LOWER(p.summary) LIKE LOWER(CONCAT('%', :query, '%'))
                 OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))
             )
           """)
    Page<Post> searchPublished(@Param("query") String query,
                               @Param("status") PostStatus status,
                               Pageable pageable);
}
