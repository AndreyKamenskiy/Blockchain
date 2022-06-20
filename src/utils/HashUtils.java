package utils;

import java.security.MessageDigest;

public class HashUtils {

    public static byte[] getHash(byte[] input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            /* Applies sha256 to our input */
            return digest.digest(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
