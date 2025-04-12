package dev.tbm00.preprocessit.model.matcher;

public class InBetweenExclusiveMatcher implements MatcherInterface {
    private double min;
    private double max;
    
    public InBetweenExclusiveMatcher(String values) {
        // expects "min,max"
        String[] parts = values.split(",");
        this.min = Double.parseDouble(parts[0]);
        this.max = Double.parseDouble(parts[1]);
    }
    
    @Override
    public String match(String word) {
        try {
            double number = Double.parseDouble(word);
            return (number > min && number < max) ? word : "";
        } catch (NumberFormatException e) {
            return "";
        }
    }
}