package br.com.libertadfacilities.blog.controller;

import br.com.libertadfacilities.blog.model.Comment;
import br.com.libertadfacilities.blog.services.CommentService;
import jakarta.servlet.http.HttpServletRequest;
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

    @PutMapping("/{commentId}/like")
    public ResponseEntity<Comment> likeComment(
            @PathVariable Long commentId,
            HttpServletRequest request
    ) {
        String ipAddress = request.getHeader("X-Forwarded-for");

        if(ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        } else {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        return ResponseEntity.ok(commentService.likeComment(commentId, ipAddress));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId){
        commentService.rejectComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
