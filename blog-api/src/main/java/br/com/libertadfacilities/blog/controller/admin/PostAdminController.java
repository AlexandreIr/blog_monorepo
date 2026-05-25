package br.com.libertadfacilities.blog.controller.admin;


import br.com.libertadfacilities.blog.dto.request.CreatePostRequest;
import br.com.libertadfacilities.blog.dto.response.PostResponse;
import br.com.libertadfacilities.blog.entity.Category;
import br.com.libertadfacilities.blog.entity.Post;
import br.com.libertadfacilities.blog.mapper.PostMapper;
import br.com.libertadfacilities.blog.repositories.CategoryRepository;
import br.com.libertadfacilities.blog.repositories.PostRepository;
import br.com.libertadfacilities.blog.repositories.UserRepository;
import br.com.libertadfacilities.blog.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostAdminController {

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final UserRepository userRepository;
    private final PostMapper mapper

    public PostResponse create(CreatePostRequest request){
        Set<Category> categories = categoryService.resolveCategories(request.categoryIds());
        Post post = mapper.toPost(request, getAuthenticatedUser(), categories);

        Post saved = postRepository.save(post);
        return toResponse(saved);

    }
}
