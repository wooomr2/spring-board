package board.comment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "article_comment_count")
@Entity
@ToString
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ArticleCommentCount {

    @Id
    private Long articleId; // shardKey

    private Long commentCount;

    public static ArticleCommentCount init(Long articleId, Long commentCount) {
        ArticleCommentCount artilceCommentCount = new ArticleCommentCount();
        artilceCommentCount.articleId = articleId;
        artilceCommentCount.commentCount = commentCount;
        return artilceCommentCount;
    }
}
