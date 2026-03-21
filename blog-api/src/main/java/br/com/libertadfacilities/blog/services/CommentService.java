package br.com.libertadfacilities.blog.services;

import br.com.libertadfacilities.blog.exception.ResourceNotFoundException;
import br.com.libertadfacilities.blog.model.Comment;
import br.com.libertadfacilities.blog.model.Post;
import br.com.libertadfacilities.blog.repositories.CommentRepository;
import br.com.libertadfacilities.blog.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final EmailService emailService;

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
        return commentRepository.findByPostIdAndApprovedTrue(postId);
    }

    public List<Comment> getPendingComments() {
        return commentRepository.findByApprovedFalse();
    }

    public Comment approveComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário não encontrado."));

        comment.setApproved(true);
        emailService.sendApprovalEmail(comment.getAuthorEmail(), comment.getAuthorName());
        return commentRepository.save(comment);
    }

    public void rejectComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário não encontrado."));
        commentRepository.delete(comment);
        emailService.sendRejectionEmail(comment.getAuthorEmail(), comment.getAuthorName());
    }
}
