package dev.tbm00.preprocessit.model.matcher;

public interface QualifierMatcher {
    // Returns true if the token (or token fragments) satisfy the qualifier condition.
    boolean match(String token, String previousToken, String nextToken);
    
    // Extracts or transforms the token into a standardized attribute value based on the qualifier.
    String extract(String token);
}