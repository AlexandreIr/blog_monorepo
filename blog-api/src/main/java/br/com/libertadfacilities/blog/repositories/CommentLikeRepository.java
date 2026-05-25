package br.com.libertadfacilities.blog.repositories;

import br.com.libertadfacilities.blog.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    boolean existsByCommentIdAndIpAddress(Long commentId, String ipAddress);
}
