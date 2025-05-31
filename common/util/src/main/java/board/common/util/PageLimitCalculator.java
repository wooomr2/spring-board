package board.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PageLimitCalculator {

    /**
     * @param page             현재 페이지(n)
     * @param pageSize         페이지당 게시글 개수(m)
     * @param movablePageCount 이동 가능한 페이지 개수(k)
     * @return limit = (((n – 1) / k) + 1) * m * k + 1
     */
    public static Long calculatePageLimit(Long page, Long pageSize, Long movablePageCount) {
        return ((page - 1) / movablePageCount + 1) * pageSize * movablePageCount + 1;
    }
}
