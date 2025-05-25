package board.comment.dto.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CommentCreateRequest {

    private Long articleId;
    private String content;
    private Long parentCommentId;
    private Long writerId;
}
