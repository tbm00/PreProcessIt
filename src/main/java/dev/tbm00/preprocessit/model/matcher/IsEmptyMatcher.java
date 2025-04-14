package dev.tbm00.preprocessit.model.matcher;

public class IsEmptyMatcher implements MatcherInterface {
    @Override
    public String match(String word) {
        if (word.isEmpty()) return "null";
        else return "";
    }
}
