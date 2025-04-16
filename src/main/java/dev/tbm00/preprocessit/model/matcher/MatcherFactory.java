package dev.tbm00.preprocessit.model.matcher;

import dev.tbm00.preprocessit.model.data.enums.Condition;

public class MatcherFactory {

    public static MatcherInterface createMatcher(Condition condition, String values) {
        if (Condition.EQUALS_STRING.equals(condition)) {
            return new EqualsStringMatcher(values);
        } else if (Condition.GREATER_THAN.equals(condition)) {
            return new GreaterThanMatcher(values);
        } else if (Condition.GREATER_THAN_EQUAL_TO.equals(condition)) {
            return new GreaterThanEqualToMatcher(values);
        } else if (Condition.LESS_THAN.equals(condition)) {
            return new LessThanMatcher(values);
        } else if (Condition.LESS_THAN_EQUAL_TO.equals(condition)) {
            return new LessThanEqualToMatcher(values);
        } else if (Condition.IN_BETWEEN_INCLUSIVE.equals(condition)) {
            return new InBetweenInclusiveMatcher(values);
        } else if (Condition.IN_BETWEEN_EXCLUSIVE.equals(condition)) {
            return new InBetweenExclusiveMatcher(values);
        } else if (Condition.EQUALS_VALUE.equals(condition)) {
            return new EqualsValueMatcher(values);
        } else if (Condition.CONTAINS.equals(condition)) {
            return new ContainsMatcher(values);
        } else if (Condition.START_CONTAINS.equals(condition)) {
            return new StartContainsMatcher(values);
        } else if (Condition.END_CONTAINS.equals(condition)) {
            return new EndContainsMatcher(values);
        } else if (Condition.IS_TYPE.equals(condition)) {
            return new IsTypeMatcher(values);
        } else if (Condition.START_IS_TYPE.equals(condition)) {
            return new StartIsTypeMatcher(values);
        } else if (Condition.END_IS_TYPE.equals(condition)) {
            return new EndIsTypeMatcher(values);
        } else if (Condition.IS_EMPTY.equals(condition)) {
            return new IsEmptyMatcher();
        } else if (Condition.NOT_IN_BETWEEN_INCLUSIVE.equals(condition)) {
            return new NotInBetweenInclusiveMatcher(values);
        } else if (Condition.NOT_IN_BETWEEN_EXCLUSIVE.equals(condition)) {
            return new NotInBetweenExclusiveMatcher(values);
        } else if (Condition.NOT_EQUALS_VALUE.equals(condition)) {
            return new NotEqualsValueMatcher(values);
        } else if (Condition.NOT_EQUALS_STRING.equals(condition)) {
            return new NotEqualsStringMatcher(values);
        } else if (Condition.NOT_CONTAINS.equals(condition)) {
            return new NotContainsMatcher(values);
        } else if (Condition.NOT_START_CONTAINS.equals(condition)) {
            return new NotStartContainsMatcher(values);
        } else if (Condition.NOT_END_CONTAINS.equals(condition)) {
            return new NotEndContainsMatcher(values);
        } else if (Condition.NOT_IS_TYPE.equals(condition)) {
            return new NotIsTypeMatcher(values);
        } else if (Condition.NOT_IS_EMPTY.equals(condition)) {
            return new NotIsEmptyMatcher();
        }
        return null;
    }
}