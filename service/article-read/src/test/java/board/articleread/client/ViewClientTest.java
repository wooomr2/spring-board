package board.articleread.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class ViewClientTest {

    @Autowired
    ViewClient viewClient;

    @Test
    void readCacheableTest() throws InterruptedException {
        viewClient.count(1L); // 로그출력
        viewClient.count(1L); // 캐시 조회. 로그 출력 안됨
        viewClient.count(1L); // 캐시 조회. 로그 출력 안됨

        TimeUnit.SECONDS.sleep(3); // 캐시만료 후에는
        viewClient.count(1L); // 로그 출력
    }

    @Test
    void readCacheableMultiThreadTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        viewClient.count(1L);

        for (int i = 0; i < 5; i++) {
            CountDownLatch latch = new CountDownLatch(5);
            for (int j = 0; j < 5; j++) {
                executorService.submit(() -> {
                    viewClient.count(1L);
                    latch.countDown();
                });
            }
            latch.await();
            TimeUnit.SECONDS.sleep(2);
            System.out.println("=== cache expired ===I");
        }
    }
}