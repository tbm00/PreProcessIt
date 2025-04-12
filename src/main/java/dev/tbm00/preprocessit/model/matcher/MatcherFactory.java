package dev.tbm00.preprocessit.model.matcher;

import dev.tbm00.preprocessit.model.data.enums.Condition;

public class MatcherFactory {

    public static MatcherInterface createMatcher(Condition condition, String values) {
        // Instantiate the proper matcher depending on condition ENUM

        if (Condition.EQUALS_STRING.equals(condition)) {
            return new EqualsStringMatcher(values);
        }
        
        // TODO add additional matchers for each condition
        /*
         * GREATER_THAN
         * GREATER_THAN_EQUAL_TO
         * LESS_THAN
         * LESS_THAN_EQUAL_TO
         * IN_BETWEEN_INCLUSIVE
         * IN_BETWEEN_EXCLUSIVE
         * EQUALS_VALUE
         * EQUALS_STRING (ALREADY DONE)
         * CONTAINS
         * START_CONTAINS
         * END_CONTAINS
         * IS_TYPE
         * NOT_IN_BETWEEN_INCLUSIVE
         * NOT_IN_BETWEEN_EXCLUSIVE
         * NOT_EQUALS_VALUE
         * NOT_EQUALS_STRING
         * NOT_EQUALS_STRING_IGNORE_CASE
         * NOT_CONTAINS
         * NOT_START_CONTAINS
         * NOT_END_CONTAINS
         * NOT_IS_TYPE
         */
        
        return null;
    }
}