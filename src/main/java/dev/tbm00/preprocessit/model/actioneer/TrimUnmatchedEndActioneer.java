package dev.tbm00.preprocessit.model.actioneer;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.enums.Action;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class TrimUnmatchedEndActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString) {

        if (matchedString != null && !matchedString.isEmpty()) {
            ActioneerInterface actioneer = ActioneerFactory.getActioneer(Action.TRIM_MATCH_END);
            word = actioneer.execute(word, actionSpec, matchedString);
        }

        StaticUtil.log("      (TrimUnmatchedEndActioneer: " + word + ")");
        return word;
    }
}
