package br.com.libertadfacilities.blog.controller;

import br.com.libertadfacilities.blog.model.Comment;
import br.com.libertadfacilities.blog.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/comments")
@RequiredArgsConstructor
public class AdminController {

    private final CommentService commentService;

    @GetMapping("/pending")
    public ResponseEntity<List<Comment>> getPendingComments(Long postId) {
        return ResponseEntity.ok(commentService.getPendingComments(postId));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Comment> approveComment(@PathVariable Long id){
        return ResponseEntity.ok(commentService.approveComment(id));
    }

    @DeleteMapping("/{id}/reject")
    public ResponseEntity<Void> rejectComment(@PathVariable Long id){
        commentService.rejectComment(id);
        return ResponseEntity.noContent().build();
    }
}
