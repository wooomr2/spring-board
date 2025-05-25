package board.article.data;

import board.article.entity.Article;
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
public class TestDataInitializer {

    @PersistenceContext
    EntityManager em;

    @Autowired
    TransactionTemplate tx;

    Snowflake snowflake = new Snowflake();
    CountDownLatch latch = new CountDownLatch(EXECUTE_COUNT);

    // totalData = BULK_INSERT_SIZE * EXECUTE_COUNT = 100,000,000
    static final int BULK_INSERT_SIZE = 2000;
    static final int EXECUTE_COUNT = 50000;

    @Test
    void init() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int ii = 0; ii < EXECUTE_COUNT; ii++) {
            executorService.submit(() -> {
                insert();
                latch.countDown();
                System.out.println("Latch count: " + latch.getCount());
            });
        }
        latch.await();
        executorService.shutdown();

    }

    private void insert() {
        tx.executeWithoutResult(status -> {
            for (int ii = 0; ii < BULK_INSERT_SIZE; ii++) {
                Article article = Article.create(
                        snowflake.nextId(),
                        "title-" + snowflake.nextId(),
                        "content-" + snowflake.nextId(),
                        1L,
                        1L
                );
                em.persist(article);
            }
        });
    }

}
