package dev.tbm00.preprocessit.model.matcher;

public class ContainsMatcher implements MatcherInterface {
    private String[] substrings;
    
    public ContainsMatcher(String values) {
        this.substrings = values.split("\\|");
    }
    
    @Override
    public String match(String word) {
        String upperWord = word.toUpperCase();

        for (String s : substrings) {
            String upperCandidate = s.toUpperCase();
            int idx = upperWord.indexOf(upperCandidate);
            if (idx != -1) {
                return word.substring(idx, idx+upperCandidate.length());
            }
        }
        
        return "";
    }
}