package board.comment.data;

import board.comment.entity.CommentPath;
import board.comment.entity.CommentV2;
import board.common.snowflake.Snowflake;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class CommentV2DataInitializerTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    TransactionTemplate tx;

    Snowflake snowflake = new Snowflake();
    CountDownLatch latch = new CountDownLatch(EXECUTE_COUNT);

    // totalData = BULK_INSERT_SIZE * EXECUTE_COUNT = 100,000,000
    static final int BULK_INSERT_SIZE = 2000;
    static final int EXECUTE_COUNT = 5000;

    @Test
    void init() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int ii = 0; ii < EXECUTE_COUNT; ii++) {
            int start = ii * BULK_INSERT_SIZE;
            int end = (ii + 1) * BULK_INSERT_SIZE;

            executorService.submit(() -> {
                insert(start, end);
                latch.countDown();
                System.out.println("Latch count: " + latch.getCount());
            });
        }
        latch.await();
        executorService.shutdown();

    }

    private void insert(int start, int end) {
        tx.executeWithoutResult(status -> {

            for (int ii = start; ii < end; ii++) {
                CommentV2 comment = CommentV2.create(
                        snowflake.nextId(),
                        "content-" + snowflake.nextId(),
                        1L,
                        1L,
                        toPath(ii)
                );
                em.persist(comment);
            }
        });
    }

    private CommentPath toPath(int value) {

        final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        final int DEPTH_CHUNK_SIZE = 5;

        String path = "";
        for (int i = 0; i < DEPTH_CHUNK_SIZE; i++) {
            path = CHARSET.charAt(value % CHARSET.length()) + path;
            value /= CHARSET.length();
        }
        return CommentPath.create(path);
    }
}
