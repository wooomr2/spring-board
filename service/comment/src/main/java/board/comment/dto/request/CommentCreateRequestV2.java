package board.comment.dto.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CommentCreateRequestV2 {

    private Long articleId;
    private String content;
    private String parentPath;
    private Long writerId;
}
