package board.articleread.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static board.common.dataserializer.DataSerializer.deserialize;
import static board.common.dataserializer.DataSerializer.serialize;
import static java.util.stream.Collectors.joining;

@Component
@RequiredArgsConstructor
public class OptimizedCacheManager {

    private final StringRedisTemplate redisTemplate;
    private final OptimizedCacheLockProvider optimizedCacheLockProvider;

    private static final String DELIMITER = "::";

    /**
     * ex) args = [1,2,3]
     * return prefix::1::2::3
     */
    private String generateKey(String prefix, Object[] args) {
        return prefix + DELIMITER + Arrays.stream(args).map(String::valueOf).collect(joining(DELIMITER));
    }

    public Object process(String type, long ttlSec, Object[] args,
                          Class<?> returnType, OptimizedCacheOriginDataSupplier<?> originDataSupplier) throws Throwable {

        String key = generateKey(type, args);

        String cachedData = redisTemplate.opsForValue().get(key);
        if (cachedData == null) {
            return refresh(originDataSupplier, key, ttlSec);
        }

        OptimizedCache optimizedCache = deserialize(cachedData, OptimizedCache.class);
        if (optimizedCache == null) {
            return refresh(originDataSupplier, key, ttlSec);
        }

        if (!optimizedCache.isExpired()) {
            return optimizedCache.parseData(returnType);
        }

        if (!optimizedCacheLockProvider.lock(key)) {
            return optimizedCache.parseData(returnType);
        }

        try {
            return refresh(originDataSupplier, key, ttlSec);
        } finally {
            optimizedCacheLockProvider.unlock(key);
        }
    }

    private Object refresh(OptimizedCacheOriginDataSupplier<?> originDataSupplier, String key, long ttlSec) throws Throwable {
        Object result = originDataSupplier.get();

        OptimizedCacheTTL cacheTTL = OptimizedCacheTTL.of(ttlSec);
        OptimizedCache optimizedCache = OptimizedCache.of(result, cacheTTL.getLogicalTTL());

        redisTemplate.opsForValue()
                .set(
                        key,
                        serialize(optimizedCache),
                        cacheTTL.getPhysicalTTL()
                );

        return result;
    }
}
