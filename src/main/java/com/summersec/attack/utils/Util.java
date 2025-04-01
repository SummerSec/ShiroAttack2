package com.summersec.attack.utils;

import clojure.lang.Obj;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;

public class Util {
    public static String DEFAULT_PATH = "~/Desktop/";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LENGTH = 8;

    public static void createSerializeFile(Object obj, String... serPath) throws IOException {
        String targetFilePath = DEFAULT_PATH + "ser.bin";
        if (serPath.length != 0) {
            targetFilePath = serPath[0];
        }
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(targetFilePath));
        oos.writeObject(obj);
    }

    public static byte[] getSerializedData(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        return bos.toByteArray();
    }

    public static Object unserializeFromFile(String... serFilePath) throws IOException, ClassNotFoundException {
        String targetFilePath = DEFAULT_PATH + "ser.bin";
        if (serFilePath.length != 0) {
            targetFilePath = serFilePath[0];
        }
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(targetFilePath));
        return ois.readObject();
    }

    public static String bcelEncode(String fileName, String... classFilePath) throws IOException {
        String filePath = DEFAULT_PATH + fileName;
        if (classFilePath.length != 0) {
            filePath = classFilePath[0] + fileName;
        }
        byte[] code = Files.readAllBytes(Paths.get(filePath));
        return  "$$BCEL$$" + com.sun.org.apache.bcel.internal.classfile.Utility.encode(code, true);
    }

    public static byte[] getFileByte(String fileName, String... filePath) throws IOException {
        String targetFilePath = DEFAULT_PATH + fileName;
        if (filePath.length != 0) {
            targetFilePath = filePath[0] + fileName;
        }
        byte[] code = Files.readAllBytes(Paths.get(targetFilePath));
        return code;
    }

    public static String byteCodeToBase64(byte[] code) {
        return DatatypeConverter.printBase64Binary(code);
    }

    public static byte[] base64ToByteCode(String bs) {
        byte[] value = null;

        Class base64;
        try {
            base64 = Class.forName("java.util.Base64");
            Object decoder = base64.getMethod("getDecoder", (Class[])null).invoke(base64, (Object[])null);
            value = (byte[])((byte[])decoder.getClass().getMethod("decode", String.class).invoke(decoder, bs));
        } catch (Exception var6) {
            try {
                base64 = Class.forName("sun.misc.BASE64Decoder");
                Object decoder = base64.newInstance();
                value = (byte[])((byte[])decoder.getClass().getMethod("decodeBuffer", String.class).invoke(decoder, bs));
            } catch (Exception var5) {
            }
        }

        return value;
    }

    public static String fullyURLEncode(String input) throws UnsupportedEncodingException {
        StringBuilder encodedString = new StringBuilder();

        // Iterate over each character in the input string
        for (char ch : input.toCharArray()) {
            // Encode each character to its %XX format
            encodedString.append(String.format("%%%02X", (int) ch));
        }

        return encodedString.toString();
    }

    public static String unicodeEncode(String input) {
        StringBuilder unicodeBuilder = new StringBuilder();
        for (char c : input.toCharArray()) {
            unicodeBuilder.append("\\u");
            unicodeBuilder.append(String.format("%04x", (int) c));
        }
        return unicodeBuilder.toString();
    }

    public static byte[] byteArrayMerger(byte[] bt1, byte[] bt2) {
        if (bt2 == null) {
            return bt1;
        } else {
            byte[] bt3 = new byte[bt1.length + bt2.length];
            System.arraycopy(bt1, 0, bt3, 0, bt1.length);
            System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
            return bt3;
        }
    }

    public static String generateRandomString() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(LENGTH);

        for (int i = 0; i < LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }




}
