package board.articleread.cache;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class OptimizedCacheTTLTest {

    @Test
    void ofTest() {
        //given
        long ttlSec = 10;

        // when
        OptimizedCacheTTL optimizedCacheTTL = OptimizedCacheTTL.of(ttlSec);

        //then
        assertThat(optimizedCacheTTL.getLogicalTTL()).isEqualTo(Duration.ofSeconds(ttlSec));
        assertThat(optimizedCacheTTL.getPhysicalTTL()).isEqualTo(
                optimizedCacheTTL.getLogicalTTL().plusSeconds(OptimizedCacheTTL.PHYSICAL_TTL_DELAY_SECONDS)
        );
    }

}