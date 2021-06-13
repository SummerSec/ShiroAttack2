package com.summersec.attack.utils;

import java.net.URL;
import org.mozilla.universalchardet.UniversalDetector;



import java.net.URL;
import org.mozilla.universalchardet.CharsetListener;
import org.mozilla.universalchardet.UniversalDetector;

public class Utils {
    public Utils() {
    }

    public static String log(String info) {
        return trimN(info) + "\n";
    }

    private static String trimN(String str) {
        int len = str.length();
        int st = 0;

        char[] val;
        for(val = str.toCharArray(); st < len && val[st] <= '\r'; ++st) {
        }

        while(st < len && val[len - 1] <= '\r') {
            --len;
        }

        return st <= 0 && len >= str.length() ? str : str.substring(st, len);
    }

    public static String guessEncoding(byte[] bytes) {
        String DEFAULT_ENCODING = "UTF-8";
        UniversalDetector detector = new UniversalDetector((CharsetListener)null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        if (encoding == null) {
            encoding = DEFAULT_ENCODING;
        }

        return encoding;
    }

    public static String UrlToDomain(String target) {
        try {
            URL url = new URL(target);
            int port;
            if (url.getPort() == -1) {
                port = url.getDefaultPort();
            } else {
                port = url.getPort();
            }

            String httpAddress = url.getProtocol() + "://" + url.getHost() + ":" + port;
            return httpAddress;
        } catch (Exception var4) {
            return var4.getMessage();
        }
    }
}




