package board.comment.api;

import board.comment.dto.response.CommentPageResponseV2;
import board.comment.dto.response.CommentResponseV2;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class CommentApiV2Test {

    RestClient restClient = RestClient.create("http://localhost:9001");

    @Getter
    @AllArgsConstructor
    static class CommentCreateRequestV2 {

        private Long articleId;
        private String content;
        private String parentPath;
        private Long writerId;
    }

    CommentResponseV2 createComment(CommentCreateRequestV2 request) {
        return restClient.post()
                .uri("/v2/comments")
                .body(request)
                .retrieve()
                .body(CommentResponseV2.class);
    }

    @Test
    void create() {
        CommentResponseV2 response1 = createComment(new CommentCreateRequestV2(1L, "comment1", null, 1L));
        CommentResponseV2 response2 = createComment(new CommentCreateRequestV2(1L, "comment2", response1.getPath(), 1L));
        CommentResponseV2 response3 = createComment(new CommentCreateRequestV2(1L, "comment3", response2.getPath(), 1L));

        System.out.println("댓글 ID 목록:");
        System.out.println("response1: " + response1.getCommentId() + " path: " + response1.getPath());
        System.out.println("response2: " + response2.getCommentId() + " path: " + response2.getPath());
        System.out.println("response3: " + response3.getCommentId() + " path: " + response3.getPath());
    }

    @Test
    void read() {
        CommentResponseV2 response = restClient.get()
                .uri("/v2/comments/{commentId}", 187457349503643648L)
                .retrieve()
                .body(CommentResponseV2.class);

        System.out.println(response);
    }

    @Test
    void delete() {
        // 185308445518135296L
        // 185308446147280896L
        // 185308446285692928L

        restClient.delete()
                .uri("/v2/comments/{commentId}", 187457349503643648L)
                .retrieve()
                .toBodilessEntity();
    }

    @Test
    void readAll() {
        CommentPageResponseV2 response = restClient.get()
                .uri("v2/comments?articleId=1&page=50000&pageSize=30")
                .retrieve()
                .body(CommentPageResponseV2.class);

        System.out.println(response.getCommentCount());
        for (CommentResponseV2 comment : response.getComments()) {
            System.out.println(comment.getCommentId());
        }
    }

    @Test
    void readAllInfiniteScroll() {
        List<CommentResponseV2> response1 = restClient.get()
                .uri("v2/comments/infinite-scroll?articleId=1&pageSize=30")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponseV2>>() {
                });

        System.out.println("first Page of Infinite Scroll");
        for (CommentResponseV2 comment : response1) {
            System.out.println(comment);
        }

        String lastPath = response1.getLast().getPath();

        List<CommentResponseV2> response2 = restClient.get()
                .uri("v2/comments/infinite-scroll?articleId=1&pageSize=30&lastPath=%s"
                        .formatted(lastPath))
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponseV2>>() {
                });

        System.out.println("second Page of Infinite Scroll");
        for (CommentResponseV2 comment : response2) {
            System.out.println(comment);
        }
    }

    @Test
    void countTest() {
        Long articleId = 4L;
        CommentResponseV2 commentResponse = createComment(new CommentCreateRequestV2(articleId, "comment1", null, 1L));

        Long count1 = restClient.get()
                .uri("/v2/comments/articles/{articleId}/count", articleId)
                .retrieve()
                .body(Long.class);
        System.out.println("count1 = " + count1); // 1

        restClient.delete()
                .uri("/v2/comments/{commentId}", commentResponse.getCommentId())
                .retrieve()
                .toBodilessEntity();

        Long count2 = restClient.get()
                .uri("/v2/comments/articles/{articleId}/count", articleId)
                .retrieve()
                .body(Long.class);
        System.out.println("count2 = " + count2); // 0
    }
}
