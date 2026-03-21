package br.com.libertadfacilities.blog.services;

import br.com.libertadfacilities.blog.exception.BusinessRuleException;
import br.com.libertadfacilities.blog.exception.ResourceNotFoundException;
import br.com.libertadfacilities.blog.model.Category;
import br.com.libertadfacilities.blog.model.Post;
import br.com.libertadfacilities.blog.model.User;
import br.com.libertadfacilities.blog.repositories.CategoryRepository;
import br.com.libertadfacilities.blog.repositories.PostRepository;
import br.com.libertadfacilities.blog.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public final CloudinaryService cloudinaryService;

    public Post createPost(Post post,
                           String email,
                           Long categoryId,
                           MultipartFile file){

        User author = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Usuário não encontrado"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Categoria não encontrada"));

        post.setAuthor(author);
        post.setCategory(category);

        if(file != null && !file.isEmpty()) {
            String mediaUrl = cloudinaryService.uploadFile(file);
            post.setMediaUrl(mediaUrl);
        }

        return postRepository.save(post);
    }

    public Page<Post> getAllPosts(int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findAll(pageable);
    }

    public Page<Post> getPostsByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByCategoryId(categoryId, pageable);
    }

    public void deletePost(Long postId, String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Usuário não encontrado."));
        Post post = postRepository.findById(postId)
                        .orElseThrow(()-> new ResourceNotFoundException("Post não encontrado."));
        if(!post.getAuthor().equals(user)) {
            throw new BusinessRuleException("Usuário não pode apagar post de outro usuário.");
        }
        postRepository.deleteById(postId);
    }

    public Page<Post> searchPosts(String keyword, int page, int size,
                                  String sortBy, String sortDirection){
        Sort.Direction direction = sortDirection.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        if(keyword == null || keyword.trim().isBlank()) {
            return postRepository.findAll(pageable);
        }

        return postRepository.searchPosts(keyword, pageable);
    }
}
