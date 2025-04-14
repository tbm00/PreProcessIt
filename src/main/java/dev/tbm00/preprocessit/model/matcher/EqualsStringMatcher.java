package dev.tbm00.preprocessit.model.matcher;

public class EqualsStringMatcher implements MatcherInterface {

    private String[] validValues;
    
    public EqualsStringMatcher(String values) {
        values.toUpperCase();
        this.validValues = values.split("\\|");
    }

    @Override
    public String match(String word) {
        String upperWord = word.toUpperCase();

        for (String v : validValues) {
            String upperCandidate = v.toUpperCase();
            if (upperWord.equals(upperCandidate)) {
                return word;
            }
        }
        
        return "";
    }
}
