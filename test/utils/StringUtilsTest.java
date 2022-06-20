package utils;

import junit.framework.TestCase;

public class StringUtilsTest extends TestCase {

    public void test_toHexString() {
        final byte[] hash = {0x01, 0x23, 0x45, 0x67, (byte) 0x89,
                (byte) 0xab, (byte) 0xcd, (byte) 0xef};
        final String answer = "0123456789abcdef";
        assert answer.equals(String.copyValueOf(StringUtils.toHexString(hash)));
    }

}