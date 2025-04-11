package dev.tbm00.preprocessit.model.matcher;

public interface QualifierMatcher {
    // Returns true once the qualifier finishes processing
    boolean process(String token, String previousToken, String nextToken, String locations, String values, String[] qualifiedActions, String[] unqualifiedActions);
    
    // Extracts or transforms the token into a standardized attribute value based on the qualifier.
    String standardize(String token);
}