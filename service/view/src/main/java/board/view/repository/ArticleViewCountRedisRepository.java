package board.view.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArticleViewCountRedisRepository {

    private final StringRedisTemplate redisTemplate;

    /**
     * key = view::article::{article_id}::view_count
     */
    private static final String KEY_FORMAT = "view::article::%s::view_count";

    private String _generateKey(Long articleId) {
        return KEY_FORMAT.formatted(articleId);
    }

    public Long read(Long articleId) {
        String result = redisTemplate.opsForValue().get(_generateKey(articleId));
        return result == null ? 0L : Long.parseLong(result);
    }

    public Long increase(Long articleId) {
        return redisTemplate.opsForValue().increment(_generateKey(articleId));
    }
}
