package board.view.service;

import board.common.outboxmessagerelay.OutboxEventPublisher;
import board.view.repository.ArticleViewCountRedisRepository;
import board.view.repository.ArticleViewDistributedLockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ArticleViewService {

    private final ArticleViewCountRedisRepository articleViewCountRedisRepository;
    private final ArticleViewCountBackupProcessor articleViewCountBackupProcessor;
    private static final int BACK_UP_BATCH_SIZE = 100;
    private static final Duration TTL = Duration.ofMinutes(10);
    private final ArticleViewDistributedLockRepository articleViewDistributedLockRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    public Long increase(Long articleId, Long userId) {
        // 분산락:: 조회수 어뷰징 방지. 10분 이내에 동일 유저가 동일 게시글을 조회하면 Lock 획득 실패
        if (!articleViewDistributedLockRepository.lock(articleId, userId, TTL)) {
            return articleViewCountRedisRepository.read(articleId);
        }

        Long count = articleViewCountRedisRepository.increase(articleId);
        if (count % BACK_UP_BATCH_SIZE == 0) {
            articleViewCountBackupProcessor.backup(articleId, count);
        }
        return count;
    }

    public Long count(Long articleId) {
        return articleViewCountRedisRepository.read(articleId);
    }
}
