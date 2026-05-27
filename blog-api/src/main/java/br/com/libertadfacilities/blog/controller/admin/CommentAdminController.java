package br.com.libertadfacilities.blog.controller.admin;

import br.com.libertadfacilities.blog.dto.response.CommentResponse;
import br.com.libertadfacilities.blog.services.admin.CommentAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/comments")
@RequiredArgsConstructor
public class CommentAdminController {

    private final CommentAdminService commentAdminService;

    @GetMapping("/pending")
    public ResponseEntity<List<CommentResponse>> listPending() {
        return ResponseEntity.ok(commentAdminService.listPending());
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<CommentResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(commentAdminService.approve(id));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<CommentResponse> reject(@PathVariable Long id) {
        return ResponseEntity.ok(commentAdminService.reject(id));
    }
}
