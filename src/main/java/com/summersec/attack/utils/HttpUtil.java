package com.summersec.attack.utils;


import com.summersec.attack.UI.MainController;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class HttpUtil {
    private static final int Timeout = 5000;
    private static final String DefalutEncoding = "UTF-8";
    public static HostnameVerifier allHostsValid = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    public HttpUtil() {
    }

    public static String httpRequest(String requestUrl, int timeOut, String requestMethod, String contentType, String postString, String encoding) throws Exception {
        if ("".equals(encoding) || encoding == null) {
            encoding = "UTF-8";
        }

        URLConnection httpUrlConn = null;
        HttpsURLConnection hsc = null;
        HttpURLConnection hc = null;
        InputStream inputStream = null;

        String var12;
        try {
            String result;
            try {
                URL url = new URL(requestUrl);
                if (requestUrl.startsWith("https")) {
                    SSLContext sslContext = SSLContext.getInstance("SSL");
                    TrustManager[] tm = new TrustManager[]{new MyCert()};
                    sslContext.init((KeyManager[])null, tm, new SecureRandom());
                    SSLSocketFactory ssf = sslContext.getSocketFactory();
                    hsc = (HttpsURLConnection)url.openConnection();
                    hsc.setInstanceFollowRedirects(false);
                    hsc.setSSLSocketFactory(ssf);
                    hsc.setHostnameVerifier(allHostsValid);
                    httpUrlConn = hsc;
                } else {
                    hc = (HttpURLConnection)url.openConnection();
                    hc.setRequestMethod(requestMethod);
                    hc.setInstanceFollowRedirects(false);
                    System.out.println(hc.getRequestProperties());
                    httpUrlConn = hc;
                }

                ((URLConnection)httpUrlConn).setConnectTimeout(timeOut);
                ((URLConnection)httpUrlConn).setReadTimeout(timeOut);
                if (contentType != null && !"".equals(contentType)) {
                    ((URLConnection)httpUrlConn).setRequestProperty("Content-Type", contentType);
                }

                ((URLConnection)httpUrlConn).setRequestProperty("User-Agent", UserAgentUtil.getRandomUserAgent());
                ((URLConnection)httpUrlConn).setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
                ((URLConnection)httpUrlConn).setRequestProperty("Accept-Encoding", "gzip, deflate");
                ((URLConnection)httpUrlConn).setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
                ((URLConnection)httpUrlConn).setRequestProperty("Connection", "close");
                ((URLConnection)httpUrlConn).setDoOutput(true);
                ((URLConnection)httpUrlConn).setDoInput(true);
                ((URLConnection)httpUrlConn).connect();
                if (null != postString && !"".equals(postString)) {
                    OutputStream outputStream = ((URLConnection)httpUrlConn).getOutputStream();
                    outputStream.write(postString.getBytes(encoding));
                    outputStream.flush();
                    outputStream.close();
                }

                inputStream = ((URLConnection)httpUrlConn).getInputStream();
                result = readString(inputStream, encoding);
                String var24 = result;
                return var24;
            } catch (IOException var19) {
                System.out.println(var19);
                if (hsc != null) {
                    result = readString(hsc.getErrorStream(), encoding);
                    return result;
                }
            } catch (Exception var20) {
                System.out.println(var20);
                throw var20;
            }

            if (hc != null) {
                result = readString(hc.getErrorStream(), encoding);
                var12 = result;
                return var12;
            }

            result = "";
            var12 = result;
        } finally {
            if (hsc != null) {
                hsc.disconnect();
            }

            if (hc != null) {
                hc.disconnect();
            }

        }

        return var12;
    }

    public static String readString(InputStream inputStream, String encoding) throws IOException {
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos = null;

        try {
            bis = new BufferedInputStream(inputStream);
            baos = new ByteArrayOutputStream();
            byte[] arr = new byte[1];

            int len;
            while((len = bis.read(arr)) != -1) {
                baos.write(arr, 0, len);
            }
        } catch (IOException var9) {
        } finally {
            if (baos != null) {
                baos.flush();
                baos.close();
            }

            if (bis != null) {
                bis.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }

            return baos.toString(encoding);
        }
    }

    public static String httpRequestAddHeader(String requestUrl, int timeOut, String requestMethod, String contentType, String postString, String encoding, HashMap<String, String> headers) throws Exception {
        if ("".equals(encoding) || encoding == null) {
            encoding = "UTF-8";
        }

        URLConnection httpUrlConn = null;
        HttpsURLConnection hsc = null;
        HttpURLConnection hc = null;
        InputStream inputStream = null;
        BufferedInputStream bis = null;
        Object var12 = null;

        String result;
        try {
            String key;
            try {
                URL url = new URL(requestUrl);
                if (requestUrl.startsWith("https")) {
                    SSLContext sslContext = SSLContext.getInstance("SSL");
                    TrustManager[] tm = new TrustManager[]{new MyCert()};
                    sslContext.init((KeyManager[])null, tm, new SecureRandom());
                    SSLSocketFactory ssf = sslContext.getSocketFactory();
                    Proxy proxy = (Proxy)MainController.currentProxy.get("proxy");
                    if (proxy != null) {
                        hsc = (HttpsURLConnection)url.openConnection(proxy);
                        hsc.setInstanceFollowRedirects(false);
                    } else {
                        hsc = (HttpsURLConnection)url.openConnection();
                        hsc.setInstanceFollowRedirects(false);
                    }

                    hsc.setSSLSocketFactory(ssf);
                    hsc.setHostnameVerifier(allHostsValid);
                    httpUrlConn = hsc;
                } else {
                    Proxy proxy = (Proxy)MainController.currentProxy.get("proxy");
                    if (proxy != null) {
                        hc = (HttpURLConnection)url.openConnection(proxy);
                        hc.setInstanceFollowRedirects(false);
                    } else {
                        hc = (HttpURLConnection)url.openConnection();
                        hc.setInstanceFollowRedirects(false);
                    }

                    hc.setRequestMethod(requestMethod);
                    hc.setInstanceFollowRedirects(false);
                    httpUrlConn = hc;
                }

                ((URLConnection)httpUrlConn).setConnectTimeout(timeOut);
                ((URLConnection)httpUrlConn).setReadTimeout(timeOut);
                if (contentType != null && !"".equals(contentType)) {
                    ((URLConnection)httpUrlConn).setRequestProperty("Content-Type", contentType);
                }

                ((URLConnection)httpUrlConn).setRequestProperty("User-Agent",UserAgentUtil.getRandomUserAgent());
                ((URLConnection)httpUrlConn).setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
                ((URLConnection)httpUrlConn).setRequestProperty("Accept-Encoding", "gzip, deflate");
                ((URLConnection)httpUrlConn).setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
                ((URLConnection)httpUrlConn).setRequestProperty("Connection", "close");

                if (headers != null) {
                    Iterator var28 = headers.keySet().iterator();

                    while(var28.hasNext()) {
                        key = (String)var28.next();
                        String val = (String)headers.get(key);
                        ((URLConnection)httpUrlConn).addRequestProperty(key, val);
                    }
                }

                ((URLConnection)httpUrlConn).setDoOutput(true);
                ((URLConnection)httpUrlConn).setDoInput(true);
                ((URLConnection)httpUrlConn).connect();
                if (null != postString && !"".equals(postString)) {
                    OutputStream outputStream = ((URLConnection)httpUrlConn).getOutputStream();
                    outputStream.write(postString.getBytes(encoding));
                    outputStream.close();
                }

                inputStream = ((URLConnection)httpUrlConn).getInputStream();
//                result = readString(inputStream, encoding);
                Map<String, List<String>> inputStreamHeaders = ((URLConnection)httpUrlConn).getHeaderFields();
                result = inputStreamHeaders.toString()+readString(inputStream, encoding);
                String var30 = result;
                return var30;
            } catch (IOException var23) {
                System.out.println(var23);
                if (hsc != null) {
                    System.out.println("1");
                    System.out.println(hsc.getErrorStream());
                    result = readString(hsc.getErrorStream(), encoding);
                    key = result;
                    return key;
                }

                if (hc != null) {
                    System.out.println("2");
                    System.out.println(hc.getErrorStream());
                    result = readString(hc.getErrorStream(), encoding);
                    key = result;
                    return key;
                }

                result = "";
            } catch (Exception var24) {
                System.out.println("3");
                System.out.println(var24);
                throw var24;
            }
        } finally {
            if (hsc != null) {
                hsc.disconnect();
            }

            if (hc != null) {
                hc.disconnect();
            }

        }

        return result;
    }

    public static int codeByHttpRequest(String requestUrl, int timeOut, String requestMethod, String contentType, String postString, String encoding) throws Exception {
        if ("".equals(encoding) || encoding == null) {
            encoding = "UTF-8";
        }

        URLConnection httpUrlConn = null;
        HttpsURLConnection hsc = null;
        HttpURLConnection hc = null;
        InputStream inputStream = null;
        InputStreamReader isr = null;
        Object br = null;

        byte var27;
        try {
            URL url = new URL(requestUrl);
            if (requestUrl.startsWith("https")) {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                TrustManager[] tm = new TrustManager[]{new MyCert()};
                sslContext.init((KeyManager[])null, tm, new SecureRandom());
                SSLSocketFactory ssf = sslContext.getSocketFactory();
                hsc = (HttpsURLConnection)url.openConnection();
                hsc.setInstanceFollowRedirects(false);
                hsc.setSSLSocketFactory(ssf);
                hsc.setHostnameVerifier(allHostsValid);
                httpUrlConn = hsc;
            } else {
                hc = (HttpURLConnection)url.openConnection();
                hc.setInstanceFollowRedirects(false);
                hc.setRequestMethod(requestMethod);
                httpUrlConn = hc;
            }

            ((URLConnection)httpUrlConn).setReadTimeout(timeOut);
            if (contentType != null && !"".equals(contentType)) {
                ((URLConnection)httpUrlConn).setRequestProperty("Content-Type", contentType);
            }

            ((URLConnection)httpUrlConn).setRequestProperty("User-Agent", UserAgentUtil.getRandomUserAgent());
            ((URLConnection)httpUrlConn).setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            ((URLConnection)httpUrlConn).setRequestProperty("Accept-Encoding", "gzip, deflate");
            ((URLConnection)httpUrlConn).setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
            ((URLConnection)httpUrlConn).setRequestProperty("Connection", "close");
            ((URLConnection)httpUrlConn).setDoOutput(true);
            ((URLConnection)httpUrlConn).setDoInput(true);
            ((URLConnection)httpUrlConn).setUseCaches(false);
            ((URLConnection)httpUrlConn).connect();
            if (null != postString && !"".equals(postString)) {
                OutputStream outputStream = ((URLConnection)httpUrlConn).getOutputStream();
                outputStream.write(postString.getBytes(encoding));
                outputStream.close();
            }

            int var24;
            if (hsc != null) {
                var24 = hsc.getResponseCode();
                int var26 = var24;
                return var26;
            }

            if (hc != null) {
                var24 = hc.getResponseCode();
                return var24;
            }

            byte var25 = 0;
            var27 = var25;
        } catch (IOException var21) {
            throw var21;
        } catch (Exception var22) {
            throw var22;
        } finally {
            if (br != null) {
                ((BufferedReader)br).close();
            }

            if (isr != null) {
                ((InputStreamReader)isr).close();
            }

            if (inputStream != null) {
                ((InputStream)inputStream).close();
            }

            if (hsc != null) {
                hsc.disconnect();
            }

            if (hc != null) {
                hc.disconnect();
            }

        }

        return var27;
    }

    public static String headerByHttpRequest(String requestUrl, int timeOut, String requestMethod, String contentType, String postString, String encoding, HashMap<String, String> headers) throws Exception {
        if ("".equals(encoding) || encoding == null) {
            encoding = "UTF-8";
        }

        URLConnection httpUrlConn = null;
        HttpsURLConnection hsc = null;
        HttpURLConnection hc = null;
        InputStream inputStream = null;
        InputStreamReader isr = null;
        Object br = null;

        String respHeaderKey;
        try {
            URL url = new URL(requestUrl);
            if (requestUrl.startsWith("https")) {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                TrustManager[] tm = new TrustManager[]{new MyCert()};
                sslContext.init((KeyManager[])null, tm, new SecureRandom());
                SSLSocketFactory ssf = sslContext.getSocketFactory();
                Proxy proxy = (Proxy)MainController.currentProxy.get("proxy");
                if (proxy != null) {
                    hsc = (HttpsURLConnection)url.openConnection(proxy);
                    hsc.setInstanceFollowRedirects(false);
                } else {
                    hsc = (HttpsURLConnection)url.openConnection();
                    hsc.setInstanceFollowRedirects(false);
                }

                hsc.setSSLSocketFactory(ssf);
                hsc.setHostnameVerifier(allHostsValid);
                httpUrlConn = hsc;
            } else {
                Proxy proxy = (Proxy)MainController.currentProxy.get("proxy");
                if (proxy != null) {
                    hc = (HttpURLConnection)url.openConnection(proxy);
                    hc.setInstanceFollowRedirects(false);
                } else {
                    hc = (HttpURLConnection)url.openConnection();
                    hc.setInstanceFollowRedirects(false);
                }

                hc.setRequestMethod(requestMethod);
                httpUrlConn = hc;
            }

            ((URLConnection)httpUrlConn).setReadTimeout(timeOut);
            if (contentType != null && !"".equals(contentType)) {
                ((URLConnection)httpUrlConn).setRequestProperty("Content-Type", contentType);
            }

            ((URLConnection)httpUrlConn).setRequestProperty("User-Agent", UserAgentUtil.getRandomUserAgent());
            ((URLConnection)httpUrlConn).setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            ((URLConnection)httpUrlConn).setRequestProperty("Accept-Encoding", "gzip, deflate");
            ((URLConnection)httpUrlConn).setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
            ((URLConnection)httpUrlConn).setRequestProperty("Connection", "close");
            if (headers != null) {
                Iterator var27 = headers.keySet().iterator();

                while(var27.hasNext()) {
                    String key = (String)var27.next();
                    String val = (String)headers.get(key);
                    ((URLConnection)httpUrlConn).addRequestProperty(key, val);
                }
            }

            ((URLConnection)httpUrlConn).setDoOutput(true);
            ((URLConnection)httpUrlConn).setDoInput(true);
            ((URLConnection)httpUrlConn).connect();
            if (null != postString && !"".equals(postString)) {
                OutputStream outputStream = ((URLConnection)httpUrlConn).getOutputStream();
                outputStream.write(postString.getBytes(encoding));
                outputStream.close();
            }

            StringBuilder responseHeaderString;
            String var34;
            Set respHeaderKeys;
            Iterator iterator;
            List arrayList;
            Iterator var24;
            String value;
            Map responseheaders;
            if (hsc != null) {
                responseHeaderString = new StringBuilder();
                responseheaders = hsc.getHeaderFields();
                respHeaderKeys = responseheaders.keySet();
                iterator = respHeaderKeys.iterator();

                label297:
                while(true) {
                    if (iterator.hasNext()) {
                        respHeaderKey = (String)iterator.next();
                        arrayList = (List)responseheaders.get(respHeaderKey);
                        var24 = arrayList.iterator();

                        while(true) {
                            if (!var24.hasNext()) {
                                continue label297;
                            }

                            value = (String)var24.next();
                            responseHeaderString.append(value);
                        }
                    }

                    var34 = responseHeaderString.toString();
                    respHeaderKey = var34;
                    return respHeaderKey;
                }
            }

            if (hc == null) {
                responseHeaderString = new StringBuilder();
                return responseHeaderString.toString();
            }

            responseHeaderString = new StringBuilder();
            responseheaders = hc.getHeaderFields();
            respHeaderKeys = responseheaders.keySet();
            iterator = respHeaderKeys.iterator();

            while(iterator.hasNext()) {
                respHeaderKey = (String)iterator.next();
                arrayList = (List)responseheaders.get(respHeaderKey);
                var24 = arrayList.iterator();

                while(var24.hasNext()) {
                    value = (String)var24.next();
                    responseHeaderString.append(value);
                }
            }

            var34 = responseHeaderString.toString();
            respHeaderKey = var34;
        } catch (IOException var30) {
            throw var30;
        } catch (Exception var31) {
            throw var31;
        } finally {
            if (br != null) {
                ((BufferedReader)br).close();
            }

            if (isr != null) {
                ((InputStreamReader)isr).close();
            }

            if (inputStream != null) {
                ((InputStream)inputStream).close();
            }

            if (hsc != null) {
                hsc.disconnect();
            }

            if (hc != null) {
                hc.disconnect();
            }

        }

        return respHeaderKey;
    }

    public static String httpReuest(String requestUrl, String method, String contentType, String postString, String encoding) throws Exception {
        return httpRequest(requestUrl, 5000, method, contentType, postString, encoding);
    }

    public static String postHttpReuest(String requestUrl, int timeOut, String contentType, String postString, String encoding) throws Exception {
        return httpRequest(requestUrl, timeOut, "POST", contentType, postString, encoding);
    }

    public static String postHttpReuest(String requestUrl, String postString, String encoding, HashMap<String, String> headers, String contentType, int timeout) throws Exception {
        return httpRequestAddHeader(requestUrl, timeout, "POST", contentType, postString, encoding, headers);
    }

    public static String postHttpReuest(String requestUrl, String contentType, String postString, String encoding) throws Exception {
        return httpRequest(requestUrl, 5000, "POST", contentType, postString, encoding);
    }

    public static String postHttpReuest(String requestUrl, int timeOut, String postString, String encoding) throws Exception {
        return httpRequest(requestUrl, timeOut, "POST", "application/x-www-form-urlencoded", postString, encoding);
    }

    public static String postHttpReuest(String requestUrl, String postString, String encoding) throws Exception {
        return httpRequest(requestUrl, 5000, "POST", "application/x-www-form-urlencoded", postString, encoding);
    }

    public static String getHttpReuest(String requestUrl, int timeout, String encoding, HashMap<String, String> headers) throws Exception {
        return httpRequestAddHeader(requestUrl, timeout, "GET", "", "", encoding, headers);
    }

    public static String postHttpReuestByXML(String requestUrl, int timeOut, String postString, String encoding) throws Exception {
        return httpRequest(requestUrl, timeOut, "POST", "text/xml", postString, encoding);
    }

    public static String postHttpReuestByXML(String requestUrl, String postString, String encoding) throws Exception {
        return httpRequest(requestUrl, 5000, "POST", "text/xml", postString, encoding);
    }

    public static String postHttpReuestByXMLAddHeader(String requestUrl, String postString, String encoding, HashMap<String, String> headers) throws Exception {
        return httpRequestAddHeader(requestUrl, 5000, "POST", "text/xml", postString, encoding, headers);
    }

    public static int codeByHttpRequest(String requestUrl, String method, String contentType, String postString, String encoding) throws Exception {
        return codeByHttpRequest(requestUrl, 5000, method, contentType, postString, encoding);
    }

    public static int getCodeByHttpRequest(String requestUrl, String encoding) throws Exception {
        return codeByHttpRequest(requestUrl, "GET", (String)null, "", encoding);
    }

    public static int getCodeByHttpRequest(String requestUrl, int timeout, String encoding) throws Exception {
        return codeByHttpRequest(requestUrl, timeout, "GET", (String)null, "", encoding);
    }

    public static int postCodeByHttpRequest(String requestUrl, String contentType, String postString, String encoding) throws Exception {
        return codeByHttpRequest(requestUrl, 5000, "POST", contentType, postString, encoding);
    }

    public static int postCodeByHttpRequestWithNoContenType(String requestUrl, String postString, String encoding) throws Exception {
        return codeByHttpRequest(requestUrl, 5000, "POST", (String)null, postString, encoding);
    }

    public static int postCodeByHttpRequest(String requestUrl, String encoding) throws Exception {
        return codeByHttpRequest(requestUrl, 5000, "POST", (String)null, (String)null, encoding);
    }

    public static int postCodeByHttpRequest(String requestUrl, String postString, String encoding) throws Exception {
        return codeByHttpRequest(requestUrl, 5000, "POST", "application/x-www-form-urlencoded", postString, encoding);
    }

    public static int postCodeByHttpRequestXML(String requestUrl, String postString, String encoding) throws Exception {
        return codeByHttpRequest(requestUrl, 5000, "POST", "text/xml", postString, encoding);
    }

    public static String getHeaderByHttpRequest(String requestUrl, String encoding, HashMap<String, String> headers, int timeout) throws Exception {
        return headerByHttpRequest(requestUrl, 5000, "GET", "text/xml", "", encoding, headers);
    }

    public static String postHeaderByHttpRequest(String requestUrl, String encoding, String postString, HashMap<String, String> headers, int timeout) throws Exception {
        return headerByHttpRequest(requestUrl, 5000, "POST", "text/xml", postString, encoding, headers);
    }

    public static boolean downloadFile(String downURL, File file) throws Exception {
        HttpURLConnection httpURLConnection = null;
        BufferedInputStream bin = null;
        FileOutputStream out = null;

        try {
            URL url = new URL(downURL);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            bin = new BufferedInputStream(httpURLConnection.getInputStream());
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            out = new FileOutputStream(file);
            int len = 0;
            byte[] buf = new byte[1024];

            int size;
            while((size = bin.read(buf)) != -1) {
                len += size;
                out.write(buf, 0, size);
            }
        } catch (Exception var12) {
            throw var12;
        } finally {
            if (bin != null) {
                bin.close();
            }

            if (out != null) {
                out.flush();
                out.close();
            }

            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }

        }

        return true;
    }

    public static boolean downloadFile(String downURL, String path) throws Exception {
        return downloadFile(downURL, new File(path));
    }
}
