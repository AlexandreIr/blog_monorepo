package br.com.libertadfacilities.blog.dto.response;

import br.com.libertadfacilities.blog.enums.CommentStatus;

import java.time.LocalDateTime;

public record CommentResponse (
        Long id,
        String authorName,
        String authorEmail,
        String content,
        CommentStatus status,
        Long postId,
        LocalDateTime createdAt
) {
}
