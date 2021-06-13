package com.summersec.attack.deser.echo;

import com.sun.xml.internal.ws.util.StringUtils;
import javassist.ClassPool;
import javassist.CtClass;





public interface EchoPayload<T> {
    CtClass genPayload(ClassPool paramClassPool) throws Exception;

    public static class Utils
    {
        public static Class<? extends EchoPayload> getPayloadClass(String className) throws ClassNotFoundException {
            Class<? extends EchoPayload> clazz = null;
            try {
                clazz = (Class)Class.forName("com.summersec.attack.deser.echo." + StringUtils.capitalize(className));
            } catch (ClassNotFoundException e1) {
                clazz = (Class)Class.forName("com.summersec.attack.deser.plugins." + StringUtils.capitalize(className));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return clazz;
        }
    }
}



