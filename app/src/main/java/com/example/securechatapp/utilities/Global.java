package com.example.securechatapp.utilities;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Global {

    public static String MSG_DATA = "data";
    public static String FCM_TOKEN;
    public static String USER_NAME;
    public static String MSG_REGISTRATION_IDS = "registration_ids";
    public static String text = null;
    public static byte[] AESKey = {9, 115, 51, 86, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53};
    public static Cipher cipher, deciper;
    public static SecretKeySpec keySpec;
    public static void init() {
        try {
            cipher = Cipher.getInstance("AES");
            deciper = Cipher.getInstance("AES");

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        keySpec = new SecretKeySpec(AESKey, "AES");
    }
    public static String encrypt(String string) {

        byte[] stringByte = string.getBytes();
        byte[] encryptedByte = new byte[stringByte.length];

        try {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            encryptedByte = cipher.doFinal(stringByte);

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        String result = null;
        try {
            result = new String(encryptedByte, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return result;

    }
    public static String decrypt(String string) {

        String result = string;
        try {
            byte[] encryptedByte = string.getBytes("ISO-8859-1");
            byte[] decryption;

            deciper.init(Cipher.DECRYPT_MODE, keySpec);
            decryption = deciper.doFinal(encryptedByte);
            result = new String(decryption);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return result;
    }
    public static HashMap<String, String> remoteMsgHeaders = null;
    public static HashMap<String, String> getRemoteMsgHeaders() {
        if (remoteMsgHeaders == null) {
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    "Authorization",
                    "key=AAAAyQAVIpQ:APA91bHW9929FZQXfuhZ1j66NgFIUSTGvzUHH_CE7KOegyNrrrOVPZ2R-Zwg75JLZT0zENPW-jz6ypIwJOs5UhgB3UiiivkPgFBVbJD7XFtAtwJTnVw3Np1Yfg4kJ_c8c94GmkTbotpc"
            );
            remoteMsgHeaders.put(
                    "Content-Type",
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }

}
