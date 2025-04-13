package dev.tbm00.preprocessit.model.actioneer;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class AppendActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString) {
        String appendStr = actionSpec.getParameter();
        if (appendStr == null) appendStr = "";

        word = word + appendStr;
        StaticUtil.log("      (AppendActioneer: " + word + ")");
        return word;
    }
}
