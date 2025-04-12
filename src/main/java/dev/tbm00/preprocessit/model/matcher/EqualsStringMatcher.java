package dev.tbm00.preprocessit.model.matcher;

public class EqualsStringMatcher implements MatcherInterface {

    private String[] validValues;
    
    public EqualsStringMatcher(String values) {
        this.validValues = values.split("\\|");
    }

    @Override
    public String match(String word) {
        for (String value : validValues) {
            if (word.equals(value)) return value;
        }

        // else
        return "";
    }
}
