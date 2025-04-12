package dev.tbm00.preprocessit.model.actioneer;

import java.util.HashMap;
import java.util.Map;

import dev.tbm00.preprocessit.model.data.enums.Action;

public class ActioneerFactory {

    private static final Map<Action, ActioneerInterface> EXECUTOR_MAP = new HashMap<>();

    static {
        EXECUTOR_MAP.put(Action.TRIM_MATCH_ALL, new TrimMatchAllActioneer());
        EXECUTOR_MAP.put(Action.TRIM_MATCH_FIRST, new TrimMatchFirstActioneer());
        EXECUTOR_MAP.put(Action.TRIM_MATCH_FROM_START, new TrimMatchFromStartActioneer());
        EXECUTOR_MAP.put(Action.TRIM_MATCH_FROM_END, new TrimMatchFromEndActioneer());
        EXECUTOR_MAP.put(Action.KEEP_MATCH, new KeepMatchActioneer());
        EXECUTOR_MAP.put(Action.APPEND, new AppendActioneer());
        EXECUTOR_MAP.put(Action.PREPEND, new PrependActioneer());
        EXECUTOR_MAP.put(Action.INSERT_AT, new InsertAtActioneer());
    }
    
    public static ActioneerInterface getActioneer(Action action) {
        return EXECUTOR_MAP.get(action);
    }
}