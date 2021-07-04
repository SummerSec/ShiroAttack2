package com.summersec.attack.deser.frame;

import com.sun.xml.internal.ws.util.StringUtils;

import com.sun.xml.internal.ws.util.StringUtils;

public interface FramePayload<T> {
    String sendpayload(Object var1, String var2, String var3) throws Exception;

    String sendpayload(Object var1) throws Exception;

    public static class Utils {
        public Utils() {
        }

        public static Class<? extends FramePayload> getPayloadClass(String className) {
            Class clazz = null;

            try {
                clazz = Class.forName("vulgui.deser.frame." + StringUtils.capitalize(className));
            } catch (Exception var3) {
            }

            return clazz;
        }
    }
}



