package dev.tbm00.preprocessit.model.data;

import java.util.List;

public class LineResult {
    public final int lineNumber;
    public final String output;
    public final List<String> log;

    public LineResult(int lineNumber, String output, List<String> log) {
        this.lineNumber = lineNumber;
        this.output = output;
        this.log = log;
    }
}