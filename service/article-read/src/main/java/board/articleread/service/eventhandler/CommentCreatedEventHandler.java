package board.articleread.service.eventhandler;

import board.articleread.repository.ArticleQueryModelRepository;
import board.common.event.Event;
import board.common.event.EventType;
import board.common.event.payload.CommentCreatedEventPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentCreatedEventHandler implements EventHandler<CommentCreatedEventPayload> {

    private final ArticleQueryModelRepository articleQueryModelRepository;

    @Override
    public void handle(Event<CommentCreatedEventPayload> event) {
        CommentCreatedEventPayload payload = event.getPayload();

        articleQueryModelRepository.read(payload.getArticleId())
                .ifPresent(articleQueryModel -> {
                    articleQueryModel.updateBy(payload);
                    articleQueryModelRepository.update(articleQueryModel);
                });
    }

    @Override
    public boolean supports(Event<CommentCreatedEventPayload> event) {
        return event.getType() == EventType.COMMENT_CREATED;
    }
}
