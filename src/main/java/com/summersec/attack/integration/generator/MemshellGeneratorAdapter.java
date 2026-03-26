package com.summersec.attack.integration.generator;

import com.summersec.attack.integration.generator.model.MemshellGenerateRequest;
import com.summersec.attack.integration.generator.model.MemshellGenerateResult;

public interface MemshellGeneratorAdapter {
    MemshellGenerateResult generate(MemshellGenerateRequest request);
}
