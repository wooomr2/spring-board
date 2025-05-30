package board.article.service;

import board.article.dto.request.ArticleCreateRequest;
import board.article.dto.request.ArticleUpdateRequest;
import board.article.dto.response.ArticlePageResponse;
import board.article.dto.response.ArticleResponse;
import board.article.entity.Article;
import board.article.repository.ArticleRepository;
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

    @Transactional
    public ArticleResponse create(ArticleCreateRequest request) {
        Article article = articleRepository.save(
                Article.create(
                        snowflake.nextId(),
                        request.getTitle(),
                        request.getContent(),
                        request.getWriterId(),
                        request.getBoardId()
                )
        );

        return ArticleResponse.from(article);
    }

    @Transactional
    public ArticleResponse update(Long articleId, ArticleUpdateRequest request) {
        Article article = articleRepository.findById(articleId).orElseThrow();

        article.update(request.getTitle(), request.getContent());

        return ArticleResponse.from(article);
    }

    @Transactional
    public ArticleResponse read(Long articleId) {
        return ArticleResponse.from(articleRepository.findById(articleId).orElseThrow());
    }

    @Transactional
    public void delete(Long articleId) {
        articleRepository.deleteById(articleId);
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
}
