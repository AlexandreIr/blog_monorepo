package br.com.libertadfacilities.blog.dto.response;

import br.com.libertadfacilities.blog.enums.PostStatus;

import java.time.LocalDateTime;
import java.util.Set;

public record PostResponse(
        Long id,
        String title,
        String slug,
        String summary,
        String content,
        String coverImageUrl,
        String metaTitle,
        String metaDescription,
        PostStatus status,
        LocalDateTime publishedAt,
        String authorName,
        Set<String> categories
) {
}
