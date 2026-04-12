package com.summersec.attack.integration.generator.model;

public class EchoGenerateRequest {
    private String serverType;
    private String modelType;
    private String formatType;
    private String requestHeaderName;
    private String requestHeaderValue;
    /** jEG MODEL_CMD：嵌入模板的命令（写入 jEGConfig.setReqHeaderName） */
    private String jegCmdText;
    /** jEG MODEL_CODE：嵌入模板的代码（写入 jEGConfig.setReqParamName） */
    private String jegCodeText;

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

    public String getJegCmdText() {
        return jegCmdText;
    }

    public void setJegCmdText(String jegCmdText) {
        this.jegCmdText = jegCmdText;
    }

    public String getJegCodeText() {
        return jegCodeText;
    }

    public void setJegCodeText(String jegCodeText) {
        this.jegCodeText = jegCodeText;
    }
}
