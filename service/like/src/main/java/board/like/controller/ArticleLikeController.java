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
}
