package board.like.api;

import board.like.dto.response.ArticleLikeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class ArticleLikeApiTest {
    RestClient restClient = RestClient.create("http://localhost:9002");

    @Test
    void likeAndUnlikeTest() {
        Long articleId = 9999L;

        like(articleId, 1L);
        like(articleId, 2L);
        like(articleId, 3L);

        ArticleLikeResponse response1 = read(articleId, 1L);
        ArticleLikeResponse response2 = read(articleId, 2L);
        ArticleLikeResponse response3 = read(articleId, 3L);

        unlike(articleId, 1L);
        unlike(articleId, 2L);
        unlike(articleId, 3L);
    }

    void like(Long articleId, Long userId) {
        restClient.post()
                .uri("/v1/article-likes/articles/{articleId}/users/{userIds}", articleId, userId)
                .retrieve()
                .toBodilessEntity();
    }

    void unlike(Long articleId, Long userId) {
        restClient.delete()
                .uri("/v1/article-likes/articles/{articleId}/users/{userIds}", articleId, userId)
                .retrieve()
                .toBodilessEntity();
    }

    ArticleLikeResponse read(Long articleId, Long userId) {
        return restClient.get()
                .uri("/v1/article-likes/articles/{articleId}/users/{userIds}", articleId, userId)
                .retrieve()
                .body(ArticleLikeResponse.class);
    }
}
