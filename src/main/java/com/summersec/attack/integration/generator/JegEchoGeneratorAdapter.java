package com.summersec.attack.integration.generator;

import com.summersec.attack.integration.generator.model.EchoGenerateRequest;
import com.summersec.attack.integration.generator.model.EchoGenerateResult;

import java.lang.reflect.Method;

public class JegEchoGeneratorAdapter implements EchoGeneratorAdapter {
    @Override
    public EchoGenerateResult generate(EchoGenerateRequest request) {
        try {
            Class<?> constantsClass = resolveClass(
                "jeg.core.config.jEGConstants",
                "jeg.common.config.Constants",
                "jeg.common.Constants",
                "jeg.core.config.Constants"
            );
            Class<?> configClass = resolveClass(
                "jeg.core.config.jEGConfig",
                "jeg.core.jEGConfig"
            );
            Class<?> generatorClass = resolveClass(
                "jeg.core.jEGenerator",
                "jeg.core.generator.jEGenerator"
            );

            Object config = configClass.newInstance();
            invokeSetter(configClass, config, "setServerType", constantsClass, valueOrDefault(request.getServerType(), "SERVER_TOMCAT"));
            invokeSetter(configClass, config, "setModelType", constantsClass, valueOrDefault(request.getModelType(), "MODEL_CMD"));
            invokeSetter(configClass, config, "setFormatType", constantsClass, valueOrDefault(request.getFormatType(), "FORMAT_BASE64"));
            applyOptionalTextConfig(configClass, config, request);
            configClass.getMethod("build").invoke(config);

            Object generator = generatorClass.getConstructor(configClass).newInstance(config);
            String payload = extractPayload(generatorClass, generator);
            String headerName = extractHeaderName(configClass, config, request.getRequestHeaderName());
            return EchoGenerateResult.ok("jEG", payload, headerName);
        } catch (Exception e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            String message = cause.getMessage();
            if (message == null || message.trim().isEmpty()) {
                message = cause.toString();
            }
            return EchoGenerateResult.fail("jEG", message);
        }
    }

    private static Class<?> resolveClass(String... names) throws ClassNotFoundException {
        for (String name : names) {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException ignored) {
            }
        }
        throw new ClassNotFoundException("无法定位 jEG API 类");
    }

    private static String valueOrDefault(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private static void invokeSetter(Class<?> configClass, Object config, String methodName, Class<?> constantsClass, String fieldName) throws Exception {
        Method setter = configClass.getMethod(methodName, String.class);
        String resolved = resolveConstantFieldName(fieldName);
        String constantValue = String.valueOf(constantsClass.getField(resolved).get(null));
        setter.invoke(config, constantValue);
    }

    /**
     * jEG 1.0.0：MODEL_CMD（常量值 Command）把「命令」写入 setReqHeaderName（模板内生成 getReqHeaderName() 返回该串）；
     * MODEL_CODE（常量值 Code）把「代码」写入 setReqParamName。
     * 不可再把 User-Agent 填进 setReqHeaderName，否则会当作命令嵌入字节码。
     */
    private static void applyOptionalTextConfig(Class<?> configClass, Object config, EchoGenerateRequest request) {
        String modelField = valueOrDefault(request.getModelType(), "MODEL_CMD");
        boolean isCmd = modelField.contains("CMD");
        boolean isCode = modelField.contains("CODE") && !isCmd;

        if (isCmd) {
            String cmd = request.getJegCmdText();
            if (cmd != null && !cmd.trim().isEmpty()) {
                invokeOptionalSetter(configClass, config, "setReqHeaderName", cmd.trim());
            }
        } else if (isCode) {
            String code = request.getJegCodeText();
            if (code != null && !code.trim().isEmpty()) {
                invokeOptionalSetter(configClass, config, "setReqParamName", code.trim());
            }
        }

        if (request.getRequestHeaderName() != null && !request.getRequestHeaderName().trim().isEmpty()) {
            invokeOptionalSetter(configClass, config, "setReqHeaderValue", valueOrDefault(request.getRequestHeaderValue(), ""));
        }
        invokeOptionalSetter(configClass, config, "setHeaderName", request.getRequestHeaderName());
        invokeOptionalSetter(configClass, config, "setHeaderValue", request.getRequestHeaderValue());
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

    private static String extractPayload(Class<?> generatorClass, Object generator) throws Exception {
        Exception lastError = null;
        String[] methodNames = new String[]{"getPayload", "getPayloadString", "formatPayload", "toString"};
        for (String methodName : methodNames) {
            try {
                return String.valueOf(generatorClass.getMethod(methodName).invoke(generator));
            } catch (NoSuchMethodException e) {
                lastError = e;
            }
        }
        try {
            java.lang.reflect.Field field = generatorClass.getDeclaredField("payload");
            field.setAccessible(true);
            return String.valueOf(field.get(generator));
        } catch (NoSuchFieldException e) {
            lastError = e;
        }
        if (lastError != null) {
            throw lastError;
        }
        throw new NoSuchMethodException("无法从 jEG 生成器中提取 payload");
    }

    private static String extractHeaderName(Class<?> configClass, Object config, String fallback) {
        String[] methodNames = new String[]{"getRespHeaderName", "getReqHeaderName", "getHeaderName"};
        for (String methodName : methodNames) {
            try {
                Object value = configClass.getMethod(methodName).invoke(config);
                if (value != null && !String.valueOf(value).trim().isEmpty()) {
                    return String.valueOf(value);
                }
            } catch (Exception ignored) {
            }
        }
        return valueOrDefault(fallback, "User-Agent");
    }

    /** UI 曾使用 SERVER_SPRING；jEG 中实际字段为 SERVER_SPRING_MVC（值为 SpringMVC）。 */
    private static String resolveConstantFieldName(String fieldName) {
        if ("SERVER_SPRING".equals(fieldName)) {
            return "SERVER_SPRING_MVC";
        }
        return fieldName;
    }
}
