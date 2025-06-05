package board.like.service;

import board.common.snowflake.Snowflake;
import board.like.dto.response.ArticleLikeResponse;
import board.like.entity.ArticleLike;
import board.like.entity.ArticleLikeCount;
import board.like.repository.ArticleLikeCountRepository;
import board.like.repository.ArticleLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {

    private final Snowflake snowflake = new Snowflake();
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleLikeCountRepository articleLikeCountRepository;

    public ArticleLikeResponse read(Long articleId, Long userId) {
        return articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .map(ArticleLikeResponse::from)
                .orElseThrow();
    }


    public Long count(Long articleId) {
        return articleLikeCountRepository.findById(articleId)
                .map(ArticleLikeCount::getLikeCount).orElse(0L);
    }

    @Transactional
    public void like(Long articleId, Long userId) {
        articleLikeRepository.save(
                ArticleLike.create(
                        snowflake.nextId(),
                        articleId,
                        userId
                )
        );
    }

    @Transactional
    public void unlike(Long articleId, Long userId) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(articleLikeRepository::delete);
    }

    /**
     * 비관적 락 1
     * update 구문
     */
    @Transactional
    public void likePessimisticLock1(Long articleId, Long userId) {
        articleLikeRepository.save(
                ArticleLike.create(
                        snowflake.nextId(),
                        articleId,
                        userId
                )
        );

        int result = articleLikeCountRepository.increase(articleId);
        if (result == 0) {
            // TODO::
            // 최초 요청시에는 update 되는 레코드가 없으므로 1로 초기화한다.
            // 트래픽이 순식간에 몰리는 경우, 유실될 수 있으므로
            // 게시글 생성 시점에 미리 0으로 초기화(생성) 해줘야 한다.
            articleLikeCountRepository.save(
                    ArticleLikeCount.init(articleId, 1L)
            );
        }
    }

    // TODO::
    // articleLikeRepository.findByArticleIdAndUserId(articleId, userId)쿼리가 수행됩니다.
    // 동일 파라미터에 대해 데이터가 삭제되기 전까지의 모든 동시 요청은 데이터 조회를 성공하고, ifPresent 구문으로 들어가게 됩니다.
    // 그리고 JPA의 delete 메소드를 호출하는데, JPA의 delete 메소드는 조회된 엔티티가 타 트랜잭션에 의해 이미 삭제되었더라도 예외를 던지지 않습니다.
    // 즉, 동시에 ifPresent로 들어간 모든 요청은 decrease를 중복으로 수행하게 됩니다.
    // 1개만 감소하면 충분한 상황인데도, 동시 요청에 의해 카운트가 중복으로 감소되는 것입니다.
    // 이를 해결하려면, JPA의 delete 메소드를 사용하는 대신, 직접 DELETE 쿼리를 native query로 정의할 수 있습니다.
    // 직접 정의한 DELETE 쿼리는 영향 받은 row의 수를 반환할 수 있도록 하고,
    // 이러한 반환 값이 0이라면, 이미 다른 요청에 의해 데이터가 삭제 되었음을 의미합니다.
    // 이 경우 예외를 발생시켜서 decrease 중복 수행을 방지할 수 있습니다.
    @Transactional
    public void unlikePessimisticLock1(Long articleId, Long userId) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(articleLike -> {
                    articleLikeRepository.delete(articleLike);
                    articleLikeCountRepository.decrease(articleId);
                });
    }

    /**
     * 비관적 락 2
     * select ... for update + update
     */
    @Transactional
    public void likePessimisticLock2(Long articleId, Long userId) {
        articleLikeRepository.save(
                ArticleLike.create(
                        snowflake.nextId(),
                        articleId,
                        userId
                )
        );
        
        // 조회시점부터 락을 점유하고 있기때문에 조회된 엔티티를 기반으로 가능
        ArticleLikeCount articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId)
                .orElseGet(() -> ArticleLikeCount.init(articleId, 0L));
        articleLikeCount.increase();
        // 최초 생성 시에는 영속되지 않을 수 있으므로 save 명시적으로 호출.
        articleLikeCountRepository.save(articleLikeCount);
    }

    // TODO::
    @Transactional
    public void unlikePessimisticLock2(Long articleId, Long userId) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(articleLike -> {
                    articleLikeRepository.delete(articleLike);
                    ArticleLikeCount articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId).orElseThrow();
                    articleLikeCount.decrease();
                });
    }

    /**
     * 낙관적 락
     */
    @Transactional
    public void likeOptimisticLock(Long articleId, Long userId) {
        articleLikeRepository.save(
                ArticleLike.create(
                        snowflake.nextId(),
                        articleId,
                        userId
                )
        );

        ArticleLikeCount articleLikeCount = articleLikeCountRepository.findById(articleId)
                .orElseGet(() -> ArticleLikeCount.init(articleId, 0L));
        articleLikeCount.increase();
        // 최초 생성 시에는 영속되지 않을 수 있으므로 save 명시적으로 호출.
        articleLikeCountRepository.save(articleLikeCount);
    }

    @Transactional
    public void unlikeOptimisticLock(Long articleId, Long userId) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(v -> {
                    articleLikeRepository.delete(v);
                    ArticleLikeCount articleLikeCount = articleLikeCountRepository.findById(articleId).orElseThrow();
                    articleLikeCount.decrease();
                });
    }
}
