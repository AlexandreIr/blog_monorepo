package br.com.libertadfacilities.blog.controller;

import br.com.libertadfacilities.blog.dto.PostRequestDTO;
import br.com.libertadfacilities.blog.model.Category;
import br.com.libertadfacilities.blog.model.Comment;
import br.com.libertadfacilities.blog.model.Post;
import br.com.libertadfacilities.blog.services.CategoryService;
import br.com.libertadfacilities.blog.services.CommentService;
import br.com.libertadfacilities.blog.services.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CommentService commentService;
    private final PostService postService;
    private final CategoryService categoryService;

    //posts endpoints
    @PostMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Post> createPost(
            @ModelAttribute PostRequestDTO request
    ){

        String userEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        Post createdPost = postService.createPost(post, userEmail, request.getCategoryId(), request.getFile());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, HttpServletRequest request){
        String userEmail = request.getUserPrincipal().getName();
        postService.deletePost(postId, userEmail);
        return ResponseEntity.noContent().build();
    }

    //comments endpoint
    @GetMapping("/comments/pending")
    public ResponseEntity<List<Comment>> getPendingComments(Long postId) {
        return ResponseEntity.ok(commentService.getPendingComments(postId));
    }

    @PutMapping("/comments/{id}/approve")
    public ResponseEntity<Comment> approveComment(@PathVariable Long id){
        return ResponseEntity.ok(commentService.approveComment(id));
    }

    @DeleteMapping("/comments/{id}/reject")
    public ResponseEntity<Void> rejectComment(@PathVariable Long id){
        commentService.rejectComment(id);
        return ResponseEntity.noContent().build();
    }

    //category admin endpoints
    @PostMapping("/category")
    public ResponseEntity<Category> createCategory(@RequestBody Category category){
        return ResponseEntity.ok(categoryService.createCategory(category));
    }
}
