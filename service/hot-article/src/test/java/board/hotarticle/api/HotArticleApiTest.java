package board.hotarticle.api;

import board.hotarticle.response.HotArticleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class HotArticleApiTest {

    RestClient restClient = RestClient.create("http://localhost:9004");

    @Test()
    void test() {
        List<HotArticleResponse> responseList = restClient.get()
                .uri("/v1/hot-articles/articles/date/{dateStr}", "20250612")
                .retrieve()
                .body(new ParameterizedTypeReference<List<HotArticleResponse>>() {
                });

        for (HotArticleResponse hotArticleResponse : responseList) {
            System.out.println(hotArticleResponse);
        }
    }
}
