package dev.tbm00.preprocessit.model.matcher;

public class StartContainsMatcher implements MatcherInterface {
    private String[] substrings;
    
    public StartContainsMatcher(String values) {
        values.toUpperCase();
        this.substrings = values.split("\\|");
    }

    @Override
    public String match(String word) {
        String upperWord = word.toUpperCase();

        for (String s : substrings) {
            String upperCandidate = s.toUpperCase();
            if (upperWord.startsWith(upperCandidate)) {
                int len = s.length();
                return word.substring(0, len);
            }
        }
        
        return "";
    }
}