package dev.tbm00.preprocessit.model.matcher;

public class NotContainsMatcher implements MatcherInterface {
    private String[] substrings;
    
    public NotContainsMatcher(String values) {
        values.toUpperCase();
        this.substrings = values.split("\\|");
    }
    
    @Override
    public String match(String word) {
        String lowerWord = word.toUpperCase();
        for (String s : substrings) {
            if (lowerWord.contains(s)) {
                return "";
            }
        }
        return word;
    }
}