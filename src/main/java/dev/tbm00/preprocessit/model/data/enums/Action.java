package dev.tbm00.preprocessit.model.data.enums;

public enum Action {
    NEW_TOKEN_FROM_MATCH,
    NEW_TOKEN_FROM_UNMATCHED,
    TRIM_MATCH_FROM_LEFT_NEIGHBOR,
    TRIM_MATCH_FROM_RIGHT_NEIGHBOR,
    TRIM_MATCH_ALL,
    TRIM_MATCH_FIRST,
    TRIM_MATCH_START,
    TRIM_MATCH_END,
    TRIM_UNMATCHED_ALL,
    TRIM_UNMATCHED_FIRST,
    TRIM_UNMATCHED_START,
    TRIM_UNMATCHED_END,
    REPLACE_MATCH_ALL,
    REPLACE_MATCH_FIRST,
    KEEP_MATCH,
    APPEND,
    PREPEND,
    INSERT_AT,
    REPLACE_ALL,
    REPLACE_FIRST,
    CONTINUE,
    CONTINUE_AND_SKIP_NEXT_QUALIFIER,
    EXIT_TO_NEXT_TOKEN_ITERATION,
    EXIT_TO_NEXT_ATTRIBUTE_ITERATION,
    TRY_NEIGHBORS,
    SHIP,
}