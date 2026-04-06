package br.com.libertadfacilities.blog.repositories;

import br.com.libertadfacilities.blog.model.Comment;
import br.com.libertadfacilities.blog.model.enums.CommentStatus;
import br.com.libertadfacilities.blog.model.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdAndStatus(Long postId, CommentStatus status);

    List<Comment> findByPostId(Long postId);


}
