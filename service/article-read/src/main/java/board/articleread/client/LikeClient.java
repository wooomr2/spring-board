package board.articleread.client;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeClient {

    private RestClient restClient;

    @Value("${endpoints.board-like-service.url}")
    private String endpointServiceUrl;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
                .baseUrl(endpointServiceUrl)
                .build();
    }

    public long count(Long articleId) {
        try {
            Long count = restClient.get()
                    .uri("/v1/article-likes/articles/{articleId}/count", articleId)
                    .retrieve()
                    .body(Long.class);

            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("[LikeClient.count] articleId={}", articleId, e);
        }

        return 0L;
    }
}
