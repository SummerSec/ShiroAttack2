package com.summersec.attack.Encrypt;

import org.apache.shiro.codec.Base64;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName: KeyGenerator
 * @Description: TODO
 * @Author: Summer
 * @Date: 2021/12/3 11:54
 * @Version: v1.0.0
 * @Description:
 **/
public class KeyGenerator {
    public static void main(String[] args) {
//        javax.crypto.KeyGenerator keygen = null;
//        try {
//            keygen = javax.crypto.KeyGenerator.getInstance("AES");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        SecretKey deskey = keygen.generateKey();
////        System.out.println(Base64.encodeToString(deskey.getEncoded()));
//        System.out.println(Base64.encodeToString(new byte[]{101, -88, 60, -121, -55, 13, -27, -8, -27, -32, 18, -11, 106, 7, 15, -11}));
        KeyGenerator keyGenerator = new KeyGenerator();
        System.out.println(keyGenerator.getKey());
//      keyGenerator.getKey();

    }

    public String getKey() {
        javax.crypto.KeyGenerator keygen = null;
        try {
            keygen = javax.crypto.KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SecretKey deskey = keygen.generateKey();
        return Base64.encodeToString(deskey.getEncoded());
    }
}
