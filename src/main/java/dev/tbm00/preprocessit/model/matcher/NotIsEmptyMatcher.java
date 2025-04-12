package dev.tbm00.preprocessit.model.matcher;

public class NotIsEmptyMatcher implements MatcherInterface {
    @Override
    public String match(String word) {
        if (word.isEmpty()) return "";
        else return word;
    }
}
