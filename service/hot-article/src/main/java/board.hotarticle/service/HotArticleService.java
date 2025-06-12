package board.hotarticle.service;

import board.common.event.Event;
import board.common.event.EventPayload;
import board.common.event.EventType;
import board.hotarticle.client.ArticleClient;
import board.hotarticle.repository.HotArticleListRepository;
import board.hotarticle.response.HotArticleResponse;
import board.hotarticle.service.eventhandler.EventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotArticleService {

    private final ArticleClient articleClient;
    private final List<EventHandler> eventHandlers;
    private final HotArticleScoreUpdater hotArticleScoreUpdater;
    private final HotArticleScoreCalculator hotArticleScoreCalculator;
    private final HotArticleListRepository hotArticleListRepository;

    public void handleEvent(Event<EventPayload> event) {
        log.info("[HotArticleService.handleEvent] Handling event for hot articles event={}", event);

        EventHandler<EventPayload> eventHandler = _findEventHandler(event);
        if (eventHandler == null) {
            log.warn("[HotArticleService.handleEvent] No handler found for event={}", event);
            return;
        }

        if (_isArticleCreateOrDeleted(event)) {
            eventHandler.handle(event);
        } else {
            hotArticleScoreUpdater.update(event, eventHandler);
        }
    }

    private EventHandler<EventPayload> _findEventHandler(Event<EventPayload> event) {
        return eventHandlers.stream()
                .filter(handler -> handler.supports(event))
                .findAny()
                .orElse(null);
    }

    private boolean _isArticleCreateOrDeleted(Event<EventPayload> event) {
        return event.getType() == EventType.ARTICLE_CREATED ||
                event.getType() == EventType.ARTICLE_DELETED;
    }

    /**
     * yyyyMMdd 형식의 날짜 문자열을 받아 해당 날짜의 핫 아티클 목록을 반환
     */
    public List<HotArticleResponse> readAll(String dateStr) {
        return hotArticleListRepository.readAll(dateStr).stream()
                .map(articleClient::read)
                .filter(Objects::nonNull)
                .map(HotArticleResponse::from)
                .toList();
    }
}
