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
      KeyGenerator keyGenerator = new KeyGenerator();
        System.out.println(keyGenerator.getKey());

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
