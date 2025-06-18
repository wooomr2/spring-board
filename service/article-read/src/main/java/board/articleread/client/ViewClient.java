package board.articleread.client;

import board.articleread.cache.OptimizedCacheable;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewClient {

    private RestClient restClient;

    @Value("${endpoints.board-view-service.url}")
    private String endpointServiceUrl;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
                .baseUrl(endpointServiceUrl)
                .build();
    }

    /**
     * 1. RedisCache에서 데이터 조회
     * 2. 레디스에 데이터가 없다면, count 메소드 호출 원본데이터 요청
     * 3. count 메소드에서 조회된 데이터를 레디스에 저장
     */
//    @Cacheable(key = "#articleId", value = "articleViewCount")
    @OptimizedCacheable(type = "articleViewCount", ttlSec = 1)
    public long count(Long articleId) {
        log.info("[ViewClient.count] articleId={}", articleId);

        try {
            Long count = restClient.get()
                    .uri("/v1/article-views/articles/{articleId}/count", articleId)
                    .retrieve()
                    .body(Long.class);

            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("[ViewClient.count] articleId={}", articleId, e);
            return 0L;
        }
    }
}
