package br.com.libertadfacilities.blog.controller;

import br.com.libertadfacilities.blog.dto.PostRequestDTO;
import br.com.libertadfacilities.blog.model.Post;
import br.com.libertadfacilities.blog.services.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;


    @PostMapping
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

    @GetMapping
    public ResponseEntity<Page<Post>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(postService.getAllPosts(page, size));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<Post>> getPostsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(postService.getPostsByCategory(categoryId, page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Post>> searchPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ){
        return ResponseEntity.ok(postService.searchPosts(keyword, page, size, sortBy, sortDirection));
    }

    @DeleteMapping("{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, HttpServletRequest request){
        String userEmail = request.getUserPrincipal().getName();
        postService.deletePost(postId, userEmail);
        return ResponseEntity.noContent().build();
    }
}
