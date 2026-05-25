package br.com.libertadfacilities.blog.controller.publicApi.comments;

import br.com.libertadfacilities.blog.entity.Comment;
import br.com.libertadfacilities.blog.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts/{slug}/comments")
@RequiredArgsConstructor
public class PublicCommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> addComment(
            @RequestParam(required = false) Long parentId,
            @PathVariable String slug,
            @RequestBody Comment comment) {

        Comment newComment = commentService.addComment(slug, comment, parentId);
        return ResponseEntity.ok(newComment);
    }


    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getApprovedCommentsByPost(postId));
    }
}
