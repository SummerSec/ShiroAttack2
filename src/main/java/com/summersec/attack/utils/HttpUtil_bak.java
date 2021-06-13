package com.summersec.attack.utils;


import com.summersec.attack.UI.MainController;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.net.Proxy.Type;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpUtil_bak {
    public HttpUtil_bak() {
    }

    public static Map sendGetRequestWithCookie(String urlPath, String cookie, Integer timeout) throws Exception {
        Map result = new HashMap();
        StringBuilder sb = new StringBuilder();
        URL url = new URL(urlPath);
        HttpURLConnection conn;
        if (MainController.currentProxy.get("proxy") != null) {
            Proxy proxy = (Proxy)MainController.currentProxy.get("proxy");
            conn = (HttpURLConnection)url.openConnection(proxy);
        } else {
            conn = (HttpURLConnection)url.openConnection();
        }

        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setConnectTimeout(timeout * 1000);
        if (cookie != null && !cookie.equals("")) {
            conn.setRequestProperty("Cookie", cookie);
        }

        try {
            String line;
            for(BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); (line = reader.readLine()) != null; sb = sb.append(line + "\n")) {
            }

            String data = sb.toString();
            if (data.endsWith("\n")) {
                data = data.substring(0, data.length() - 1);
            }

            Map responseHeader = new HashMap();
            Iterator var28 = conn.getHeaderFields().keySet().iterator();

            while(var28.hasNext()) {
                String key = (String)var28.next();
                responseHeader.put(key, conn.getHeaderField(key));
            }

            result.put("data", data);
            responseHeader.put("status", conn.getResponseCode() + "");
            result.put("header", responseHeader);
            return result;
        } catch (Exception var14) {
            throw new Exception(var14.getMessage());
        }
    }

    public static Map sendGetRequest(String urlPath, Map header, Integer timeout) throws Exception {
        Map result = new HashMap();
        StringBuilder sb = new StringBuilder();
        URL url = new URL(urlPath);
        HttpURLConnection conn;
        if (MainController.currentProxy.get("proxy") != null) {
            Proxy proxy = (Proxy)MainController.currentProxy.get("proxy");
            conn = (HttpURLConnection)url.openConnection(proxy);
        } else {
            conn = (HttpURLConnection)url.openConnection();
        }

        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setConnectTimeout(timeout * 1000);
        if (header != null) {
            Object[] keys = header.keySet().toArray();
            Arrays.sort(keys);
            Object[] var8 = keys;
            int var9 = keys.length;

            for(int length = 0; length < var9; ++length) {
                Object key = var8[length];
                conn.setRequestProperty(key.toString(), (String)header.get(key));
            }
        }

        try {
            String line;
            for(BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); (line = reader.readLine()) != null; sb = sb.append(line + "\n")) {
            }

            String data = sb.toString();
            if (data.endsWith("\n")) {
                data = data.substring(0, data.length() - 1);
            }

            Map responseHeader = new HashMap();
            Iterator var28 = conn.getHeaderFields().keySet().iterator();

            while(var28.hasNext()) {
                String key = (String)var28.next();
                responseHeader.put(key, conn.getHeaderField(key));
            }

            result.put("data", data);
            responseHeader.put("status", conn.getResponseCode() + "");
            result.put("header", responseHeader);
            return result;
        } catch (Exception var14) {
            throw new Exception(var14.getMessage());
        }
    }

    public static String sendPostRequest(String urlPath, String cookie, String data) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        if (cookie != null && !cookie.equals("")) {
            conn.setRequestProperty("Cookie", cookie);
        }

        OutputStream outwritestream = conn.getOutputStream();
        outwritestream.write(data.getBytes());
        outwritestream.flush();
        outwritestream.close();
        String line;
        if (conn.getResponseCode() == 200) {
            for(BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); (line = reader.readLine()) != null; result = result.append(line + "\n")) {
            }
        }

        return result.toString();
    }

    public static Map sendPostRequestWithCookie(String urlPath, String cookie, String data, Integer timeout) throws Exception {
        Map result = new HashMap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        URL url = new URL(urlPath);
        HttpURLConnection conn;
        if (MainController.currentProxy.get("proxy") != null) {
            Proxy proxy = (Proxy)MainController.currentProxy.get("proxy");
            conn = (HttpURLConnection)url.openConnection(proxy);
        } else {
            conn = (HttpURLConnection)url.openConnection();
        }

        conn.setConnectTimeout(timeout * 1000);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        if (cookie != null && !cookie.equals("")) {
            conn.setRequestProperty("Cookie", cookie);
        }

        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        OutputStream outwritestream = conn.getOutputStream();
        outwritestream.write(data.getBytes());
        outwritestream.flush();
        outwritestream.close();

        try {
            DataInputStream din = new DataInputStream(conn.getInputStream());
            byte[] buffer = new byte[1024];
            boolean var12 = false;

            int length;
            while((length = din.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }

            byte[] resData = bos.toByteArray();
            result.put("data", resData);
            Map responseHeader = new HashMap();
            Iterator var28 = conn.getHeaderFields().keySet().iterator();

            while(var28.hasNext()) {
                String key = (String)var28.next();
                responseHeader.put(key, conn.getHeaderField(key));
            }

            responseHeader.put("status", conn.getResponseCode() + "");
            result.put("header", responseHeader);
            return result;
        } catch (Exception var17) {
            throw new Exception(var17.getMessage());
        }
    }

    public static Map sendPostRequestBinary(String urlPath, Map header, String data, Integer timeout) throws Exception {
        Map result = new HashMap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        URL url = new URL(urlPath);
        HttpURLConnection conn;
        if (MainController.currentProxy.get("proxy") != null) {
            Proxy proxy = (Proxy)MainController.currentProxy.get("proxy");
            conn = (HttpURLConnection)url.openConnection(proxy);
        } else {
            conn = (HttpURLConnection)url.openConnection();
        }

        conn.setConnectTimeout(timeout * 1000);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        int length;
        if (header != null) {
            Object[] keys = header.keySet().toArray();
            Arrays.sort(keys);
            Object[] var8 = keys;
            int var9 = keys.length;

            for(length = 0; length < var9; ++length) {
                Object key = var8[length];
                conn.setRequestProperty(key.toString(), (String)header.get(key));
            }
        }

        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        OutputStream outwritestream = conn.getOutputStream();
        outwritestream.write(data.getBytes());
        outwritestream.flush();
        outwritestream.close();

        try {
            DataInputStream din = new DataInputStream(conn.getInputStream());
            byte[] buffer = new byte[1024];
            boolean var22 = false;

            while((length = din.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }

            byte[] resData = bos.toByteArray();
            result.put("data", resData);
            Map responseHeader = new HashMap();
            Iterator var28 = conn.getHeaderFields().keySet().iterator();

            while(var28.hasNext()) {
                String key = (String)var28.next();
                responseHeader.put(key, conn.getHeaderField(key));
            }

            responseHeader.put("status", conn.getResponseCode() + "");
            result.put("header", responseHeader);
            return result;
        } catch (Exception var17) {
            throw new Exception(var17.getMessage());
        }
    }

    public static void disableSslVerification() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init((KeyManager[])null, trustAllCerts, new SecureRandom());
            List cipherSuites = new ArrayList();
            String[] var3 = sc.getSupportedSSLParameters().getCipherSuites();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String cipher = var3[var5];
                if (cipher.indexOf("_DHE_") < 0 && cipher.indexOf("_DH_") < 0) {
                    cipherSuites.add(cipher);
                }
            }

            HttpsURLConnection.setDefaultSSLSocketFactory(new HttpUtil_bak.MySSLSocketFactory(sc.getSocketFactory(), (String[])((String[])cipherSuites.toArray(new String[0]))));
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException var7) {
            var7.printStackTrace();
        } catch (KeyManagementException var8) {
            var8.printStackTrace();
        }

    }

    public static void main(String[] args) {
        disableSslVerification();

        try {
            InetSocketAddress proxyAddr = new InetSocketAddress("127.0.0.1", 8080);
            Proxy proxy = new Proxy(Type.HTTP, proxyAddr);
            MainController.currentProxy.put("proxy", proxy);
            Map headers = new HashMap();
            headers.put("Cookie", "rememberMe=1");
            Map result = sendPostRequestBinary("http://www.baidu.com", headers, "", 5);
            System.out.println(result);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    private static class MySSLSocketFactory extends SSLSocketFactory {
        private SSLSocketFactory sf;
        private String[] enabledCiphers;

        private MySSLSocketFactory(SSLSocketFactory sf, String[] enabledCiphers) {
            this.sf = null;
            this.enabledCiphers = null;
            this.sf = sf;
            this.enabledCiphers = enabledCiphers;
        }

        private Socket getSocketWithEnabledCiphers(Socket socket) {
            if (this.enabledCiphers != null && socket != null && socket instanceof SSLSocket) {
                ((SSLSocket)socket).setEnabledCipherSuites(this.enabledCiphers);
            }

            return socket;
        }

        public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
            return this.getSocketWithEnabledCiphers(this.sf.createSocket(s, host, port, autoClose));
        }

        public String[] getDefaultCipherSuites() {
            return this.sf.getDefaultCipherSuites();
        }

        public String[] getSupportedCipherSuites() {
            return this.enabledCiphers == null ? this.sf.getSupportedCipherSuites() : this.enabledCiphers;
        }

        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            return this.getSocketWithEnabledCiphers(this.sf.createSocket(host, port));
        }

        public Socket createSocket(InetAddress address, int port) throws IOException {
            return this.getSocketWithEnabledCiphers(this.sf.createSocket(address, port));
        }

        public Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws IOException, UnknownHostException {
            return this.getSocketWithEnabledCiphers(this.sf.createSocket(host, port, localAddress, localPort));
        }

        public Socket createSocket(InetAddress address, int port, InetAddress localaddress, int localport) throws IOException {
            return this.getSocketWithEnabledCiphers(this.sf.createSocket(address, port, localaddress, localport));
        }

        MySSLSocketFactory(SSLSocketFactory x0, String[] x1, Object x2) {
            this(x0, x1);
        }
    }
}
