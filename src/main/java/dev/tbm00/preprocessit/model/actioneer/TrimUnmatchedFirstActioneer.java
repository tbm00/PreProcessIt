package dev.tbm00.preprocessit.model.actioneer;

import java.util.List;

import dev.tbm00.preprocessit.model.data.enums.Action;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class TrimUnmatchedFirstActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString, List<String> log) {

        if (matchedString != null && !matchedString.isEmpty()) {
            ActioneerInterface actioneer = ActioneerFactory.getActioneer(Action.TRIM_MATCH_FIRST);
            word = actioneer.execute(word, actionSpec, matchedString, log);
        }

        log.add("      (TrimUnmatchedFirstActioneer: " + word + ")");
        return word;
    }
}
