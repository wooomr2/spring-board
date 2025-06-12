package board.view.service;

import board.common.event.EventType;
import board.common.event.payload.ArticleViewedEventPayload;
import board.common.outboxmessagerelay.OutboxEventPublisher;
import board.view.entity.ArticleViewCount;
import board.view.repository.ArticleViewCountBackupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleViewCountBackupProcessor {

    private final ArticleViewCountBackupRepository articleViewCountBackupRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    @Transactional
    public void backup(Long articleId, Long viewCount) {
        int result = articleViewCountBackupRepository.updateViewCount(articleId, viewCount);
        if (result == 0) {
            articleViewCountBackupRepository.findById(articleId)
                    .ifPresentOrElse(ignored -> {
                            },
                            () -> articleViewCountBackupRepository.save(
                                    ArticleViewCount.init(articleId, viewCount)
                            )
                    );
        }

        outboxEventPublisher.publish(
                EventType.ARTICLE_VIEWED,
                ArticleViewedEventPayload.builder()
                        .articleId(articleId)
                        .artileViewCount(viewCount)
                        .build(),
                articleId
        );
    }
}
