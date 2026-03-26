package com.summersec.attack.integration.generator;

import com.summersec.attack.integration.generator.model.EchoGenerateRequest;
import com.summersec.attack.integration.generator.model.EchoGenerateResult;

public interface EchoGeneratorAdapter {
    EchoGenerateResult generate(EchoGenerateRequest request);
}
