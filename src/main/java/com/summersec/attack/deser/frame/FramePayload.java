package com.summersec.attack.deser.frame;

import com.sun.xml.internal.ws.util.StringUtils;

public interface FramePayload<T> {
    String sendpayload(Object paramObject, String paramString) throws Exception;

    String sendpayload(Object paramObject) throws Exception;

    public static class Utils {
        public static Class<? extends FramePayload> getPayloadClass(String className) {
            Class<? extends FramePayload> clazz = null;
            try {
                clazz = (Class)Class.forName("vulgui.deser.frame." + StringUtils.capitalize(className));
            } catch (Exception exception) {}

            return clazz;
        }
    }
}


