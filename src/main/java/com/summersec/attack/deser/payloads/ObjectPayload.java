package com.summersec.attack.deser.payloads;

import com.sun.xml.internal.ws.util.StringUtils;



public interface ObjectPayload<T> { T getObject(Object paramObject) throws Exception;

    public static class Utils {
        public static Class<? extends ObjectPayload> getPayloadClass(String className) {
            Class<? extends ObjectPayload> clazz = null;
            try {
                clazz = (Class)Class.forName("com.summersec.attack.deser.payloads." + StringUtils.capitalize(className));
            } catch (Exception exception) {}

            return clazz;
        }
    }
}

