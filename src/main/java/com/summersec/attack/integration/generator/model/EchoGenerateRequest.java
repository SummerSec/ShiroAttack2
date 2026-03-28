package com.summersec.attack.integration.generator.model;

public class EchoGenerateRequest {
    private String serverType;
    private String modelType;
    private String formatType;
    private String requestHeaderName;
    private String requestHeaderValue;

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getFormatType() {
        return formatType;
    }

    public void setFormatType(String formatType) {
        this.formatType = formatType;
    }

    public String getRequestHeaderName() {
        return requestHeaderName;
    }

    public void setRequestHeaderName(String requestHeaderName) {
        this.requestHeaderName = requestHeaderName;
    }

    public String getRequestHeaderValue() {
        return requestHeaderValue;
    }

    public void setRequestHeaderValue(String requestHeaderValue) {
        this.requestHeaderValue = requestHeaderValue;
    }
}
