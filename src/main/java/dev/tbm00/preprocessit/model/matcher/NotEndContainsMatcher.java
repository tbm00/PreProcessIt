package dev.tbm00.preprocessit.model.matcher;

public class NotEndContainsMatcher implements MatcherInterface {
    private String[] substrings;
    
    public NotEndContainsMatcher(String values) {
        values.toUpperCase();
        this.substrings = values.split("\\|");
    }
    
    @Override
    public String match(String word) {
        String lowerWord = word.toUpperCase();
        for (String s : substrings) {
            if (lowerWord.endsWith(s)) {
                return "";
            }
        }
        return word;
    }
}