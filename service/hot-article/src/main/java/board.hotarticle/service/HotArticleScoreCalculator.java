package board.hotarticle.service;

import board.hotarticle.repository.ArticleCommentCountRepository;
import board.hotarticle.repository.ArticleLikeCountRepository;
import board.hotarticle.repository.ArticleViewCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HotArticleScoreCalculator {

    private final ArticleLikeCountRepository articleLikeCountRepository;
    private final ArticleCommentCountRepository articleCommentCountRepository;
    private final ArticleViewCountRepository articleViewCountRepository;

    // 가중치 설정
    private static final long ARTICLE_LIKE_COUNT_WEIGHT = 3;
    private static final long ARTICLE_COMMENT_COUNT_WEIGHT = 2;
    private static final long ARTICLE_VIEW_COUNT_WEIGHT = 1;

    public long calculate(Long articleId) {
        Long articleLikeCount = articleLikeCountRepository.read(articleId);
        Long articleCommentCount = articleCommentCountRepository.read(articleId);
        Long articleViewCount = articleViewCountRepository.read(articleId);

        long score = (articleLikeCount * ARTICLE_LIKE_COUNT_WEIGHT) +
                (articleViewCount * ARTICLE_VIEW_COUNT_WEIGHT) +
                (articleCommentCount * ARTICLE_COMMENT_COUNT_WEIGHT);

        return score;
    }
}
