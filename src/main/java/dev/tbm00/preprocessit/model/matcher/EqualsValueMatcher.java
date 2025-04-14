package dev.tbm00.preprocessit.model.matcher;

public class EqualsValueMatcher implements MatcherInterface {
    private double expected;
    
    public EqualsValueMatcher(String value) {
        this.expected = Double.parseDouble(value);
    }
    
    @Override
    public String match(String word) {
        try {
            double number = Double.parseDouble(word);
            return (Double.compare(number, expected) == 0) ? word : "";
        } catch (NumberFormatException e) {
            return "";
        }
    }
}