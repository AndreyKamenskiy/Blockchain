package utils;

import java.util.ArrayList;
import java.util.List;

public class ByteUtils {

    public static byte[] longToBytes(long x) {
        byte[] result = new byte[Long.BYTES];
        for (int i = Long.BYTES - 1; i >= 0; i--) {
            result[i] = (byte) (x & 0xFF);
            x >>= Byte.SIZE;
        }
        return result;
    }

    public static long bytesToLong(byte[] bytes) {
        long result = 0;
        for (int i = 0; i < Long.BYTES; i++) {
            result <<= Byte.SIZE;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }

    public static byte[] intToBytes(int x) {
        byte[] result = new byte[Integer.BYTES];
        for (int i = Integer.BYTES - 1; i >= 0; i--) {
            result[i] = (byte) (x & 0xFF);
            x >>= Byte.SIZE;
        }
        return result;
    }

    public static int bytesToInt(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < Integer.BYTES; i++) {
            result <<= Byte.SIZE;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }

    public static void addInt(byte[] array, int number, int startIndex) {
        System.arraycopy(intToBytes(number), 0, array, startIndex, Integer.BYTES);
    }

    public static boolean isLeadsNZeros(byte[] array, int zerosNum) {
        for (int i = 0; i < zerosNum; i++) {
            byte curr = array[i / 2];
            if (i % 2 == 0) {
                curr &= 0xF0;
            } else {
                curr &= 0x0F;
            }
            if (curr != 0) {
                return false;
            }
        }
        return true;
    }

    public static int getLeadingZeros(byte[] array) {
        int zeros = 0;
        for (byte b : array) {
            if ((b & 0xF0) == 0) {
                ++zeros;
            } else {
                return zeros;
            }
            if ((b & 0x0F) == 0) {
                ++zeros;
            } else {
                return zeros;
            }
        }
        return zeros;
    }

    public static byte[] StringsToBytes(String[] messages) {
        List<byte[]> bytesList = new ArrayList<>();
        int size = 0;
        for (String currentMessage : messages) {
            byte[] messageBytes = currentMessage.getBytes();
            bytesList.add(messageBytes);
            size += messageBytes.length;
        }
        byte[] res = new byte[size];
        int startIndex = 0;
        for (byte[] curr : bytesList) {
            System.arraycopy(curr, 0, res, startIndex, curr.length);
            startIndex += curr.length;
        }
        return res;
    }
}
