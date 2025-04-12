package dev.tbm00.preprocessit.model.matcher;

public class NotEqualsStringMatcher implements MatcherInterface {
    private String[] validValues;
    
    public NotEqualsStringMatcher(String values) {
        this.validValues = values.split("\\|");
    }
    
    @Override
    public String match(String word) {
        String upperWord = word.toUpperCase();

        for (String v : validValues) {
            String upperCandidate = v.toUpperCase();
            if (upperWord.equals(upperCandidate)) {
                return "";
            }
        }
        
        return word;
    }
}