package dev.tbm00.preprocessit.model.actioneer;

import java.util.HashMap;
import java.util.Map;

import dev.tbm00.preprocessit.model.data.enums.Action;

public class ActioneerFactory {

    private static final Map<Action, ActioneerInterface> EXECUTOR_MAP = new HashMap<>();

    static {
        EXECUTOR_MAP.put(Action.TOKEN_TRIM_MATCH, new TokenTrimMatchActioneer());

        // TODO expand map with additional interfaces for actions
        /*
         * TOKEN_TRIM_MATCH (ALREADY DONE)
         * TOKEN_KEEP_MATCH
         * TOKEN_APPEND_END
         * TOKEN_APPEND_START
         * TOKEN_APPEND_AT
         */
    }
    
    public static ActioneerInterface getActioneer(Action action) {
        return EXECUTOR_MAP.get(action);
    }
}