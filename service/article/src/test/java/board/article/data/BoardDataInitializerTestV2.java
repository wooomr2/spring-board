package board.article.data;

import board.article.entity.Article;
import board.common.snowflake.Snowflake;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class BoardDataInitializerTestV2 {

    @PersistenceContext
    EntityManager em;

    @Autowired
    PlatformTransactionManager transactionManager;

    Snowflake snowflake = new Snowflake();

    static final int BULK_INSERT_SIZE = 2000;
    static final int EXECUTE_COUNT = 50000;

    @Test
    void init() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < EXECUTE_COUNT; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> insert(), executorService);
            futures.add(future);
        }

        // 모든 작업 완료 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdown();
    }

    private void insert() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("bulkInsertTx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            for (int i = 0; i < BULK_INSERT_SIZE; i++) {
                Article article = Article.create(
                        snowflake.nextId(),
                        "title-" + snowflake.nextId(),
                        "content-" + snowflake.nextId(),
                        1L,
                        1L
                );
                em.persist(article);

                // 주기적으로 flush + clear
                if (i % 500 == 0) {
                    em.flush();
                    em.clear();
                }
            }

            // 마지막 flush
            em.flush();
            em.clear();

            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new RuntimeException("Insert 실패", e);
        }
    }
}