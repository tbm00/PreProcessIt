package dev.tbm00.preprocessit.model.actioneer;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class AppendActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString) {

        String param = actionSpec.getParameter();
        if (param == null || param.isEmpty()) {
            StaticUtil.log("      (AppendActioneer: no parameter provided)");
            return word;
        }

        word = word + param;
        StaticUtil.log("      (AppendActioneer: " + word + ")");
        return word;
    }
}
