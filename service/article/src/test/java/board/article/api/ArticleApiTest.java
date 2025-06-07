package board.article.api;

import board.article.dto.response.ArticlePageResponse;
import board.article.dto.response.ArticleResponse;
import board.article.entity.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

public class ArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9000");

    @Getter
    @AllArgsConstructor
    static class ArticleCreateRequest {
        private String title;
        private String content;
        private Long writerId;
        private Long boardId;
    }

    @Getter
    @AllArgsConstructor
    static class ArticleUpdateRequest {
        private String title;
        private String content;
    }

    @Test
    void createTest() {
        ArticleResponse response = create(new ArticleCreateRequest(
                "hi", "my content", 1L, 1L
        ));
        System.out.println("response = " + response);
    }

    ArticleResponse create(ArticleCreateRequest request) {
        return restClient.post()
                .uri("/v1/articles")
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void readTest() {
        ArticleResponse response = read(184800327881080832L);
        System.out.println("response = " + response);
    }

    ArticleResponse read(Long articleId) {
        return restClient.get()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void updateTest() {
        ArticleResponse response = update(184800327881080832L, new ArticleUpdateRequest(
                "updated title", "updated content"
        ));
        System.out.println("response = " + response);
    }

    ArticleResponse update(Long articleId, ArticleUpdateRequest request) {
        return restClient.put()
                .uri("/v1/articles/{articleId}", articleId)
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void deleteTest() {
        restClient.delete()
                .uri("/v1/articles/{articleId}", 184800327881080832L)
                .retrieve()
                .body(Void.class);
        System.out.println("Article deleted successfully.");
    }

    @Test
    void readAllTest() {
        ArticlePageResponse response = restClient.get()
                .uri("/v1/articles?boardId=1&pageSize=30&page=50000")
                .retrieve()
                .body(ArticlePageResponse.class);

        System.out.println("count = " + Objects.requireNonNull(response).getArticleCount());
        for (ArticleResponse article : response.getArticles()) {
            System.out.println("article = " + article);
        }
    }

    @Test
    void readAllInfiniteScrollTest() {
        List<Article> articles = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<Article>>() {
                });

        for (Article article : articles) {
            System.out.println("article = " + article);
        }

        long lastArticleId = articles.getLast().getArticleId();
        System.out.println("last article id = " + lastArticleId);

        List<Article> nextArticles = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId={lastArticleId}", lastArticleId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Article>>() {
                });

        for (Article article : nextArticles) {
            System.out.println("last article = " + article);
        }
    }

    @Test
    void countTest() {
        ArticleResponse response = create(new ArticleCreateRequest(
                "count test", "content for count test", 1L, 2L
        ));

        Long count = restClient.get()
                .uri("/v1/articles/boards/{boardId}/count", response.getBoardId())
                .retrieve()
                .body(Long.class);

        System.out.println("count = " + count);

        restClient.delete()
                .uri("v1/articles/{articleId}", response.getArticleId())
                .retrieve()
                .toBodilessEntity();

        Long count2 = restClient.get()
                .uri("/v1/articles/boards/{boardId}/count", response.getBoardId())
                .retrieve()
                .body(Long.class);

        System.out.println("count2 = " + count2);
    }
}
