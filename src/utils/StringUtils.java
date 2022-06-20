package utils;

public class StringUtils {

    public static char[] toHexString(byte[] hash) {
        final char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        int len = hash.length;
        char[] res = new char[len * 2];
        for (int i = 0; i < len; i++) {
            res[i * 2] = hexChars[(hash[i] >> 4) & 0xf];
            res[i * 2 + 1] = hexChars[hash[i] & 0xf];
        }
        return res;
    }

}
