package com.summersec.attack.integration.generator.model;

public class MemshellGenerateResult {
    private boolean success;
    private String source;
    private String payload;
    private String basicInfo;
    private String debugInfo;
    private String message;

    public static MemshellGenerateResult ok(String source, String payload, String basicInfo, String debugInfo) {
        MemshellGenerateResult result = new MemshellGenerateResult();
        result.success = true;
        result.source = source;
        result.payload = payload;
        result.basicInfo = basicInfo;
        result.debugInfo = debugInfo;
        return result;
    }

    public static MemshellGenerateResult fail(String source, String message) {
        MemshellGenerateResult result = new MemshellGenerateResult();
        result.success = false;
        result.source = source;
        result.message = message;
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getSource() {
        return source;
    }

    public String getPayload() {
        return payload;
    }

    public String getBasicInfo() {
        return basicInfo;
    }

    public String getDebugInfo() {
        return debugInfo;
    }

    public String getMessage() {
        return message;
    }
}
