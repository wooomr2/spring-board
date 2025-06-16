package board.articleread.service;

import board.articleread.client.ArticleClient;
import board.articleread.client.CommentClient;
import board.articleread.client.LikeClient;
import board.articleread.client.ViewClient;
import board.articleread.repository.ArticleIdListRepository;
import board.articleread.repository.ArticleQueryModel;
import board.articleread.repository.ArticleQueryModelRepository;
import board.articleread.repository.BoardArticleCountRepository;
import board.articleread.response.ArticleReadPageResponse;
import board.articleread.response.ArticleReadResponse;
import board.articleread.service.eventhandler.EventHandler;
import board.common.event.Event;
import board.common.event.EventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleReadService {

    private final ArticleClient articleClient;
    private final CommentClient commentClient;
    private final LikeClient likeClient;
    private final ViewClient viewClient;
    private final ArticleQueryModelRepository articleQueryModelRepository;
    private final ArticleIdListRepository articleIdListRepository;
    private final BoardArticleCountRepository boardArticleCountRepository;
    private static final Duration CACHE_DURATION_TTL = Duration.ofDays(1);
    private final List<EventHandler> eventHandlers;

    public void handleEvent(Event<EventPayload> event) {
        for (EventHandler eventHandler : eventHandlers) {
            if (eventHandler.supports(event)) {
                eventHandler.handle(event);
            }
        }
    }

    public ArticleReadResponse read(Long articleId) {
        ArticleQueryModel articleQueryModel = articleQueryModelRepository.read(articleId)
                .or(() -> _fetch(articleId))
                .orElseThrow();

        return ArticleReadResponse.from(articleQueryModel, viewClient.count(articleId));
    }

    private Optional<ArticleQueryModel> _fetch(Long articleId) {
        Optional<ArticleQueryModel> articleQueryModel = articleClient.read(articleId)
                .map(articleResponse ->
                        ArticleQueryModel.create(articleResponse, commentClient.count(articleId), likeClient.count(articleId))
                );

        articleQueryModel.ifPresent(v ->
                articleQueryModelRepository.create(v, CACHE_DURATION_TTL)
        );

        log.info("[ArticleReadService._fetch] fetchData articleId={}, ifPresent={}", articleId, articleQueryModel.isPresent());

        return articleQueryModel;
    }

    public ArticleReadPageResponse readAll(Long boardId, Long page, Long pageSize) {
        return ArticleReadPageResponse.of(
                _readAll(_readAllArticleIds(boardId, page, pageSize)),
                _count(boardId)
        );
    }

    public List<ArticleReadResponse> readAllInfiniteScroll(Long boardId, Long lastArticleId, Long pageSize) {
        return _readAll(_readAllInfiniteScrollArticleIds(boardId, lastArticleId, pageSize));
    }
    
    private Long _count(Long boardId) {
        Long result = boardArticleCountRepository.read(boardId);
        if (result != null) {
            return result;
        }

        long count = articleClient.count(boardId);
        boardArticleCountRepository.createOrUpdate(boardId, count);
        return count;
    }

    private List<ArticleReadResponse> _readAll(List<Long> articleIds) {
        Map<Long, ArticleQueryModel> articleQueryModelMap = articleQueryModelRepository.readAll(articleIds);

        return articleIds.stream()
                .map(articleId -> articleQueryModelMap.containsKey(articleId) ? articleQueryModelMap.get(articleId) : _fetch(articleId).orElse(null))
                .filter(Objects::nonNull)
                .map(articleQueryModel -> ArticleReadResponse.from(
                        articleQueryModel,
                        viewClient.count(articleQueryModel.getArticleId())
                ))
                .toList();
    }

    private List<Long> _readAllArticleIds(Long boardId, Long page, Long pageSize) {

        List<Long> articleIds = articleIdListRepository.readAll(boardId, (page - 1) * pageSize, pageSize);
        if (pageSize == articleIds.size()) {
            log.info("[ArticleReadService._readAllArticleIds] return redis data");
            return articleIds;
        }

        log.info("[ArticleReadService._readAllArticleIds] return origin data");
        return articleClient.readAll(boardId, page, pageSize)
                .getArticles()
                .stream()
                .map(ArticleClient.ArticleResponse::getArticleId)
                .toList();
    }

    private List<Long> _readAllInfiniteScrollArticleIds(Long boardId, Long lastArticleId, Long pageSize) {
        List<Long> articleIds = articleIdListRepository.readAllInfiniteScroll(boardId, lastArticleId, pageSize);
        if (pageSize == articleIds.size()) {
            log.info("[ArticleReadService._readAllInfiniteScrollArticleIds] return redis data");
            return articleIds;
        }
        log.info("[ArticleReadService._readAllInfiniteScrollArticleIds] return origin data");

        return articleClient.readAllInfiniteScroll(boardId, lastArticleId, pageSize).stream()
                .map(ArticleClient.ArticleResponse::getArticleId)
                .toList();
    }
}
