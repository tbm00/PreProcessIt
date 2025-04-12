package dev.tbm00.preprocessit.model.matcher;

import dev.tbm00.preprocessit.data.enums.Action;
import dev.tbm00.preprocessit.data.enums.Location;

public class DefaultMatcher implements QualifierMatcher {

    public DefaultMatcher() {}

    @Override
    public boolean process(String token, String previousToken, String nextToken, String values, Location[] locations, Action[] qualifiedActions, Action[] unqualifiedActions) {
        String[] validValues = values.split("\\|");

        for (Location location : locations) {
            for (String value : validValues) {
                // if match, trigger qualifiedActions and return true
            }
        }

        // else trigger unqualifiedActions and return true
        return true;
    }

    @Override
    public boolean match(String token, Location location, String value) {
        // Determine match based on the Matcher's condition ENUM
        return false;
    }

    @Override
    public String standardize(String token) {
        return token;
    }
}
