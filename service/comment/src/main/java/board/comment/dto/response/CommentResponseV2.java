package board.comment.dto.response;

import board.comment.entity.CommentV2;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class CommentResponseV2 {

    private Long commentId;
    private String content;
    private Long articleId; // shard key
    private Long writerId;

    private String path;

    private Boolean deleted;
    private LocalDateTime createdAt;

    public static CommentResponseV2 from(CommentV2 comment) {
        CommentResponseV2 response = new CommentResponseV2();
        response.commentId = comment.getCommentId();
        response.content = comment.getContent();
        response.path = comment.getCommentPath().getPath();
        response.articleId = comment.getArticleId();
        response.writerId = comment.getWriterId();
        response.deleted = comment.getDeleted();
        response.createdAt = comment.getCreatedAt();
        return response;
    }
}
