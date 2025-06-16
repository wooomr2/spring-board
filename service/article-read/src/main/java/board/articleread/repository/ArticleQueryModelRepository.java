package board.articleread.repository;

import board.common.dataserializer.DataSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

import static board.common.dataserializer.DataSerializer.deserialize;
import static board.common.dataserializer.DataSerializer.serialize;

@Repository
@RequiredArgsConstructor
public class ArticleQueryModelRepository {

    private final StringRedisTemplate redisTemplate;

    /**
     * key - article-read::article::{articleId}
     */
    private static final String KEY_FORMAT = "article-read::article::%s";

    private String generateKey(ArticleQueryModel articleQueryModel) {
        return generateKey(articleQueryModel.getArticleId());
    }

    private String generateKey(Long articleId) {
        return KEY_FORMAT.formatted(articleId);
    }

    public void create(ArticleQueryModel articleQueryModel, Duration ttl) {
        redisTemplate.opsForValue()
                .set(
                        generateKey(articleQueryModel),
                        serialize(articleQueryModel),
                        ttl
                );
    }

    public void update(ArticleQueryModel articleQueryModel) {
        redisTemplate.opsForValue()
                .setIfPresent(generateKey(articleQueryModel), serialize(articleQueryModel));
    }

    public void delete(Long articleId) {
        redisTemplate.delete(generateKey(articleId));
    }

    public Optional<ArticleQueryModel> read(Long articleId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(generateKey(articleId)))
                .map(json -> deserialize(json, ArticleQueryModel.class));
    }
}
