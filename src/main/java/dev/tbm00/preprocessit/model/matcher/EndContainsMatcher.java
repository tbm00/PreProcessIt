package dev.tbm00.preprocessit.model.matcher;

public class EndContainsMatcher implements MatcherInterface {
    private String[] substrings;
    
    public EndContainsMatcher(String values) {
        values.toUpperCase();
        this.substrings = values.split("\\|");
    }

    @Override
    public String match(String word) {
        String upperWord = word.toUpperCase();

        for (String s : substrings) {
            String upperCandidate = s.toUpperCase();
            if (upperWord.endsWith(upperCandidate)) {
                int len = s.length();
                return word.substring(word.length()-len);
            }
        }
        
        return "";
    }
}