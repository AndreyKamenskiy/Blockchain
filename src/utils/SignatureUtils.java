package utils;

import java.security.*;

public class SignatureUtils {

    public static byte[] signString(String data, PrivateKey privateKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        return signBytes(data.getBytes(), privateKey);
    }

    public static byte[] signBytes(byte[] data, PrivateKey privateKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature rsa = Signature.getInstance("SHA1withRSA");
        rsa.initSign(privateKey);
        rsa.update(data);
        return rsa.sign();
    }

    public static boolean verifySignature(byte[] data, byte[] signature, PublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(publicKey);
        sig.update(data);
        return sig.verify(signature);
    }

    public static boolean verifyStringSignature(String data, byte[] signature, PublicKey publicKey)
            throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        return verifySignature(data.getBytes(), signature, publicKey);
    }

}
