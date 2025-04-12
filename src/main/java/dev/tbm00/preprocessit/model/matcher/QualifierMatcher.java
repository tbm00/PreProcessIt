package dev.tbm00.preprocessit.model.matcher;

import dev.tbm00.preprocessit.data.enums.Action;
import dev.tbm00.preprocessit.data.enums.Location;

public interface QualifierMatcher {
    // Returns true once the qualifier finishes processing
    abstract boolean process(String token, String previousToken, String nextToken, String values, Location[] locations, Action[] qualifiedActions, Action[] unqualifiedActions);
    
    // Returns true if the token's current location matches the iteration's current value
    abstract boolean match(String token, Location location, String value);

    // Extracts or transforms the token into a standardized attribute value based on the qualifier.
    abstract String standardize(String token);
}