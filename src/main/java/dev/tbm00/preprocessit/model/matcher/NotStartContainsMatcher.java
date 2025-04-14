package dev.tbm00.preprocessit.model.matcher;

public class NotStartContainsMatcher implements MatcherInterface {
    private String[] substrings;
    
    public NotStartContainsMatcher(String values) {
        values.toUpperCase();
        this.substrings = values.split("\\|");
    }
    
    @Override
    public String match(String word) {
        String upperWord = word.toUpperCase();

        for (String s : substrings) {
            String upperCandidate = s.toUpperCase();
            if (upperWord.startsWith(upperCandidate)) {
                return "";
            }
        }
        
        return word;
    }
}