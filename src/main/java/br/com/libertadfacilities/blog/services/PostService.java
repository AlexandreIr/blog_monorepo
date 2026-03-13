package br.com.libertadfacilities.blog.services;

import br.com.libertadfacilities.blog.model.Category;
import br.com.libertadfacilities.blog.model.Post;
import br.com.libertadfacilities.blog.model.User;
import br.com.libertadfacilities.blog.repositories.CategoryRepository;
import br.com.libertadfacilities.blog.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final CategoryRepository categoryRepository;

    public final CloudinaryService cloudinaryService;

    public Post createPost(Post post,
                           Long authorId,
                           Long categoryId,
                           MultipartFile file){

        User author = userService.getUserById(authorId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new RuntimeException("Categoria não encontrada"));

        post.setAuthor(author);
        post.setCategory(category);

        //TODO Cloudinary implementation
        if(file != null && !file.isEmpty()) {
            String mediaUrl = cloudinaryService.uploadFile(file);
            post.setMediaUrl(mediaUrl);
        }

        return postRepository.save(post);
    }

    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }

    public List<Post> getPostsByCategory(Long categoryId) {
        return postRepository.findByCategoryId(categoryId);
    }
}
