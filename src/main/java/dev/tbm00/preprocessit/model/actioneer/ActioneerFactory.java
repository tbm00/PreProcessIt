package dev.tbm00.preprocessit.model.actioneer;

import java.util.HashMap;
import java.util.Map;

import dev.tbm00.preprocessit.model.data.enums.Action;

public class ActioneerFactory {

    private static final Map<Action, ActioneerInterface> EXECUTOR_MAP = new HashMap<>();

    static {
        EXECUTOR_MAP.put(Action.TOKEN_TRIM_MATCH_FROM_START, new TokenTrimMatchFromStartActioneer());
        EXECUTOR_MAP.put(Action.TOKEN_TRIM_MATCH_FROM_END, new TokenTrimMatchFromEndActioneer());
        EXECUTOR_MAP.put(Action.TOKEN_KEEP_MATCH, new TokenKeepMatchActioneer());
        EXECUTOR_MAP.put(Action.TOKEN_APPEND, new TokenAppendActioneer());
        EXECUTOR_MAP.put(Action.TOKEN_PREPEND, new TokenPrependActioneer());
        EXECUTOR_MAP.put(Action.TOKEN_INSERT_AT, new TokenInsertAtActioneer());
    }
    
    public static ActioneerInterface getActioneer(Action action) {
        return EXECUTOR_MAP.get(action);
    }
}