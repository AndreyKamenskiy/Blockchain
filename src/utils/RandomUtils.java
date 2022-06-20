package utils;

import java.util.Random;

public class RandomUtils {
    private static long seed = TimeUtils.getTimestamp();
    private static final Random random = new Random(seed);

    public static void setSeed(long seed) {
        RandomUtils.seed = seed;
        random.setSeed(seed);
    }

    public static int nextInt() {
        return Math.abs(random.nextInt());
    }

    public static int nextIntRange(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

}
