package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

public class RandomUtils {
    private static Random random = new Random();

    /**
     * Set {@link Random} object used when generating the data.
     *
     * @param random Random object to use
     */
    public static void setRandom(Random random) {
        RandomUtils.random = random;
    }

    public static Date randomDate(long startYear, long endYear) {
        assert endYear > startYear;
        final long YEAR_MULTIPLIER = 1000L * 60 * 60 * 24 * 365;
        long ms = (startYear - 1970) * YEAR_MULTIPLIER;
        ms += Math.abs(random.nextLong()) % ((endYear - startYear) * YEAR_MULTIPLIER);
        return new Date(ms);
    }

    public static void randomImage(int height, int width, File saveTo) throws IOException {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int red = random.nextInt(256);
                int green = random.nextInt(256);
                int blue = random.nextInt(256);
                img.setRGB(j, i, (red << 16) | (green << 8) | blue);
            }
        }

        ImageIO.write(img, "jpg", saveTo);
    }

    public static Date randomTimestamp(Date min, Date max) {
        long start = min.getTime();
        long end = max.getTime();
        long diff = end - start + 1;
        return new Date(start + (long)(Math.random() * diff));
    }
}
