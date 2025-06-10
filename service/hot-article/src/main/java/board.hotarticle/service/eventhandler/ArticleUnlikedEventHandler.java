package board.hotarticle.service.eventhandler;

import board.common.event.Event;
import board.common.event.EventType;
import board.common.event.payload.ArticleUnLikedEventPayload;
import board.common.util.TimeCalculator;
import board.hotarticle.repository.ArticleLikeCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleUnlikedEventHandler implements EventHandler<ArticleUnLikedEventPayload> {

    private final ArticleLikeCountRepository articleLikeCountRepository;

    @Override
    public void handle(Event<ArticleUnLikedEventPayload> event) {
        ArticleUnLikedEventPayload payload = event.getPayload();
        articleLikeCountRepository.createOrUpdate(
                payload.getArticleId(),
                payload.getArticleLkieCount(),
                TimeCalculator.calculateDurationBetweenMidnight()
        );
    }

    @Override
    public boolean supports(Event<ArticleUnLikedEventPayload> event) {
        return event.getType() == EventType.ARTICLE_UNLIKED;
    }

    @Override
    public Long findArticleId(Event<ArticleUnLikedEventPayload> event) {
        return event.getPayload().getArticleId();
    }
}
