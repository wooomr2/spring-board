package board.articleread.learning;

import org.junit.jupiter.api.Test;

public class LongToDoubleTest {

    @Test
    void longToDoubleTest() {
        // long은 64비트 정수
        // double은 64비트 부동소수점
        long longValue = 111111111111111111L;
        System.out.println("longValue = " + longValue);

        double doubleValue = longValue;
        System.out.println("doubleValue = " + doubleValue);

        long longValue2 = (long) doubleValue;
        System.out.println("longValue2 = " + longValue2);
    }
}
