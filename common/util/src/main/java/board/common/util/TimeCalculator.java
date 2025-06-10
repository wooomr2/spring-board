package board.common.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class TimeCalculator {

    public static Duration calculateDurationBetweenMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.plusDays(1).with(LocalTime.MIDNIGHT);
        return Duration.between(now, midnight);
    }
}
