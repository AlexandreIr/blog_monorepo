package br.com.libertadfacilities.blog.services;

import br.com.libertadfacilities.blog.entity.Post;
import br.com.libertadfacilities.blog.entity.PostView;
import br.com.libertadfacilities.blog.enums.PostStatus;
import br.com.libertadfacilities.blog.exception.ResourceNotFoundException;
import br.com.libertadfacilities.blog.repositories.PostRepository;
import br.com.libertadfacilities.blog.repositories.PostViewRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class PostViewService {

    private final PostRepository postRepository;
    private final PostViewRepository postViewRepository;

    private static final String IP_HASH_SALT = "troque-essa-chave-em-producao";

    @Transactional
    public void registerView(String slug, HttpServletRequest request) {
        Post post = postRepository.findBySlugAndStatus(slug, PostStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Post publicado não encontrado"));

        String ip = extractClientIp(request);
        String ipHash = hashIp(ip);

        boolean alreadyViewed = postViewRepository.existsByPostIdAndIpHash(post.getId(), ipHash);

        if (alreadyViewed) {
            return;
        }

        PostView view = new PostView();
        view.setPost(post);
        view.setIpHash(ipHash);

        postViewRepository.save(view);

        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");

        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        return request.getRemoteAddr();
    }

    private String hashIp(String ip) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((IP_HASH_SALT + ip).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar hash do IP", e);
        }
    }
}
