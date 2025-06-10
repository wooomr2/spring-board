package board.common.util;

import org.junit.jupiter.api.Test;

import java.time.Duration;

class TimeCalculatorTest {

    @Test
    void test() {
        Duration duration = TimeCalculator.calculateDurationBetweenMidnight();
        System.out.println(duration.getSeconds() / 60 + " minutes");
    }
}