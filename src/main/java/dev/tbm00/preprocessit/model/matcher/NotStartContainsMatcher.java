package dev.tbm00.preprocessit.model.matcher;

public class NotStartContainsMatcher implements MatcherInterface {
    private String[] substrings;
    
    public NotStartContainsMatcher(String values) {
        values.toUpperCase();
        this.substrings = values.split("\\|");
    }
    
    @Override
    public String match(String word) {
        String lowerWord = word.toUpperCase();
        for (String s : substrings) {
            if (lowerWord.startsWith(s)) {
                return "";
            }
        }
        return word;
    }
}