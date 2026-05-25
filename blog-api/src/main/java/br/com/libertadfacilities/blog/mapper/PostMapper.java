package br.com.libertadfacilities.blog.mapper;

import br.com.libertadfacilities.blog.dto.request.CreatePostRequest;
import br.com.libertadfacilities.blog.dto.response.PostResponse;
import br.com.libertadfacilities.blog.entity.Category;
import br.com.libertadfacilities.blog.entity.Post;
import br.com.libertadfacilities.blog.entity.User;
import br.com.libertadfacilities.blog.enums.PostStatus;

import java.util.Set;
import java.util.stream.Collectors;

public class PostMapper {

    public Post toPost(CreatePostRequest request, User user, Set<Category> categories){
        Post post = new Post();

        post.setTitle(request.title());
        post.setSummary(request.summary().trim());
        post.setContent(request.content().trim());
        post.setCoverImageUrl(request.coverImageUrl());
        post.setMetaTitle(request.metaTitle());
        post.setMetaDescription(request.metaDescription());
        post.setStatus(PostStatus.DRAFT);
        post.setAuthor(user);
        post.setCategories(categories);

        return post;

    }

    public PostResponse toResponse(Post post){
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
                post.getCategories().stream()
                        .map(Category::getName).collect(Collectors.toSet()));
    }
}
