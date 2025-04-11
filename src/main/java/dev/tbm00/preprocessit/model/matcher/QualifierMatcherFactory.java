package dev.tbm00.preprocessit.model.matcher;

public class QualifierMatcherFactory {
    public static QualifierMatcher createMatcher(String condition) {
        // Depending on the input rule parts (location, condition, value), instantiate the proper matcher.
        //if (condition.contains("<ExampleCondition>")) {
        //    return new ExampleMatcher(location, value);
        //}
        
        // Add additional conditions for different types of qualifiers.
        
        // Fallback matcher if no specific rule matches:
        return new DefaultMatcher();
    }
}