package board.articleread.service.eventhandler;

import board.articleread.repository.ArticleQueryModelRepository;
import board.common.event.Event;
import board.common.event.EventType;
import board.common.event.payload.ArticleUpdatedEventPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleUpdatedEventHandler implements EventHandler<ArticleUpdatedEventPayload> {

    private final ArticleQueryModelRepository articleQueryModelRepository;

    @Override
    public void handle(Event<ArticleUpdatedEventPayload> event) {
        ArticleUpdatedEventPayload payload = event.getPayload();

        articleQueryModelRepository.read(payload.getArticleId())
                .ifPresent(articleQueryModel -> {
                            articleQueryModel.updateBy(payload);
                            articleQueryModelRepository.update(articleQueryModel);
                        }
                );
    }

    @Override
    public boolean supports(Event<ArticleUpdatedEventPayload> event) {
        return event.getType() == EventType.ARTICLE_UPDATED;
    }
}
