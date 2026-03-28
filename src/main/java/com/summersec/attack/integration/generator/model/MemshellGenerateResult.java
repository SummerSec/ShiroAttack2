package com.summersec.attack.integration.generator.model;

public class MemshellGenerateResult {
    private boolean success;
    private String source;
    private String payload;
    private String basicInfo;
    private String debugInfo;
    private String message;
    private String serverType;
    private String toolType;
    private String shellType;
    private String formatType;
    private String gadgetType;
    private String fallbackSource;
    private String fallbackMessage;

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

    public static MemshellGenerateResult okWithFallback(String source, String payload, String basicInfo, String debugInfo,
                                                         String fallbackSource, String fallbackMessage) {
        MemshellGenerateResult result = ok(source, payload, basicInfo, debugInfo);
        result.fallbackSource = fallbackSource;
        result.fallbackMessage = fallbackMessage;
        return result;
    }

    public MemshellGenerateResult withSelection(String toolType, String serverType, String shellType, String formatType, String gadgetType) {
        this.toolType = toolType;
        this.serverType = serverType;
        this.shellType = shellType;
        this.formatType = formatType;
        this.gadgetType = gadgetType;
        return this;
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

    public String getFallbackSource() {
        return fallbackSource;
    }

    public String getFallbackMessage() {
        return fallbackMessage;
    }

    public String getServerType() {
        return serverType;
    }

    public String getToolType() {
        return toolType;
    }

    public String getShellType() {
        return shellType;
    }

    public String getFormatType() {
        return formatType;
    }

    public String getGadgetType() {
        return gadgetType;
    }
}
