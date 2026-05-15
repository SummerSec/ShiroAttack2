package com.summersec.attack.CLI;

import com.summersec.attack.utils.OutputSink;
import javafx.scene.control.TextArea;

public class ConsoleTextArea extends TextArea {
    private final OutputSink sink;
    private final String level;

    public ConsoleTextArea(OutputSink sink) {
        this(sink, "info");
    }

    public ConsoleTextArea(OutputSink sink, String level) {
        this.sink = sink;
        this.level = level;
        setEditable(false);
        setWrapText(true);
    }

    @Override
    public void appendText(String text) {
        super.appendText(text);
        if (sink != null && text != null) {
            String trimmed = text.trim();
            if (trimmed.isEmpty()) return;
            switch (level) {
                case "error": sink.error(trimmed); break;
                case "warn":  sink.warn(trimmed); break;
                case "success": sink.success(trimmed); break;
                default: sink.info(trimmed);
            }
        }
    }
}
