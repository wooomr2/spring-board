package board.like.controller;

import board.like.dto.response.ArticleLikeResponse;
import board.like.service.ArticleLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ArticleLikeController {

    private final ArticleLikeService articleLikeService;

    @GetMapping("/v1/article-likes/articles/{articleId}/users/{userId}")
    public ArticleLikeResponse read(
            @PathVariable Long articleId,
            @PathVariable Long userId
    ) {
        return articleLikeService.read(articleId, userId);
    }

    @PostMapping("v1/article-likes/articles/{articleId}/users/{userId}")
    public void like(
            @PathVariable Long articleId,
            @PathVariable Long userId
    ) {
        articleLikeService.like(articleId, userId);
    }

    @DeleteMapping("v1/article-likes/articles/{articleId}/users/{userId}")
    public void unlike(
            @PathVariable Long articleId,
            @PathVariable Long userId
    ) {
        articleLikeService.unlike(articleId, userId);
    }

    /**
     * 비관적 락 1
     */
    @PostMapping("v1/article-likes/articles/{articleId}/users/{userId}/pessimistic-lock-1")
    public void likePessimisticLock1(
            @PathVariable Long articleId,
            @PathVariable Long userId
    ) {
        articleLikeService.likePessimisticLock1(articleId, userId);
    }

    @DeleteMapping("v1/article-likes/articles/{articleId}/users/{userId/pessimistic-lock-1}")
    public void unlikePessimisticLock1(
            @PathVariable Long articleId,
            @PathVariable Long userId
    ) {
        articleLikeService.unlikePessimisticLock1(articleId, userId);
    }

    /**
     * 비관적 락 2
     */
    @PostMapping("v1/article-likes/articles/{articleId}/users/{userId}/pessimistic-lock-2")
    public void likePessimisticLock2(
            @PathVariable Long articleId,
            @PathVariable Long userId
    ) {
        articleLikeService.likePessimisticLock2(articleId, userId);
    }

    @DeleteMapping("v1/article-likes/articles/{articleId}/users/{userId/pessimistic-lock-2}")
    public void unlikePessimisticLock2(
            @PathVariable Long articleId,
            @PathVariable Long userId
    ) {
        articleLikeService.unlikePessimisticLock2(articleId, userId);
    }

    /**
     * 낙관적 락
     */
    @PostMapping("v1/article-likes/articles/{articleId}/users/{userId}/optimistic-lock")
    public void likeOptimisticLock(
            @PathVariable Long articleId,
            @PathVariable Long userId
    ) {
        articleLikeService.likeOptimisticLock(articleId, userId);
    }

    @DeleteMapping("v1/article-likes/articles/{articleId}/users/{userId/optimistic-lock}")
    public void unlikeOptimisticLock(
            @PathVariable Long articleId,
            @PathVariable Long userId
    ) {
        articleLikeService.unlikeOptimisticLock(articleId, userId);
    }
}
