package br.com.libertadfacilities.blog.services.admin;

import br.com.libertadfacilities.blog.dto.response.CommentResponse;
import br.com.libertadfacilities.blog.dto.response.PageResponse;
import br.com.libertadfacilities.blog.entity.Comment;
import br.com.libertadfacilities.blog.enums.CommentStatus;
import br.com.libertadfacilities.blog.exception.ResourceNotFoundException;
import br.com.libertadfacilities.blog.mapper.CommentMapper;
import br.com.libertadfacilities.blog.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentAdminService {

    private final CommentRepository commentRepository;
    private final CommentMapper mapper;

    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> listPending(int page, int size) {

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 50),
                Sort.by(Sort.Direction.ASC, "createdAt")
        );

        Page<Comment> comments = commentRepository.findByStatus(CommentStatus.PENDING, pageable);

        return new PageResponse<>(
                comments.getContent().stream().map(mapper::toResponse).toList(),
                comments.getNumber(),
                comments.getSize(),
                comments.getTotalElements(),
                comments.getTotalPages(),
                comments.isFirst(),
                comments.isLast()
        );
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
