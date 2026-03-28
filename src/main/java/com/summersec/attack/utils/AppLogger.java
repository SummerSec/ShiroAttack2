package com.summersec.attack.utils;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppLogger {
    private static final Object LOCK = new Object();
    private static final File LOG_DIR = new File("logs");
    private static final SimpleDateFormat TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final String SESSION_FILE_NAME = "shiro_attack-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".log";
    private static final File LOG_FILE = new File(LOG_DIR, SESSION_FILE_NAME);

    public static void info(String message) {
        write("INFO", message, null);
    }

    public static void warn(String message) {
        write("WARN", message, null);
    }

    public static void error(String message) {
        write("ERROR", message, null);
    }

    public static void error(String message, Throwable throwable) {
        write("ERROR", message, throwable);
    }

    private static void write(String level, String message, Throwable throwable) {
        synchronized (LOCK) {
            PrintWriter writer = null;
            try {
                if (!LOG_DIR.exists()) {
                    LOG_DIR.mkdirs();
                }
                writer = new PrintWriter(new OutputStreamWriter(new java.io.FileOutputStream(LOG_FILE, true), StandardCharsets.UTF_8));
                writer.println(TS.format(new Date()) + " [" + level + "] " + safe(message));
                if (throwable != null) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    throwable.printStackTrace(pw);
                    pw.flush();
                    writer.println(sw.toString());
                }
                writer.flush();
            } catch (Exception ignored) {
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        }
    }

    private static String safe(String message) {
        return message == null ? "null" : message;
    }
}
