package com.summersec.attack.utils;

public class CliOutputSink implements OutputSink {
    private final boolean jsonMode;

    public CliOutputSink(boolean jsonMode) {
        this.jsonMode = jsonMode;
    }

    @Override
    public void info(String message) { emit("info", message); }

    @Override
    public void success(String message) { emit("success", message); }

    @Override
    public void warn(String message) { emit("warn", message); }

    @Override
    public void error(String message) { emit("error", message); }

    @Override
    public void raw(String message) {
        if (jsonMode) { System.out.println(jsonEscape(message)); }
        else { System.out.println(message); }
    }

    private void emit(String level, String message) {
        if (jsonMode) {
            String escaped = jsonEscape(message);
            System.out.println("{\"level\":\"" + level + "\",\"msg\":\"" + escaped + "\"}");
        } else {
            String prefix;
            switch (level) {
                case "success": prefix = "[+] "; break;
                case "warn":    prefix = "[!] "; break;
                case "error":   prefix = "[-] "; break;
                default:        prefix = "[*] "; break;
            }
            System.out.println(prefix + message);
        }
    }

    private static String jsonEscape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\n");  break;
                case '\r': sb.append("\r");  break;
                case '\t': sb.append("\t");  break;
                default:   sb.append(c);
            }
        }
        return sb.toString();
    }
}
