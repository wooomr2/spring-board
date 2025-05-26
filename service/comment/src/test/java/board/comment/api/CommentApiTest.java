package board.comment.api;

import board.comment.dto.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class CommentApiTest {

    RestClient restClient = RestClient.create("http://localhost:9001");

    @Getter
    @AllArgsConstructor
    static class CommentCreateRequest {

        private Long articleId;
        private String content;
        private Long parentCommentId;
        private Long writerId;
    }

    CommentResponse createComment(CommentCreateRequest request) {
        return restClient.post()
                .uri("/v1/comments")
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Test
    void create() {
        CommentResponse response1 = createComment(new CommentCreateRequest(1L, "comment1", null, 1L));
        CommentResponse response2 = createComment(new CommentCreateRequest(1L, "comment2", response1.getCommentId(), 1L));
        CommentResponse response3 = createComment(new CommentCreateRequest(1L, "comment3", response1.getCommentId(), 1L));

        System.out.println("댓글 ID 목록:");
        System.out.println("response1: " + response1.getCommentId());
        System.out.println("response2: " + response2.getCommentId());
        System.out.println("response3: " + response3.getCommentId());
    }

    @Test
    void read() {
        CommentResponse response = restClient.get()
                .uri("/v1/comments/{commentId}", 185196145969573888L)
                .retrieve()
                .body(CommentResponse.class);

        System.out.println(response);
    }

    @Test
    void delete() {
        // 185308445518135296L
        // 185308446147280896L
        // 185308446285692928L

        restClient.delete()
                .uri("/v1/comments/{commentId}", 185308446285692928L)
                .retrieve()
                .toBodilessEntity();
    }
}
