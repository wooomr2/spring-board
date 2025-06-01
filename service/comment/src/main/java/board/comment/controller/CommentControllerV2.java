package board.comment.controller;

import board.comment.dto.request.CommentCreateRequestV2;
import board.comment.dto.response.CommentResponseV2;
import board.comment.service.CommentServiceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentControllerV2 {

    private final CommentServiceV2 commentService;

    @GetMapping("/v2/comments/{commentId}")
    public CommentResponseV2 read(@PathVariable Long commentId) {
        return commentService.read(commentId);
    }

    @PostMapping("/v2/comments")
    public CommentResponseV2 create(@RequestBody CommentCreateRequestV2 request) {
        return commentService.create(request);
    }

    @DeleteMapping("/v2/comments/{commentId}")
    public void delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
    }

//    /**
//     * 페이징 List
//     */
//    @GetMapping("/v2/comments")
//    public CommentPageResponse readAll(@RequestParam Long articleId,
//                                       @RequestParam Long page,
//                                       @RequestParam Long pageSize
//    ) {
//        return commentService.readAll(articleId, page, pageSize);
//    }
//
//    /**
//     * 무한스크롤 List
//     */
//    @GetMapping("/v2/comments/infinite-scroll")
//    public List<CommentResponseV2> readAll(
//            @RequestParam Long articleId,
//            @RequestParam(required = false) Long lastParentId,
//            @RequestParam(required = false) Long lastCommentId,
//            @RequestParam Long pageSize
//    ) {
//        return commentService.readAll(articleId, lastParentId, lastCommentId, pageSize);
//    }
}
