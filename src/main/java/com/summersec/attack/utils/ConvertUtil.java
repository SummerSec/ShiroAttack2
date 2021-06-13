package com.summersec.attack.utils;

import java.math.BigInteger;

public class ConvertUtil {
    public ConvertUtil() {
    }

    public static String toHexString(String input) {
        return String.format("%x", new BigInteger(1, input.getBytes()));
    }

    public static String fromHexString(String hex) {
        StringBuilder str = new StringBuilder();

        for(int i = 0; i < hex.length(); i += 2) {
            str.append((char)Integer.parseInt(hex.substring(i, i + 2), 16));
        }

        return str.toString();
    }
}
