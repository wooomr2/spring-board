package board.hotarticle.service.eventhandler;

import board.common.event.Event;
import board.common.event.EventType;
import board.common.event.payload.ArticleLikedEventPayload;
import board.common.util.TimeCalculator;
import board.hotarticle.repository.ArticleLikeCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleLikedEventHandler implements EventHandler<ArticleLikedEventPayload> {

    private final ArticleLikeCountRepository articleLikeCountRepository;

    @Override
    public void handle(Event<ArticleLikedEventPayload> event) {
        ArticleLikedEventPayload payload = event.getPayload();
        articleLikeCountRepository.createOrUpdate(
                payload.getArticleId(),
                payload.getArticleLkieCount(),
                TimeCalculator.calculateDurationBetweenMidnight()
        );
    }

    @Override
    public boolean supports(Event<ArticleLikedEventPayload> event) {
        return event.getType() == EventType.ARTICLE_LIKED;
    }

    @Override
    public Long findArticleId(Event<ArticleLikedEventPayload> event) {
        return event.getPayload().getArticleId();
    }
}
