package com.summersec.attack.utils;

public interface OutputSink {
    void info(String message);
    void success(String message);
    void warn(String message);
    void error(String message);
    void raw(String message);
}
