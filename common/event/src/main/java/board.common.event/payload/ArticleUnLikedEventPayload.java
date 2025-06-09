package board.common.event.payload;

import board.common.event.EventPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleUnLikedEventPayload implements EventPayload {

    private Long articleLkieId;
    private Long articleId;
    private Long userId;
    private LocalDateTime createdAt;
    private Long articleLkieCount;
}
