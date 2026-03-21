package br.com.libertadfacilities.blog.controller;

import br.com.libertadfacilities.blog.model.Comment;
import br.com.libertadfacilities.blog.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;


    @PostMapping
    public ResponseEntity<Comment> addComment(
            @RequestParam(required = false) Long parentId,
            @PathVariable Long postId,
            @RequestBody Comment comment) {

        Comment newComment = commentService.addComment(postId, comment, parentId);
        return ResponseEntity.ok(newComment);
    }


    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getApprovedCommentsByPost(postId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId){
        commentService.rejectComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
