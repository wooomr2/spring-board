package board.comment.service;

import board.comment.dto.request.CommentCreateRequestV2;
import board.comment.dto.response.CommentResponseV2;
import board.comment.entity.CommentPath;
import board.comment.entity.CommentV2;
import board.comment.repository.CommentRepositoryV2;
import board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.function.Predicate.not;

@Service
@RequiredArgsConstructor
public class CommentServiceV2 {

    private final Snowflake snowflake = new Snowflake();
    private final CommentRepositoryV2 commentRepository;

    @Transactional
    public CommentResponseV2 create(CommentCreateRequestV2 request) {
        CommentV2 parent = _findParent(request);

        CommentPath parentCommentPath = parent == null ?
                CommentPath.create("") :
                parent.getCommentPath();

        CommentV2 comment = commentRepository.save(
                CommentV2.create(
                        snowflake.nextId(),
                        request.getContent(),
                        request.getArticleId(),
                        request.getWriterId(),
                        parentCommentPath.createChildCommentPath(
                                commentRepository.findDescendantsTopPath(
                                        request.getArticleId(), parentCommentPath.getPath()
                                ).orElse(null)
                        )
                )
        );

        return CommentResponseV2.from(comment);
    }

    private CommentV2 _findParent(CommentCreateRequestV2 request) {
        String parentPath = request.getParentPath();
        if (parentPath == null) {
            return null;
        }

        return commentRepository.findByPath(parentPath)
                .filter(not(CommentV2::getDeleted))
                .orElseThrow();
    }

    public CommentResponseV2 read(Long commentId) {
        return CommentResponseV2.from(
                commentRepository.findById(commentId).orElseThrow()
        );
    }

    @Transactional
    public void delete(Long commentId) {
        commentRepository.findById(commentId)
                .filter(not(CommentV2::getDeleted))
                .ifPresent(comment -> {
                    if (_hasChildren(comment)) {
                        comment.delete();
                    } else {
                        _delete(comment);
                    }
                });
    }

    private void _delete(CommentV2 comment) {
        commentRepository.delete(comment);
        if (!comment.isRoot()) {
            commentRepository.findByPath(comment.getCommentPath().getParentPath())
                    .filter(CommentV2::getDeleted)
                    .filter(not(this::_hasChildren))
                    .ifPresent(this::_delete);
        }
    }

    private boolean _hasChildren(CommentV2 comment) {
        return commentRepository.findDescendantsTopPath(
                comment.getArticleId(),
                comment.getCommentPath().getPath()
        ).isPresent();
    }
}
