package com.summersec.attack.Encrypt;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import org.apache.shiro.codec.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class GcmEncrypt implements EncryptInterface {
    @Override
    public String getName() {
        return "gcm";
    }

    @Override
    public byte[] getBytes(Object obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        byteArrayOutputStream = new ByteArrayOutputStream();
        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public String encrypt(String key, byte[] payload) {
        try {
            byte[] raw = Base64.decode(key);
            byte[] ivs = generateInitializationVector();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/GCM/PKCS5Padding");
            GCMParameterSpec iv = new GCMParameterSpec(128, ivs);
            cipher.init(1, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(pad(payload));
            return new String(Base64.encode(byteMerger(ivs, encrypted)));
        } catch (Exception exception) {
            return "0";
        }
    }

    private static SecureRandom secureRandom;

    private static int initializationVectorSize = 128;

    private static byte[] pad(byte[] s) {
        s = byteMerger(s, charToByte((char)(16 - s.length % 16)));
        return s;
    }

    private static byte[] charToByte(char c) {
        byte[] b = new byte[2];
        b[0] = (byte)((c & 0xFF00) >> 8);
        b[1] = (byte)(c & 0xFF);
        return b;
    }

    private static byte[] byteMerger(byte[] bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt1.length + bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    private static byte[] generateInitializationVector() {
        int size = getInitializationVectorSize();
        int sizeInBytes = size / 8;
        byte[] ivBytes = new byte[sizeInBytes];
        SecureRandom random = ensureSecureRandom();
        random.nextBytes(ivBytes);
        return ivBytes;
    }

    private static SecureRandom ensureSecureRandom() {
        SecureRandom random = getSecureRandom();
        if (random == null)
            random = getDefaultSecureRandom();
        return random;
    }

    private static SecureRandom getSecureRandom() {
        return secureRandom;
    }

    private static SecureRandom getDefaultSecureRandom() {
        try {
            return SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            return new SecureRandom();
        }
    }

    private static int getInitializationVectorSize() {
        return initializationVectorSize;
    }
}
