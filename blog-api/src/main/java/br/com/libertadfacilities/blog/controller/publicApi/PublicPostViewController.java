package br.com.libertadfacilities.blog.controller.publicApi;


import br.com.libertadfacilities.blog.services.PostViewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PublicPostViewController {

    private final PostViewService postViewService;

    @PostMapping("/{slug}/views")
    public ResponseEntity<Void> registerView(
            @PathVariable String slug,
            HttpServletRequest request
    ) {
        postViewService.registerView(slug, request);
        return ResponseEntity.noContent().build();
    }
}
