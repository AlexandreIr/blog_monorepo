package br.com.libertadfacilities.blog.services.admin;

import br.com.libertadfacilities.blog.dto.request.CreatePostRequest;
import br.com.libertadfacilities.blog.dto.request.UpdatePostRequest;
import br.com.libertadfacilities.blog.dto.response.PageResponse;
import br.com.libertadfacilities.blog.dto.response.PostResponse;
import br.com.libertadfacilities.blog.entity.Category;
import br.com.libertadfacilities.blog.entity.Post;
import br.com.libertadfacilities.blog.entity.User;
import br.com.libertadfacilities.blog.enums.PostStatus;
import br.com.libertadfacilities.blog.exception.BusinessRuleException;
import br.com.libertadfacilities.blog.exception.ResourceNotFoundException;
import br.com.libertadfacilities.blog.mapper.PostMapper;
import br.com.libertadfacilities.blog.repositories.CategoryRepository;
import br.com.libertadfacilities.blog.repositories.PostRepository;
import br.com.libertadfacilities.blog.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostAdminService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final PostMapper mapper;

    public PostResponse create(CreatePostRequest request, User authenticatedUser){

        String slug = generateUniqueSlug(request.title());

        Post post = mapper.toPost(request, authenticatedUser);
        post.setSlug(slug);

        Set<Category> categories = resolveCategories(request.categoryIds());

        post.setCategories(categories);
        return mapper.toResponse(postRepository.save(post));

    }

    @Transactional(readOnly = true)
    public PageResponse<PostResponse> findAll(PostStatus status, String query, int page, int size){
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size,1),50),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        String normalizeQuery = query==null?null:query.trim();

        Page<Post> posts = postRepository.findAdminPosts(status, normalizeQuery, pageable);

        return new PageResponse<>(
                posts.getContent().stream().map(mapper::toResponse).toList(),
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalElements(),
                posts.getTotalPages(),
                posts.isFirst(),
                posts.isLast()
        );
    }

    public PostResponse findById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post não encontrado"));

        return mapper.toResponse(post);
    }

    public PostResponse update(Long id, UpdatePostRequest request){
        Post post = postRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Post não encontrado"));

        if(!post.getTitle().equalsIgnoreCase(request.title().trim())){
            post.setSlug(generateUniqueSlug(request.title()));
        }

        post.setTitle(request.title().trim());
        post.setSummary(request.summary().trim());
        post.setContent(request.content().trim());
        post.setCoverImageUrl(request.coverImageUrl());
        post.setMetaTitle(request.metaTitle());
        post.setMetaDescription(request.metaDescription());
        post.setCategories(resolveCategories(request.categoryIds()));

        Post updated = postRepository.save(post);
        return mapper.toResponse(updated);
    }

    public PostResponse publish(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post não encontrado"));

        post.setStatus(PostStatus.PUBLISHED);

        if (post.getPublishedAt() == null) {
            post.setPublishedAt(LocalDateTime.now());
        }

        Post updated = postRepository.save(post);
        return mapper.toResponse(updated);
    }

    public PostResponse unpublish(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post não encontrado"));

        post.setStatus(PostStatus.DRAFT);
        Post updated = postRepository.save(post);
        return mapper.toResponse(updated);
    }

    private String generateUniqueSlug(String title){
        String baseSlug = SlugUtil.toSlug(title);
        String slug = baseSlug;
        int counter = 1;

        while(postRepository.existsBySlug(slug)){
            slug = baseSlug + "-"+ counter;
            counter++;
        }
        return slug;
    }

    public Set<Category> resolveCategories(Set<Long> categoryIds) {
        Set<Category> categories = categoryIds.stream()
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + id)))
                .collect(Collectors.toSet());

        if (categories.isEmpty()) {
            throw new BusinessRuleException("Informe pelo menos uma categoria");
        }

        return categories;
    }
}
