package dev.tbm00.preprocessit.model.actioneer;

import java.util.HashMap;
import java.util.Map;

import dev.tbm00.preprocessit.model.data.enums.Action;

public class ActioneerFactory {

    private static final Map<Action, ActioneerInterface> EXECUTOR_MAP = new HashMap<>();

    static {
        EXECUTOR_MAP.put(Action.TRIM_UNMATCHED_ALL, new TrimUnmatchedAllActioneer());
        EXECUTOR_MAP.put(Action.TRIM_UNMATCHED_FIRST, new TrimUnmatchedFirstActioneer());
        EXECUTOR_MAP.put(Action.TRIM_UNMATCHED_START, new TrimUnmatchedStartActioneer());
        EXECUTOR_MAP.put(Action.TRIM_UNMATCHED_END, new TrimUnmatchedEndActioneer());
        EXECUTOR_MAP.put(Action.TRIM_MATCH_ALL, new TrimMatchAllActioneer());
        EXECUTOR_MAP.put(Action.TRIM_MATCH_FIRST, new TrimMatchFirstActioneer());
        EXECUTOR_MAP.put(Action.TRIM_MATCH_START, new TrimMatchStartActioneer());
        EXECUTOR_MAP.put(Action.TRIM_MATCH_END, new TrimMatchEndActioneer());
        EXECUTOR_MAP.put(Action.REPLACE_MATCH_ALL, new ReplaceMatchAllActioneer());
        EXECUTOR_MAP.put(Action.REPLACE_MATCH_FIRST, new ReplaceMatchFirstActioneer());
        EXECUTOR_MAP.put(Action.TRIM_MATCH_FROM_LEFT_NEIGHBOR, new TrimMatchEndActioneer());
        EXECUTOR_MAP.put(Action.TRIM_MATCH_FROM_RIGHT_NEIGHBOR, new TrimMatchStartActioneer());
        EXECUTOR_MAP.put(Action.TRIM_MATCH_FROM_TOKEN, new TrimMatchAllActioneer());
        EXECUTOR_MAP.put(Action.TRIM_UNMATCHED_FROM_TOKEN, new TrimMatchAllActioneer());
        EXECUTOR_MAP.put(Action.NEW_TOKEN_FROM_UNMATCHED, new TrimMatchAllActioneer());
        EXECUTOR_MAP.put(Action.KEEP_MATCH, new KeepMatchActioneer());
        EXECUTOR_MAP.put(Action.APPEND, new AppendActioneer());
        EXECUTOR_MAP.put(Action.PREPEND, new PrependActioneer());
        EXECUTOR_MAP.put(Action.INSERT_AT, new InsertAtActioneer());
        EXECUTOR_MAP.put(Action.REPLACE_ALL, new ReplaceAllActioneer());
        EXECUTOR_MAP.put(Action.REPLACE_FIRST, new ReplaceFirstActioneer());
        EXECUTOR_MAP.put(Action.ROUND, new RoundActioneer());
        EXECUTOR_MAP.put(Action.FORMAT_NUMBER, new FormatNumberActioneer());
        EXECUTOR_MAP.put(Action.SET_CASING, new SetCasingActioneer());
    }
    
    public static ActioneerInterface getActioneer(Action action) {
        return EXECUTOR_MAP.get(action);
    }
}