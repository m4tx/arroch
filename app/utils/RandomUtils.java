package utils;

import java.util.Date;
import java.util.Random;

public class RandomUtils {
    private static Random random = new Random();

    public static Date randomDate(long startYear, long endYear) {
        final long YEAR_MULTIPLIER = 1000L * 60 * 60 * 24 * 365;
        long ms = (startYear - 1970) * YEAR_MULTIPLIER;
        ms += Math.abs(random.nextLong()) % ((endYear - startYear) * YEAR_MULTIPLIER);
        return new Date(ms);
    }
}
