package utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class KeysGenerator {
    private static final int KEY_LENGTH = 1024;
    private static KeyPairGenerator keyGen;

    static {
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(KEY_LENGTH);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static KeyPair getNewKeyPair() {
        return keyGen.generateKeyPair();
    }

}
