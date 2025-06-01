package board.comment.repository;

import board.comment.entity.CommentV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepositoryV2 extends JpaRepository<CommentV2, Long> {

    @Query("""
            select c
            from CommentV2 c
            where c.commentPath.path = :path
            """
    )
    Optional<CommentV2> findByPath(@Param("path") String path);

    @Query(value = """
            select path
            from comment_v2
            where article_id = :articleId
            and path > :pathPrefix
            and path like CONCAT(:pathPrefix, '%')
            order by path desc limit 1
            """,
            nativeQuery = true)
    Optional<String> findDescendantsTopPath(
            @Param("articleId") Long articleId,
            @Param("pathPrefix") String pathPrefix
    );


    @Query(value = """
                select c.comment_id, c.content, c.path, c.article_id, c.writer_id, c.deleted, c.created_at
                from (
                    select comment_id
                    from comment_v2
                    where article_id = :articleId
                    order by path
                    limit :limit offset :offset
                ) t
                left join comment_v2 c on t.comment_id = c.comment_id
            """,
            nativeQuery = true)
    List<CommentV2> findAll(
            @Param("articleId") Long articleId,
            @Param("offset") Long offset,
            @Param("limit") Long limit
    );


    @Query(value = """
                select count(*)
                from (
                    select comment_id
                    from comment_v2
                    where article_id = :articleId
                    limit :limit
                ) t
            """,
            nativeQuery = true)
    Long count(@Param("articleId") Long articleId,
               @Param("limit") Long limit);


    @Query(value = """
                select comment_id, content, path, article_id, writer_id, deleted, created_at
                from comment_v2
                where article_id = :articleId
                order by path
                limit :limit
            """,
            nativeQuery = true)
    List<CommentV2> findAllInfiniteScroll(
            @Param("articleId") Long articleId,
            @Param("limit") Long limit
    );

    @Query(value = """
                select comment_id, content, path, article_id, writer_id, deleted, created_at
                from comment_v2
                where article_id = :articleId and path > :lastPath
                order by path
                limit :limit
            """,
            nativeQuery = true)
    List<CommentV2> findAllInfiniteScroll(
            @Param("articleId") Long articleId,
            @Param("lastPath") String lastPath,
            @Param("limit") Long limit
    );
}
