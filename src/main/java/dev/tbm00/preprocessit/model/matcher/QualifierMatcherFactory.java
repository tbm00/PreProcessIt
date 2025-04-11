package dev.tbm00.preprocessit.model.matcher;

public class QualifierMatcherFactory {
    public static QualifierMatcher createMatcher(String location, String condition, String value) {
        // Depending on the input rule parts (location, condition, value), instantiate the proper matcher.
        // For demonstration, here's a simple example handling an "END CONTAINS" condition:
        if ("END".equalsIgnoreCase(location) && condition.contains("CONTAINS")) {
            return new EndContainsMatcher(value);
        }
        // Add additional conditions for different types of qualifiers.
        
        // Fallback matcher if no specific rule matches:
        return new DefaultMatcher();
    }
}