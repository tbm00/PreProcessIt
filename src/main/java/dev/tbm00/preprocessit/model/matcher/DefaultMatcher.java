package dev.tbm00.preprocessit.model.matcher;

public class DefaultMatcher implements QualifierMatcher {
    @Override
    public boolean match(String token, String previousToken, String nextToken) {
        return false;
    }

    @Override
    public String extract(String token) {
        return token;
    }
}
