package board.articleread.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}