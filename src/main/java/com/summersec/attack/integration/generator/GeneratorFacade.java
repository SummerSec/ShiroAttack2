package com.summersec.attack.integration.generator;

import com.summersec.attack.integration.generator.model.EchoGenerateRequest;
import com.summersec.attack.integration.generator.model.EchoGenerateResult;
import com.summersec.attack.integration.generator.model.MemshellGenerateRequest;
import com.summersec.attack.integration.generator.model.MemshellGenerateResult;

public class GeneratorFacade {
    private final EchoGeneratorAdapter jegAdapter = new JegEchoGeneratorAdapter();
    private final MemshellGeneratorAdapter jmgAdapter = new JmgMemshellGeneratorAdapter();
    private final MemshellGeneratorAdapter legacyMemshellAdapter = new LegacyMemshellGeneratorAdapter();

    public EchoGenerateResult generateEcho(String source, EchoGenerateRequest request) {
        if ("jEG".equalsIgnoreCase(source)) {
            return jegAdapter.generate(request);
        }
        return EchoGenerateResult.fail("Legacy", "Legacy 模式不直接生成回显字符串，请使用原有回显链路");
    }

    public MemshellGenerateResult generateMemshell(String source, MemshellGenerateRequest request) {
        if ("jMG".equalsIgnoreCase(source)) {
            return jmgAdapter.generate(request);
        }
        return legacyMemshellAdapter.generate(request);
    }
}
