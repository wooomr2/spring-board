package board.comment.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommentPathTest {

    @Test
    void createChildCommentTest() {
        // 00000 <-- 최초생성
        createChildComment(CommentPath.create(""), null, "00000");

        // 00000
        //       00000 <- 하위댓글 최초생성
        createChildComment(CommentPath.create("00000"), null, "0000000000");

        // 00000
        // 00001 다음 댓글 생성
        createChildComment(CommentPath.create(""), "00000", "00001");

        // 0000z
        //       abcdz
        //             zzzzz
        //                   zzzzz
        //       abce0
        createChildComment(CommentPath.create("0000z"), "0000zabcdzzzzzzzzzzz", "0000zabce0");
    }

    void createChildComment(CommentPath commentPath, String descendantsTopPath, String expectedChildPath) {
        CommentPath childCommentPath = commentPath.createChildCommentPath(descendantsTopPath);

        assertThat(childCommentPath.getPath()).isEqualTo(expectedChildPath);
    }

    @Test
    void createChildCommentPathIfMax() {
        assertThatThrownBy(() -> CommentPath.create("zzzzz".repeat(5)).createChildCommentPath(null))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void createChildCommentPathIfChunkOverflow() {
        // given
        CommentPath commentPath = CommentPath.create("");

        //when
        assertThatThrownBy(() -> commentPath.createChildCommentPath("zzzzz"))
                .isInstanceOf(IllegalStateException.class);
    }
}