package board.view.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class ArticleViewDistributedLockRepository {

    private final StringRedisTemplate redisTemplate;

    /**
     * key = view::article::{article_id}::user::{user_id}::lock
     */
    private static final String KEY_FORMAT = "view::article::%s::user::%s::lock";


    private String _generateKey(Long articleId, Long userId) {
        return KEY_FORMAT.formatted(articleId, userId);
    }

    public boolean lock(Long articleId, Long userId, Duration ttl) {
        String key = _generateKey(articleId, userId);
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, "", ttl));
    }
}
