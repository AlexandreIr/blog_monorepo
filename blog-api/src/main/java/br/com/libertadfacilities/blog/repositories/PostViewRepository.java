package br.com.libertadfacilities.blog.repositories;

import br.com.libertadfacilities.blog.entity.PostView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostViewRepository extends JpaRepository<PostView, Long> {
    boolean existsByPostIdAndIpHash(Long postId, String ipHash);
}
