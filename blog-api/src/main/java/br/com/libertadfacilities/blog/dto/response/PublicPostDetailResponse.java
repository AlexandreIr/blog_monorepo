package br.com.libertadfacilities.blog.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public record PublicPostDetailResponse(
        Long id,
        String title,
        String slug,
        String summary,
        String content,
        String coverImageUrl,
        String metaTitle,
        String metaDescription,
        LocalDateTime publishedAt,
        String authorName,
        Set<CategoryResponse> categories
) {
}
