package dev.tbm00.preprocessit.model.matcher;

public class NotEqualsStringMatcher implements MatcherInterface {
    private String[] validValues;
    
    public NotEqualsStringMatcher(String values) {
        this.validValues = values.split("\\|");
    }
    
    @Override
    public String match(String word) {
        for (String value : validValues) {
            if (word.equals(value)) {
                return "";
            }
        }
        return word;
    }
}