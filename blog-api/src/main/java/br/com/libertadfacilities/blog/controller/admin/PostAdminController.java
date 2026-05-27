package br.com.libertadfacilities.blog.controller.admin;


import br.com.libertadfacilities.blog.dto.request.CreatePostRequest;
import br.com.libertadfacilities.blog.dto.request.UpdatePostRequest;
import br.com.libertadfacilities.blog.dto.response.PageResponse;
import br.com.libertadfacilities.blog.dto.response.PostResponse;
import br.com.libertadfacilities.blog.entity.User;
import br.com.libertadfacilities.blog.enums.PostStatus;
import br.com.libertadfacilities.blog.exception.ResourceNotFoundException;
import br.com.libertadfacilities.blog.repositories.UserRepository;
import br.com.libertadfacilities.blog.services.admin.PostAdminService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
public class PostAdminController {

    private final UserRepository userRepository;
    private final PostAdminService postAdminService;

    @PostMapping
    public ResponseEntity<PostResponse> create(@RequestBody @Valid CreatePostRequest request){
       return ResponseEntity.status(HttpStatus.CREATED).body(postAdminService.create(request, getAuthenticatedUser()));
    }

    private User getAuthenticatedUser() {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado"));
    }

    @Operation(summary = "Listar posts com paginação e filtros")
    @GetMapping
    public ResponseEntity<PageResponse<PostResponse>> findAll(
            @RequestParam(required = false) PostStatus status,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(postAdminService.findAll(status, query, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(postAdminService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> update(@PathVariable Long id,
                                               @RequestBody @Valid UpdatePostRequest request) {
        return ResponseEntity.ok(postAdminService.update(id, request));
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<PostResponse> publish(@PathVariable Long id) {
        return ResponseEntity.ok(postAdminService.publish(id));
    }

    @PatchMapping("/{id}/unpublish")
    public ResponseEntity<PostResponse> unpublish(@PathVariable Long id) {
        return ResponseEntity.ok(postAdminService.unpublish(id));
    }

}
