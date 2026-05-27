package br.com.libertadfacilities.blog.services.publicApi;

import br.com.libertadfacilities.blog.dto.request.CreateCommentRequest;
import br.com.libertadfacilities.blog.dto.response.CommentResponse;
import br.com.libertadfacilities.blog.entity.Comment;
import br.com.libertadfacilities.blog.entity.Post;
import br.com.libertadfacilities.blog.enums.CommentStatus;
import br.com.libertadfacilities.blog.enums.PostStatus;
import br.com.libertadfacilities.blog.exception.ResourceNotFoundException;
import br.com.libertadfacilities.blog.mapper.CommentMapper;
import br.com.libertadfacilities.blog.repositories.CommentRepository;
import br.com.libertadfacilities.blog.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PublicCommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper mapper;

    public CommentResponse create(String postSlug, CreateCommentRequest request){
        Post post = postRepository.findBySlugAndStatus(postSlug, PostStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Post publicado não encontrado"));

        Comment comment = mapper.toComment(request, post);

        return mapper.toResponse(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> listApprovedByPost(String postSlug) {
        Post post = postRepository.findBySlugAndStatus(postSlug, PostStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Post publicado não encontrado"));

        return commentRepository.findByPostIdAndStatusOrderByCreatedAtDesc(post.getId(), CommentStatus.APPROVED)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
