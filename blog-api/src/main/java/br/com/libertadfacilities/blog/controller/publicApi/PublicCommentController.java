package br.com.libertadfacilities.blog.controller.publicApi;

import br.com.libertadfacilities.blog.dto.request.CreateCommentRequest;
import br.com.libertadfacilities.blog.dto.response.CommentResponse;
import br.com.libertadfacilities.blog.services.publicApi.PublicCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("posts/{slug}/comments")
@RequiredArgsConstructor
public class PublicCommentController {

    private final PublicCommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> create(@PathVariable String slug,
                                                  @RequestBody @Valid CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.create(slug, request));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> listApproved(@PathVariable String slug) {
        return ResponseEntity.ok(commentService.listApprovedByPost(slug));
    }

}
