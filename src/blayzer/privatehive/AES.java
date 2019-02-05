package blayzer.privatehive;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    public static String encrypt(String string, String secret)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(secret.getBytes("UTF-8"));
            cipher.init(Cipher.ENCRYPT_MODE, getKey(secret), iv);
            return Base64.getEncoder().encodeToString(cipher.doFinal(string.getBytes("UTF-8")));
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decrypt(String string, String secret)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(secret.getBytes("UTF-8"));
            cipher.init(Cipher.DECRYPT_MODE, getKey(secret), iv);
            return new String(cipher.doFinal(Base64.getDecoder().decode(string)));
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    public static SecretKeySpec getKey(String key)
    {
        try {
            byte[] keyBytes = key.getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            keyBytes = sha.digest(keyBytes);
            keyBytes = Arrays.copyOf(keyBytes, 16);
            return new SecretKeySpec(keyBytes, "AES");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
