package com.clickntap.utils;

import ch.ethz.ssh2.crypto.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;

public class SecurityUtils {
    private static final String DEFAULT_IV = "0123456789abcdef";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/CTR/NoPadding";
    private static final String DEFAULT_ALGORITHM = "AES";

    public static byte[] decrypt(byte[] input, byte[] key, String algorithm, String cipherAlgorithm, AlgorithmParameterSpec paramSpec) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, algorithm);
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
        return cipher.doFinal(input);
    }

    public static byte[] decrypt(byte[] input, byte[] key) throws Exception {
        return decrypt(input, key, DEFAULT_ALGORITHM, DEFAULT_CIPHER_ALGORITHM, new IvParameterSpec(DEFAULT_IV.getBytes()));
    }

    public static byte[] encrypt(byte[] input, byte[] key, String algorithm, String cipherAlgorithm, AlgorithmParameterSpec paramSpec) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, algorithm);
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
        return cipher.doFinal(input);
    }

    public static byte[] encrypt(byte[] input, byte[] key) throws Exception {
        return encrypt(input, key, DEFAULT_ALGORITHM, DEFAULT_CIPHER_ALGORITHM, new IvParameterSpec(DEFAULT_IV.getBytes()));
    }

    public static SecretKey generateKey(int size) throws Exception {
        return generateKey(size, DEFAULT_ALGORITHM);
    }

    public static SecretKey generateKey(int size, String algorithm) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(algorithm);
        kgen.init(size);
        return kgen.generateKey();
    }

    //
    // public static String smartmd5(String s) throws Exception {
    // byte[] defaultBytes = s.getBytes(ConstUtils.UTF_8);
    // MessageDigest algorithm = MessageDigest.getInstance("MD5");
    // algorithm.reset();
    // algorithm.update(defaultBytes);
    // byte messageDigest[] = algorithm.digest();
    // StringBuffer hexString = new StringBuffer();
    // for (int i = 0; i < messageDigest.length; i++) {
    // hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
    // }
    // return hexString.toString();
    // }

    private static String encode(String s, String encoding) throws Exception {
        MessageDigest md = MessageDigest.getInstance(encoding);
        md.reset();
        md.update(s.getBytes("utf8"));
        return new String(Hex.encodeHex(md.digest()));
    }

    public static String md5(String s) throws Exception {
        return encode(s, "MD5");
    }

    public static String sha1(String s) throws Exception {
        return encode(s, "SHA-1");
    }

    public static String sha256(String s) throws Exception {
        return encode(s, "SHA-256");
    }

    public static String base64enc(String s) throws Exception {
        return new String(Base64.encode(s.getBytes("utf8")));
    }

    public static String base64dec(String s) throws Exception {
        return new String(Base64.decode(s.toCharArray()), "utf8");
    }

    public static void main(String args[]) throws Exception {
        String s = "tonino.mendicino@clickntap.com";
        System.out.println(SecurityUtils.md5(s));
        System.out.println(SecurityUtils.sha1(s));
        System.out.println(SecurityUtils.sha256(s));
        System.out.println(SecurityUtils.base64dec(SecurityUtils.base64enc(s)));
    }
}
