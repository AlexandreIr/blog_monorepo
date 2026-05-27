package br.com.libertadfacilities.blog.services.admin;

import br.com.libertadfacilities.blog.dto.response.CommentResponse;
import br.com.libertadfacilities.blog.entity.Comment;
import br.com.libertadfacilities.blog.enums.CommentStatus;
import br.com.libertadfacilities.blog.exception.ResourceNotFoundException;
import br.com.libertadfacilities.blog.mapper.CommentMapper;
import br.com.libertadfacilities.blog.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentAdminService {

    private final CommentRepository commentRepository;
    private final CommentMapper mapper;

    @Transactional(readOnly = true)
    public List<CommentResponse> listPending() {
        return commentRepository.findByStatusOrderByCreatedAtAsc(CommentStatus.PENDING)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public CommentResponse approve(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário não encontrado"));

        if (comment.getStatus() == CommentStatus.APPROVED) {
            return mapper.toResponse(comment);
        }

        comment.setStatus(CommentStatus.APPROVED);
        Comment updated = commentRepository.save(comment);

        return mapper.toResponse(updated);
    }

    public CommentResponse reject(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário não encontrado"));

        if (comment.getStatus() == CommentStatus.REJECTED) {
            return mapper.toResponse(comment);
        }

        comment.setStatus(CommentStatus.REJECTED);
        Comment updated = commentRepository.save(comment);

        return mapper.toResponse(updated);
    }
}
