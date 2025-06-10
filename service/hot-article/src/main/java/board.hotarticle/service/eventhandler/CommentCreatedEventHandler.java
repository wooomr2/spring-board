package board.hotarticle.service.eventhandler;

import board.common.event.Event;
import board.common.event.EventType;
import board.common.event.payload.CommentCreatedEventPayload;
import board.common.util.TimeCalculator;
import board.hotarticle.repository.ArticleCommentCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentCreatedEventHandler implements EventHandler<CommentCreatedEventPayload> {

    private final ArticleCommentCountRepository articleCommentCountRepository;

    @Override
    public void handle(Event<CommentCreatedEventPayload> event) {
        CommentCreatedEventPayload payload = event.getPayload();
        articleCommentCountRepository.createOrUpdate(
                payload.getArticleId(),
                payload.getArticleCommentCount(),
                TimeCalculator.calculateDurationBetweenMidnight()
        );
    }

    @Override
    public boolean supports(Event<CommentCreatedEventPayload> event) {
        return event.getType() == EventType.COMMENT_CREATED;
    }

    @Override
    public Long findArticleId(Event<CommentCreatedEventPayload> event) {
        return event.getPayload().getArticleId();
    }
}
