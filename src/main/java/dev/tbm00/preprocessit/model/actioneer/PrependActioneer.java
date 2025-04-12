package dev.tbm00.preprocessit.model.actioneer;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class PrependActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString) {
        String prependStr = actionSpec.getParameter();
        if (prependStr == null) {
            prependStr = "";
        }
        // Prepend the parameter to the token's value.
        word = prependStr + word;
        StaticUtil.log("PrependActioneer: " + word);
        return word;
    }
}
