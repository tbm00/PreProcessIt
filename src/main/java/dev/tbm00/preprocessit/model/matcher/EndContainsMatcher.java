package dev.tbm00.preprocessit.model.matcher;

public class EndContainsMatcher implements MatcherInterface {
    private String[] substrings;
    
    public EndContainsMatcher(String values) {
        values.toUpperCase();
        this.substrings = values.split("\\|");
    }
    
    @Override
    public String match(String word) {
        String lowerWord = word.toUpperCase();
        for (String s : substrings) {
            if (lowerWord.endsWith(s)) {
                return s;
            }
        }
        return "";
    }
}