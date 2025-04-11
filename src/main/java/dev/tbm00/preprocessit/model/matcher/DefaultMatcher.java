package dev.tbm00.preprocessit.model.matcher;

public class DefaultMatcher implements QualifierMatcher {

    public DefaultMatcher() {}

    @Override
    public boolean process(String token, String previousToken, String nextToken, String locations, String values, String[] qualifiedActions, String[] unqualifiedActions) {
        String[] validLocations = locations.split("\\|");
        String[] validValues = values.split("\\|");

        for (String location : validLocations) {
            for (String value : validValues) {
                // if match, trigger qualifiedActions and return true
            }
        }

        // else trigger unqualifiedActions and return true
        return true;
    }

    @Override
    public String standardize(String token) {
        return token;
    }
}
