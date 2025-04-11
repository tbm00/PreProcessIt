package dev.tbm00.preprocessit.model.matcher;

public class DefaultMatcher implements QualifierMatcher {
    private String[] validLocations;
    private String[] validValues;

    public DefaultMatcher(String location, String value) {
        this.validLocations = location.split("\\|");
        this.validValues = value.split("\\|");
    }

    @Override
    public boolean match(String token, String previousToken, String nextToken) {
        for (String location : validLocations) {
            for (String value : validValues) {
                // if match, return true
            }
        }
        // If no match, work with next node to find a match and return true when found
        // else work with prior node to find match and return true when found
        // else return false
        return false;
    }

    @Override
    public String standardize(String token) {
        return token;
    }
}
