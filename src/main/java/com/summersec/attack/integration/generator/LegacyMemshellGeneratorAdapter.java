package com.summersec.attack.integration.generator;

import com.summersec.attack.deser.plugins.servlet.MemBytes;
import com.summersec.attack.integration.generator.model.MemshellGenerateRequest;
import com.summersec.attack.integration.generator.model.MemshellGenerateResult;

public class LegacyMemshellGeneratorAdapter implements MemshellGeneratorAdapter {
    @Override
    public MemshellGenerateResult generate(MemshellGenerateRequest request) {
        try {
            String payload = MemBytes.getBytes(request.getOption());
            if (payload == null || payload.isEmpty()) {
                return MemshellGenerateResult.fail("Legacy", "未找到对应内存马模板");
            }
            return MemshellGenerateResult.ok("Legacy", payload, "", "");
        } catch (Exception e) {
            return MemshellGenerateResult.fail("Legacy", e.getMessage());
        }
    }
}
