package br.com.libertadfacilities.blog.services;

import br.com.libertadfacilities.blog.exception.BusinessRuleException;
import br.com.libertadfacilities.blog.exception.ResourceNotFoundException;
import br.com.libertadfacilities.blog.model.Comment;
import br.com.libertadfacilities.blog.model.CommentLike;
import br.com.libertadfacilities.blog.model.Post;
import br.com.libertadfacilities.blog.model.enums.CommentStatus;
import br.com.libertadfacilities.blog.repositories.CommentLikeRepository;
import br.com.libertadfacilities.blog.repositories.CommentRepository;
import br.com.libertadfacilities.blog.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final EmailService emailService;

    private final CommentLikeRepository commentLikeRepository;

    public Comment addComment(Long postId, Comment comment, Long parentId){

        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new ResourceNotFoundException("Post não encontrado."));

        comment.setPost(post);

        if(parentId != null){
            Comment parent = commentRepository.findById(parentId)
                    .orElseThrow(()-> new ResourceNotFoundException("Comentário não encontrado."));
            comment.setParentComment(parent);

            emailService.sendReplyEmail(parent.getAuthorEmail(), parent.getAuthorName(), comment.getAuthorName());
        }

        return commentRepository.save(comment);
    }

    public List<Comment> getApprovedCommentsByPost(Long postId) {
        return commentRepository.findByPostIdAndStatus(postId, CommentStatus.APPROVED);
    }

    public List<Comment> getPendingComments(Long postId) {
        return commentRepository.findByPostIdAndStatus(postId, CommentStatus.PENDING);
    }

    public Comment approveComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário não encontrado."));

        comment.setStatus(CommentStatus.APPROVED);
        emailService.sendApprovalEmail(comment.getAuthorEmail(), comment.getAuthorName());
        return commentRepository.save(comment);
    }

    public void rejectComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário não encontrado."));
        commentRepository.delete(comment);
        emailService.sendRejectionEmail(comment.getAuthorEmail(), comment.getAuthorName());
    }

    public @Nullable Comment likeComment(Long commentId, String ipAddress) {

        if(commentLikeRepository.existsByCommentIdAndIpAddress(commentId, ipAddress)){
            throw new BusinessRuleException("Você já curtiu esse comentário.");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new ResourceNotFoundException("Comentário não encontrado com id: "+commentId));

        CommentLike newLike = new CommentLike(ipAddress, comment);
        commentLikeRepository.save(newLike);

        comment.setLikesCount(comment.getLikesCount()+1);

        return commentRepository.save(comment);
    }
}
