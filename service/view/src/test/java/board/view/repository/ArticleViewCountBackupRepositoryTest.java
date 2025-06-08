package board.view.repository;

import board.view.entity.ArticleViewCount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ArticleViewCountBackupRepositoryTest {

    @Autowired
    ArticleViewCountBackupRepository articleViewCountBackupRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    @Transactional
    void updateViewCountTest() {
        // given
        articleViewCountBackupRepository.save(
                ArticleViewCount.init(1L, 0L)
        );
        em.flush();
        em.clear();

        // when
        int result1 = articleViewCountBackupRepository.updateViewCount(1L, 100L);
        int result2 = articleViewCountBackupRepository.updateViewCount(1L, 300L);
        // 현재 viewCount보다 작은 값으로 업데이트 시도 시 실패해야함
        int result3 = articleViewCountBackupRepository.updateViewCount(1L, 200L);

        // then
        assertThat(result1).isEqualTo(1);
        assertThat(result2).isEqualTo(1);
        assertThat(result3).isEqualTo(0);

        ArticleViewCount articleViewCount = articleViewCountBackupRepository.findById(1L).get();
        assertThat(articleViewCount.getViewCount()).isEqualTo(300L);
    }
}