package board.article.controller;

import board.article.dto.request.ArticleCreateRequest;
import board.article.dto.request.ArticleUpdateRequest;
import board.article.dto.response.ArticlePageResponse;
import board.article.dto.response.ArticleResponse;
import board.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/v1/articles/{articleId}")
    public ArticleResponse read(@PathVariable Long articleId) {
        return articleService.read(articleId);
    }

    @GetMapping("/v1/articles")
    public ArticlePageResponse readAll(
            @RequestParam(required = true) Long boardId,
            @RequestParam(required = false, defaultValue = "0") Long page,
            @RequestParam(required = false, defaultValue = "10") Long pageSize,
            @RequestParam(required = false, defaultValue = "10") Long movablePageCount
    ) {
        return articleService.readAll(boardId, page, pageSize, movablePageCount);
    }

    @GetMapping("/v1/articles/infinite-scroll")
    public List<ArticleResponse> readAllInfiniteScroll(
            @RequestParam Long boardId,
            @RequestParam(required = false, defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Long lastArticleId
    ) {
        return articleService.readAllInfiniteScroll(boardId, pageSize, lastArticleId);
    }

    @PostMapping("/v1/articles")
    public ArticleResponse create(@RequestBody ArticleCreateRequest request) {
        return articleService.create(request);
    }

    @PutMapping("/v1/articles/{articleId}")
    public ArticleResponse update(@PathVariable Long articleId,
                                  @RequestBody ArticleUpdateRequest request) {
        return articleService.update(articleId, request);
    }

    @DeleteMapping("/v1/articles/{articleId}")
    public void delete(@PathVariable Long articleId) {
        articleService.delete(articleId);
    }

    @GetMapping("v1/articles/boards/{boardId}/count")
    public Long count(@PathVariable Long boardId) {
        return articleService.count(boardId);
    }
}
