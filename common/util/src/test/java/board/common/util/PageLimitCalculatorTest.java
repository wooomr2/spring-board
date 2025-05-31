package board.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PageLimitCalculatorTest {

    @Test
    void calculatePageLimitTest() {

        calc(1L, 30L, 10L, 301L);
        calc(7L, 30L, 10L, 301L);
        calc(10L, 30L, 10L, 301L);
        calc(11L, 30L, 10L, 601L);
        calc(12L, 30L, 10L, 601L);
    }

    void calc(Long page, Long pageSize, Long movaplePageCount, Long expected) {

        Long limit = PageLimitCalculator.calculatePageLimit(page, pageSize, movaplePageCount);
        assertEquals(expected, limit);
    }
}