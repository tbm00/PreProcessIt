package dev.tbm00.preprocessit.model.matcher;

public class EndContainsMatcher implements QualifierMatcher {
    private String[] validSuffixes;

    public EndContainsMatcher(String value) {
        // Split the valid suffixes (e.g., "MS|ms|milliseconds") into an array.
        this.validSuffixes = value.split("\\|");
    }

    @Override
    public boolean match(String token, String previousToken, String nextToken) {
        for (String suffix : validSuffixes) {
            // Do a case-insensitive check if token ends with any of the valid suffixes.
            if (token.toLowerCase().endsWith(suffix.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String extract(String token) {
        // Here you might want to normalize the token by, for example, ensuring the suffix is uppercase.
        // This is a simple example.
        for (String suffix : validSuffixes) {
            if (token.toLowerCase().endsWith(suffix.toLowerCase())) {
                // Remove unwanted parts or standardize the format.
                // For demonstration, simply return the token with a standardized suffix:
                String standardizedSuffix = suffix.toUpperCase();
                String basePart = token.substring(0, token.length() - suffix.length());
                return basePart + standardizedSuffix;
            }
        }
        return token;
    }
}
