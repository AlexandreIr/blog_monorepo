package br.com.libertadfacilities.blog.mapper;

import br.com.libertadfacilities.blog.dto.request.CreatePostRequest;
import br.com.libertadfacilities.blog.dto.response.CategoryResponse;
import br.com.libertadfacilities.blog.dto.response.PostResponse;
import br.com.libertadfacilities.blog.dto.response.PublicPostDetailResponse;
import br.com.libertadfacilities.blog.dto.response.PublicPostSummaryResponse;
import br.com.libertadfacilities.blog.entity.Category;
import br.com.libertadfacilities.blog.entity.Post;
import br.com.libertadfacilities.blog.entity.User;
import br.com.libertadfacilities.blog.enums.PostStatus;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PostMapper {

    public Post toPost(CreatePostRequest request, User user){
        Post post = new Post();

        post.setTitle(request.title());
        post.setSummary(request.summary().trim());
        post.setContent(request.content().trim());
        post.setCoverImageUrl(request.coverImageUrl());
        post.setMetaTitle(request.metaTitle());
        post.setMetaDescription(request.metaDescription());
        post.setStatus(PostStatus.DRAFT);
        post.setAuthor(user);

        return post;

    }

    public PostResponse toResponse(Post post){
        Set<CategoryResponse> categoryResponses = post.getCategories()
                .stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getSlug()
                )).collect(Collectors.toSet());
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getSlug(),
                post.getSummary(),
                post.getContent(),
                post.getCoverImageUrl(),
                post.getMetaTitle(),
                post.getMetaDescription(),
                post.getStatus(),
                post.getPublishedAt(),
                post.getAuthor().getName(),
                categoryResponses
        );
    }

    public PublicPostSummaryResponse toSummaryResponse(Post post) {
        Set<CategoryResponse> categories = post.getCategories()
                .stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getSlug()
                ))
                .collect(Collectors.toSet());

        return new PublicPostSummaryResponse(
                post.getId(),
                post.getTitle(),
                post.getSlug(),
                post.getSummary(),
                post.getCoverImageUrl(),
                post.getPublishedAt(),
                post.getAuthor().getName(),
                categories
        );
    }

    public PublicPostDetailResponse toDetailResponse(Post post) {
        Set<CategoryResponse> categories = post.getCategories()
                .stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getSlug()
                ))
                .collect(Collectors.toSet());

        return new PublicPostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getSlug(),
                post.getSummary(),
                post.getContent(),
                post.getCoverImageUrl(),
                post.getMetaTitle(),
                post.getMetaDescription(),
                post.getPublishedAt(),
                post.getAuthor().getName(),
                categories
        );
    }
}
