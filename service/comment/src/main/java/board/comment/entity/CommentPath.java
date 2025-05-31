package board.comment.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentPath {
    private String path;

    // 총 62
    private static final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final int DEPTH_CHEUNK_SIZE = 5;
    private static final int MAX_DEPTH = 5;

    private static final String MIN_CHUNK = String.valueOf(CHARSET.charAt(0)).repeat(DEPTH_CHEUNK_SIZE);
    private static final String MAX_CHUNK = String.valueOf(CHARSET.charAt(CHARSET.length() - 1)).repeat(DEPTH_CHEUNK_SIZE);

    public static CommentPath create(String path) {
        if (_isDepthOverflowed(path)) {
            throw new IllegalStateException("DEPTH OVERFLOWED");
        }

        CommentPath commentPath = new CommentPath();
        commentPath.path = path;

        return commentPath;
    }

    private static boolean _isDepthOverflowed(String path) {
        return _calcDepth(path) > MAX_DEPTH;
    }

    private static int _calcDepth(String path) {
        return path.length() / DEPTH_CHEUNK_SIZE;
    }

    public int getDepth() {
        return _calcDepth(path);
    }

    public boolean isRoot() {
        return _calcDepth(path) == 1;
    }

    public String getParentPath() {
        return path.substring(0, path.length() - DEPTH_CHEUNK_SIZE);
    }

    public CommentPath createChildCommentPath(String decendentsTopPath) {
        if (decendentsTopPath == null) {
            return CommentPath.create(path + MIN_CHUNK);
        }
        String childrenTopPath = _findChildrenTopPath(decendentsTopPath);
        return CommentPath.create(_increase(childrenTopPath));
    }

    private String _findChildrenTopPath(String decendentsTopPath) {
        return decendentsTopPath.substring(0, (getDepth() + 1) * DEPTH_CHEUNK_SIZE);
    }

    private String _increase(String path) {
        String lastChunk = path.substring(path.length() - DEPTH_CHEUNK_SIZE);
        if (_isChunkOverflowed(lastChunk)) {
            throw new IllegalStateException("CHUNK OVERFLOWED");
        }

        int charsetLength = CHARSET.length();

        // 10진수 변환 후 -> 값 1 증가
        int value = 0;
        for (char ch : lastChunk.toCharArray()) {
            value = value * charsetLength + CHARSET.indexOf(ch);
        }
        value = value + 1;

        // 10진수 값을 다시 62진수로 변환
        String result = "";
        for (int i = 0; i < DEPTH_CHEUNK_SIZE; i++) {
            result = CHARSET.charAt(value % charsetLength) + result;
            value /= charsetLength;
        }

        return path.substring(0, path.length() - DEPTH_CHEUNK_SIZE) + result;
    }

    private boolean _isChunkOverflowed(String lastChunk) {
        return MAX_CHUNK.equals(lastChunk);
    }
}
