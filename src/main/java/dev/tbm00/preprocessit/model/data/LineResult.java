package dev.tbm00.preprocessit.model.data;

public class LineResult {
    public final int lineNumber;
    public final String output;

    public LineResult(int lineNumber, String output) {
        this.lineNumber = lineNumber;
        this.output = output;
    }
}