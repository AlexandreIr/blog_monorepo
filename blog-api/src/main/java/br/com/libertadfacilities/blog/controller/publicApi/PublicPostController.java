package br.com.libertadfacilities.blog.controller.publicApi;

import br.com.libertadfacilities.blog.dto.response.PageResponse;
import br.com.libertadfacilities.blog.dto.response.PublicPostDetailResponse;
import br.com.libertadfacilities.blog.dto.response.PublicPostSummaryResponse;
import br.com.libertadfacilities.blog.services.publicApi.PublicPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class PublicPostController {

        private final PublicPostService publicPostService;

        @GetMapping("/posts")
        public ResponseEntity<PageResponse<PublicPostSummaryResponse>> listPublished(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size
        ) {
            return ResponseEntity.ok(publicPostService.listPublished(page, size));
        }

        @GetMapping("/posts/{slug}")
        public ResponseEntity<PublicPostDetailResponse> findBySlug(@PathVariable String slug) {
            return ResponseEntity.ok(publicPostService.findBySlug(slug));
        }

        @GetMapping("/categories/{slug}/posts")
        public ResponseEntity<PageResponse<PublicPostSummaryResponse>> findByCategory(
                @PathVariable String slug,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size
        ) {
            return ResponseEntity.ok(publicPostService.findByCategory(slug, page, size));
        }

        @GetMapping("/posts/search")
        public ResponseEntity<PageResponse<PublicPostSummaryResponse>> search(
                @RequestParam String query,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size
        ) {

            return ResponseEntity.ok(publicPostService.search(query, page, size));
        }
}
