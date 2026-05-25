package br.com.libertadfacilities.blog.repositories;

import br.com.libertadfacilities.blog.entity.Comment;
import br.com.libertadfacilities.blog.enums.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdAndStatus(Long postId, CommentStatus status);

    List<Comment> findByPostId(Long postId);


}
