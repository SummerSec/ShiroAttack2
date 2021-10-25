package com.summersec.attack.Encrypt;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.util.ByteSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class CbcEncrypt implements EncryptInterface {
    @Override
    public String getName() {
        return "cbc";
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
    public String encrypt(String key, byte[] objectBytes) {

        byte[] keyDecode = Base64.decode(key);
        AesCipherService cipherService = new AesCipherService();
        ByteSource byteSource = cipherService.encrypt(objectBytes, keyDecode);
        byte[] value = byteSource.getBytes();
        return new String(Base64.encode(value));
    }
}
