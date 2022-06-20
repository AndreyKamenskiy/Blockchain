package utils;

import java.util.Date;

public class TimeUtils {

    public static long getTimestamp() {
        return new Date().getTime();
    }

    public static int secondsPassed(long fromMilli, long toMilli) {
        final int millisecondsInSeconds = 1000;
        return (int) ((toMilli - fromMilli) / millisecondsInSeconds);
    }

}
