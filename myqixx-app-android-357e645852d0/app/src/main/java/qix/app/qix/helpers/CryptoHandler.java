package qix.app.qix.helpers;

import android.util.Base64;
import android.util.Log;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoHandler {

    private static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    public static String encrypt(String message) {

        byte[] srcBuff = message.getBytes();
        String ivx = getRandomString();

        //Log.d("IVX String:", ivx);
        SecretKeySpec skeySpec = new SecretKeySpec(Constants.KALIGO_SECRET_KEY.getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivx.getBytes());

        Cipher ecipher = null;

        try {
            ecipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }

        try {
            assert ecipher != null;
            ecipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
        }

        byte[] dstBuff = new byte[0];

        try {
            dstBuff = ecipher.doFinal(srcBuff);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        String hexCode = bytesToHexString(ivx.getBytes()) + bytesToHexString(dstBuff);

        Log.d("TOKEN HEX", hexCode);

        return Base64.encodeToString(new BigInteger(hexCode, 16).toByteArray(), Base64.DEFAULT);
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder buffer = new StringBuilder();
        for (byte aByte : bytes) {
            if (((int) aByte & 0xff) < 0x10)
                buffer.append("0");
            buffer.append(Long.toString((int) aByte & 0xff, 16));
        }
        return buffer.toString();
    }

    private static byte[] hexStringToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String decrypt(String base64Encoded) {

        String encryptedAndIvx = bytesToHexString(Base64.decode(base64Encoded, Base64.DEFAULT));

        String ivx = new String(hexStringToBytes(encryptedAndIvx.substring(0, 16*2)));
        String encrypted = encryptedAndIvx.substring(16*2);

        SecretKeySpec skeySpec = new SecretKeySpec(Constants.KALIGO_SECRET_KEY.getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivx.getBytes());

        Cipher ecipher = null;
        try {
            ecipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            assert ecipher != null;
            ecipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
        }

        byte[] originalBytes = new byte[0];
        try {
            originalBytes = ecipher.doFinal(hexStringToBytes(encrypted));
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return new String(originalBytes);
    }

    private static String getRandomString(){
        StringBuilder randStr = new StringBuilder();
        for(int i = 0; i < 16; i++){
            int number = getRandomNumber();
            char ch = CHAR_LIST.charAt(number);
            randStr.append(ch);
        }
        return randStr.toString();
    }

    private static int getRandomNumber() {
        int randomInt;
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(CHAR_LIST.length());
        if (randomInt - 1 == -1) {
            return randomInt;
        } else {
            return randomInt - 1;
        }
    }
}
