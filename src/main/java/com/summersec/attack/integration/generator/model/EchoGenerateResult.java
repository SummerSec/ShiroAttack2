package com.summersec.attack.integration.generator.model;

public class EchoGenerateResult {
    private boolean success;
    private String source;
    private String payload;
    private String requestHeaderName;
    private String message;
    private String serverType;
    private String modelType;
    private String formatType;

    public static EchoGenerateResult ok(String source, String payload, String requestHeaderName) {
        EchoGenerateResult result = new EchoGenerateResult();
        result.success = true;
        result.source = source;
        result.payload = payload;
        result.requestHeaderName = requestHeaderName;
        return result;
    }

    public static EchoGenerateResult fail(String source, String message) {
        EchoGenerateResult result = new EchoGenerateResult();
        result.success = false;
        result.source = source;
        result.message = message;
        return result;
    }

    public EchoGenerateResult withSelection(String serverType, String modelType, String formatType) {
        this.serverType = serverType;
        this.modelType = modelType;
        this.formatType = formatType;
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

    public String getRequestHeaderName() {
        return requestHeaderName;
    }

    public String getMessage() {
        return message;
    }

    public String getServerType() {
        return serverType;
    }

    public String getModelType() {
        return modelType;
    }

    public String getFormatType() {
        return formatType;
    }
}
