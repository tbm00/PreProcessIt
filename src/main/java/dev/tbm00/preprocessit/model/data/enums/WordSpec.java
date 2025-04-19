package dev.tbm00.preprocessit.model.data.enums;

public class WordSpec {
    private final Word word;
    private final String parameter;

    public WordSpec(Word word, String parameter) {
        this.word = word;
        this.parameter = parameter;
    }

    public Word getWord() {
        return word;
    }

    public String getParameter() {
        return parameter;
    }

    @Override
    public String toString() {
        if (parameter != null && !parameter.isEmpty()) {
            return word.name() + "(" + parameter + ")";
        }
        return word.name();
    }
}
