package board.hotarticle.service;

import board.common.event.Event;
import board.common.event.EventPayload;
import board.hotarticle.repository.ArticleCreatedTimeRepository;
import board.hotarticle.repository.HotArticleListRepository;
import board.hotarticle.service.eventhandler.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class HotArticleScoreUpdater {

    private final HotArticleListRepository hotArticleListRepository;
    private final HotArticleScoreCalculator hotArticleScoreCalculator;
    private final ArticleCreatedTimeRepository articleCreatedTimeRepository;

    private static final long HOT_ARTILCE_COUNT_LIMIT = 10;
    private static final Duration HOT_ARTICLE_TTL = Duration.ofDays(10);

    public void update(Event<EventPayload> event, EventHandler<EventPayload> eventHandler) {

        Long articleId = eventHandler.findArticleId(event);
        LocalDateTime createdTime = articleCreatedTimeRepository.read(articleId);

        if (!_isArticleCreatedToday(createdTime)) {
            return;
        }

        eventHandler.handle(event);

        long score = hotArticleScoreCalculator.calculate(articleId);
        hotArticleListRepository.add(
                articleId,
                createdTime,
                score,
                HOT_ARTILCE_COUNT_LIMIT,
                HOT_ARTICLE_TTL
        );
    }

    private boolean _isArticleCreatedToday(LocalDateTime createdTime) {
        return createdTime != null && createdTime.toLocalDate().equals(LocalDate.now());
    }
}
