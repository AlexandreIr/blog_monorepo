package br.com.libertadfacilities.blog.mapper;

import br.com.libertadfacilities.blog.dto.request.CreateCommentRequest;
import br.com.libertadfacilities.blog.dto.response.CommentResponse;
import br.com.libertadfacilities.blog.entity.Comment;
import br.com.libertadfacilities.blog.entity.Post;
import br.com.libertadfacilities.blog.enums.CommentStatus;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public Comment toComment(CreateCommentRequest request, Post post){
        Comment comment = new Comment();
        comment.setAuthorName(request.authorName().trim());
        comment.setAuthorEmail(request.authorEmail().trim().toLowerCase());
        comment.setContent(request.content().trim());
        comment.setStatus(CommentStatus.PENDING);
        comment.setPost(post);
        return comment;
    }

    public CommentResponse toResponse(Comment comment){
        return new CommentResponse(
                comment.getId(),
                comment.getAuthorName(),
                comment.getAuthorEmail(),
                comment.getContent(),
                comment.getStatus(),
                comment.getPost().getId(),
                comment.getCreatedAt()
        );
    }
}
