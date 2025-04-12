package dev.tbm00.preprocessit.model.matcher;

public class LessThanMatcher implements MatcherInterface {
    private double threshold;
    
    public LessThanMatcher(String value) {
        this.threshold = Double.parseDouble(value);
    }
    
    @Override
    public String match(String word) {
        try {
            double number = Double.parseDouble(word);
            return (number < threshold) ? word : "";
        } catch (NumberFormatException e) {
            return "";
        }
    }
}