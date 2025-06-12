package board.article.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "board_article_count")
@Entity
@Getter
@ToString
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class BoardArticleCount {

    @Id
    private Long boardId; // shardKey

    private Long articleCount;

    public static BoardArticleCount init(Long boardId, Long articleCount) {
        BoardArticleCount boardArticleCount = new BoardArticleCount();
        boardArticleCount.boardId = boardId;
        boardArticleCount.articleCount = articleCount;
        return boardArticleCount;
    }

    public Long getShardKey() {
        return boardId;
    }
}
