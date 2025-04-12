package dev.tbm00.preprocessit.model.matcher;

public class StartContainsMatcher implements MatcherInterface {
    private String[] substrings;
    
    public StartContainsMatcher(String values) {
        values.toUpperCase();
        this.substrings = values.split("\\|");
    }
    
    @Override
    public String match(String word) {
        String lowerWord = word.toUpperCase();
        for (String s : substrings) {
            if (lowerWord.startsWith(s)) {
                return s;
            }
        }
        return "";
    }
}