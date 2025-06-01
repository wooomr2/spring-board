package board.comment.controller;

import board.comment.dto.request.CommentCreateRequest;
import board.comment.dto.response.CommentPageResponse;
import board.comment.dto.response.CommentResponse;
import board.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/v1/comments/{commentId}")
    public CommentResponse read(@PathVariable Long commentId) {
        return commentService.read(commentId);
    }

    @PostMapping("/v1/comments")
    public CommentResponse create(@RequestBody CommentCreateRequest request) {
        return commentService.create(request);
    }

    @DeleteMapping("/v1/comments/{commentId}")
    public void delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
    }

    /**
     * 페이징 List
     */
    @GetMapping("/v1/comments")
    public CommentPageResponse readAll(@RequestParam Long articleId,
                                       @RequestParam Long page,
                                       @RequestParam Long pageSize
    ) {
        return commentService.readAllInfiniteScroll(articleId, page, pageSize);
    }

    /**
     * 무한스크롤 List
     */
    @GetMapping("/v1/comments/infinite-scroll")
    public List<CommentResponse> readAll(
            @RequestParam Long articleId,
            @RequestParam(required = false) Long lastParentId,
            @RequestParam(required = false) Long lastCommentId,
            @RequestParam Long pageSize
    ) {
        return commentService.readAllInfiniteScroll(articleId, lastParentId, lastCommentId, pageSize);
    }
}
