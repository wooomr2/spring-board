package board.articleread.cache;

import lombok.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OptimizedCacheTest {

    @Test
    void parsDataTest() {
        parseData("data", 10);
        parseData(3L, 5);
        parseData(3, 10);
        parseData(new TestClass("test", LocalDateTime.now()), 10);
    }

    void parseData(Object data, long ttl) {
        // given
        OptimizedCache cache = OptimizedCache.of(data, Duration.ofSeconds(ttl));
        System.out.println("data = " + data);

        // when
        Object parsedData = cache.parseData(data.getClass());

        //then
        System.out.println("parsedData = " + parsedData);
        assertThat(parsedData).isEqualTo(data);
    }

    @Getter
    @ToString
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    static class TestClass {
        String testData;
        LocalDateTime testTime;
    }

    @Test
    void isExpiredTest() {
        assertThat(OptimizedCache.of("data", Duration.ofDays(-30)).isExpired()).isTrue();
        assertThat(OptimizedCache.of("data", Duration.ofDays(30)).isExpired()).isFalse();
    }
}