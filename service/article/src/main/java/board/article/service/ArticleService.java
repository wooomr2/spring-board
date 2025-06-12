package board.article.service;

import board.article.dto.request.ArticleCreateRequest;
import board.article.dto.request.ArticleUpdateRequest;
import board.article.dto.response.ArticlePageResponse;
import board.article.dto.response.ArticleResponse;
import board.article.entity.Article;
import board.article.entity.BoardArticleCount;
import board.article.repository.ArticleRepository;
import board.article.repository.BoardArticleCountRepository;
import board.common.event.EventType;
import board.common.event.payload.ArticleCreatedEventPayload;
import board.common.event.payload.ArticleDeletedEventPayload;
import board.common.event.payload.ArticleUpdatedEventPayload;
import board.common.outboxmessagerelay.OutboxEventPublisher;
import board.common.snowflake.Snowflake;
import board.common.util.PageLimitCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final Snowflake snowflake = new Snowflake();
    private final ArticleRepository articleRepository;
    private final BoardArticleCountRepository boardArticleCountRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    @Transactional
    public ArticleResponse create(ArticleCreateRequest request) {
        Article article = articleRepository.save(
                Article.create(
                        snowflake.nextId(),
                        request.getTitle(),
                        request.getContent(),
                        request.getBoardId(),
                        request.getWriterId()
                )
        );
        int result = boardArticleCountRepository.increase(request.getBoardId());
        if (result == 0) {
            boardArticleCountRepository.save(BoardArticleCount.init(request.getBoardId(), 1L));
        }

        outboxEventPublisher.publish(
                EventType.ARTICLE_CREATED,
                ArticleCreatedEventPayload.builder()
                        .articleId(article.getArticleId())
                        .boardId(article.getBoardId())
                        .title(article.getTitle())
                        .content(article.getContent())
                        .writerId(article.getWriterId())
                        .createdAt(article.getCreatedAt())
                        .modifiedAt(article.getModifiedAt())
                        .boardArticleCount(count(article.getBoardId()))
                        .build()
                , article.getShardKey()
        );

        return ArticleResponse.from(article);
    }

    @Transactional
    public ArticleResponse update(Long articleId, ArticleUpdateRequest request) {
        Article article = articleRepository.findById(articleId).orElseThrow();

        article.update(request.getTitle(), request.getContent());

        outboxEventPublisher.publish(
                EventType.ARTICLE_UPDATED,
                ArticleUpdatedEventPayload.builder()
                        .articleId(article.getArticleId())
                        .boardId(article.getBoardId())
                        .title(article.getTitle())
                        .content(article.getContent())
                        .writerId(article.getWriterId())
                        .createdAt(article.getCreatedAt())
                        .modifiedAt(article.getModifiedAt())
                        .build()
                , article.getShardKey()
        );

        return ArticleResponse.from(article);
    }

    @Transactional
    public ArticleResponse read(Long articleId) {
        return ArticleResponse.from(articleRepository.findById(articleId).orElseThrow());
    }

    @Transactional
    public void delete(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow();
        articleRepository.delete(article);
        boardArticleCountRepository.decrease(article.getBoardId());

        outboxEventPublisher.publish(
                EventType.ARTICLE_DELETED,
                ArticleDeletedEventPayload.builder()
                        .articleId(article.getArticleId())
                        .boardId(article.getBoardId())
                        .title(article.getTitle())
                        .content(article.getContent())
                        .writerId(article.getWriterId())
                        .createdAt(article.getCreatedAt())
                        .modifiedAt(article.getModifiedAt())
                        .boardArticleCount(count(article.getBoardId()))
                        .build()
                , article.getShardKey()
        );

    }

    public ArticlePageResponse readAll(Long boardId, Long page, Long pageSize, Long movablePageCount) {

        return ArticlePageResponse.of(
                articleRepository.findAll(
                                boardId,
                                (page - 1) * pageSize,
                                pageSize
                        )
                        .stream().map(ArticleResponse::from).toList()
                ,
                articleRepository.count(
                        boardId,
                        PageLimitCalculator.calculatePageLimit(page, pageSize, movablePageCount)
                )
        );
    }

    public List<ArticleResponse> readAllInfiniteScroll(Long boardId, Long pageSize, Long lastArticleId) {
        List<Article> articleList = lastArticleId == null ?
                articleRepository.findAllInfiniteScroll(boardId, pageSize) :
                articleRepository.findAllInfiniteScroll(boardId, pageSize, lastArticleId);

        return articleList.stream().map(ArticleResponse::from).toList();
    }

    public Long count(Long boardId) {
        return boardArticleCountRepository.findById(boardId)
                .map(BoardArticleCount::getArticleCount)
                .orElse(0L);
    }
}
