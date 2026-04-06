package br.com.libertadfacilities.blog.repositories;

import br.com.libertadfacilities.blog.model.Post;
import br.com.libertadfacilities.blog.model.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<Post> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Post> findByAuthorId(Long authorId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Post> searchPosts(@Param("keyword") String keyword, Pageable pageable);
}
