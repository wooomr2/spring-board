package board.common.snowflake;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

class SnowflakeTest {
    Snowflake snowflake = new Snowflake();

    @Test
    void nextIdTest() throws ExecutionException, InterruptedException {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future<List<Long>>> futures = new ArrayList<>();

        int repeatCount = 1000;
        int idCount = 1000;

        // when
        for (int ii = 0; ii < repeatCount; ii++) {
            futures.add(executorService.submit(() -> _generateIdList(snowflake, idCount)));
        }

        // then
        List<Long> result = new ArrayList<>();
        for (Future<List<Long>> future : futures) {
            List<Long> idList = future.get();
            for (int ii = 1; ii < idList.size(); ii++) {
                assertThat(idList.get(ii)).isGreaterThan(idList.get(ii - 1));
            }
            result.addAll(idList);
        }
        assertThat(result.stream().distinct().count()).isEqualTo(repeatCount * idCount);

        executorService.shutdown();
    }

    @Test
    void nextIdPerformanceTest() throws InterruptedException {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int repeatCount = 1000;
        int idCount = 1000;
        CountDownLatch latch = new CountDownLatch(repeatCount);

        // when
        long start = System.nanoTime();

        for (int ii = 0; ii < repeatCount; ii++) {
            executorService.submit(() -> {
                _generateIdList(snowflake, idCount);
                latch.countDown();
            });
        }

        latch.await();

        long end = System.nanoTime();

        System.out.printf("times = %s ms%n", (end - start) / 1_000_000);

        executorService.shutdown();
    }

    private List<Long> _generateIdList(Snowflake snowflake, int count) {

        List<Long> idList = new ArrayList<>();
        while (count-- > 0) {
            idList.add(snowflake.nextId());
        }

        return idList;
    }
}