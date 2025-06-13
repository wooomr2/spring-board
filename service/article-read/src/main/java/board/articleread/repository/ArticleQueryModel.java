package board.articleread.repository;


import board.articleread.client.ArticleClient;
import board.common.event.payload.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ArticleQueryModel {

    private Long articleId;
    private String title;
    private String content;
    private Long boardId;
    private Long writerId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    private Long articleCommentCount;
    private Long articleLikeCount;

    public static ArticleQueryModel create(ArticleCreatedEventPayload payload) {
        ArticleQueryModel model = new ArticleQueryModel();
        model.articleId = payload.getArticleId();
        model.title = payload.getTitle();
        model.content = payload.getContent();
        model.boardId = payload.getBoardId();
        model.writerId = payload.getWriterId();
        model.createdAt = payload.getCreatedAt();
        model.modifiedAt = payload.getModifiedAt();
        model.articleCommentCount = 0L;
        model.articleLikeCount = 0L;
        return model;
    }

    public static ArticleQueryModel create(ArticleClient.ArticleResponse response, Long commentCount, Long likeCount) {
        ArticleQueryModel model = new ArticleQueryModel();
        model.articleId = response.getArtilceId();
        model.title = response.getTitle();
        model.content = response.getContent();
        model.boardId = response.getBoardId();
        model.writerId = response.getWriterId();
        model.createdAt = response.getCreatedAt();
        model.modifiedAt = response.getModifiedAt();
        model.articleCommentCount = commentCount;
        model.articleLikeCount = likeCount;
        return model;
    }

    public void updatedBy(ArticleUpdatedEventPayload payload) {
        this.title = payload.getTitle();
        this.content = payload.getContent();
        this.boardId = payload.getBoardId();
        this.writerId = payload.getWriterId();
        this.createdAt = payload.getCreatedAt();
        this.modifiedAt = payload.getModifiedAt();
    }

    public void updatedBy(CommentCreatedEventPayload payload) {
        this.articleCommentCount = payload.getArticleCommentCount();
    }

    public void updatedBy(CommentDeletedEventPayload payload) {
        this.articleCommentCount = payload.getArticleCommentCount();
    }

    public void updatedBy(ArticleLikedEventPayload payload) {
        this.articleLikeCount = payload.getArticleLkieCount();
    }

    public void updatedBy(ArticleUnLikedEventPayload payload) {
        this.articleLikeCount = payload.getArticleLkieCount();
    }
}
