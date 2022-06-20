package utils;

import junit.framework.TestCase;

import java.util.Arrays;

public class ByteUtilsTest extends TestCase {

    public void test_getLeadingZeros() {
        byte[][] arr = {
                {0x1a, 0x01},
                {0x10, 0x00},
                {0x04, 0x01},
                {0x00, 0x15},
                {0x00, 0x01},
                {0x00, 0x00},
        };
        int[] results = {0, 0, 1, 2, 3, 4};
        for (int i = 0; i < arr.length; i++) {
            assert ByteUtils.getLeadingZeros(arr[i]) == results[i];
        }
    }

    public void test_StringsToBytes() {
        String[] messages = new String[]{
                "message 1",
                "some text",
                "текст на русском",
                "one more text",
                "/n/t/b/r",
                "final string"
        };
        byte[] inBytes = ByteUtils.StringsToBytes(messages);
        int index = 0;
        for (String currStr : messages) {
            byte[] currBytes = currStr.getBytes();
            assert Arrays.equals(currBytes, 0, currBytes.length,
                    inBytes, index, index + currBytes.length);
            index += currBytes.length;
        }
    }
}