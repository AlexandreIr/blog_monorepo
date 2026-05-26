package br.com.libertadfacilities.blog.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public record PublicPostSummaryResponse(
        Long id,
        String title,
        String slug,
        String summary,
        String coverImageUrl,
        LocalDateTime publishedAt,
        String authorName,
        Set<CategoryResponse> categories
) {
}
