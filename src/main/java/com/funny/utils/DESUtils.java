package com.funny.utils;

import com.youzan.open.sdk.util.hash.MD5Utils;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * 用来解密cards
 *
 * @author liyanjun
 */
public class DESUtils {


    private static final String DES = "DES";

    private static final String PADDING = "DES/ECB/PKCS5Padding";

    private static final String DEFAULT_INCODING = "utf-8";

    public static String DesDecrypt(String key, String orderId, String strText) throws Exception {
        String tempString = MD5Utils.MD5(orderId + key).substring(4, 8);

        return decrypt(strText, tempString);
    }

    public final static String encrypt(String code, String key) throws Exception {
        return Base64.encodeBase64String(encrypt(code.getBytes(DEFAULT_INCODING), key.getBytes(DEFAULT_INCODING)));

    }


    public static byte[] encrypt(byte[] code, byte[] key) throws Exception {

        GetCipher getCipher = new GetCipher(key).invoke();
        SecureRandom sr = getCipher.getSr();
        SecretKey secretKey = getCipher.getSecretKey();
        Cipher cipher = getCipher.getCipher();

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);

        return cipher.doFinal(code);

    }


    public final static String decrypt(String code, String key) throws Exception {
        return new String(decrypt(Base64.decodeBase64(code), key.getBytes(DEFAULT_INCODING)), DEFAULT_INCODING);
    }


    public static byte[] decrypt(byte[] src, byte[] key) throws Exception {

        SecureRandom sr = new SecureRandom();

        DESKeySpec dks = new DESKeySpec(key);

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);

        SecretKey sectetKey = keyFactory.generateSecret(dks);

        Cipher cipher = Cipher.getInstance(PADDING);

        cipher.init(Cipher.DECRYPT_MODE, sectetKey, sr);

        return cipher.doFinal(src);

    }


    private static class GetCipher {
        private byte[] key;
        private SecureRandom sr;
        private SecretKey secretKey;
        private Cipher cipher;

        public GetCipher(byte... key) {
            this.key = key;
        }

        public SecureRandom getSr() {
            return sr;
        }

        public SecretKey getSecretKey() {
            return secretKey;
        }

        public Cipher getCipher() {
            return cipher;
        }

        public GetCipher invoke() throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException {
            sr = new SecureRandom();

            DESKeySpec dks = new DESKeySpec(key);

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);

            secretKey = keyFactory.generateSecret(dks);

            cipher = Cipher.getInstance(PADDING);
            return this;
        }
    }
}
