package dev.tbm00.preprocessit.model.actioneer;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class ReplaceAllActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString) {
        String param = actionSpec.getParameter();
        if (param == null || param.isEmpty()) {
            StaticUtil.log("      (ReplaceAllActioneer: no parameter provided)");
            return word;
        }
        // Expecting parameter format: "from,to"
        String[] parts = param.split(",", 2);
        if (parts.length < 2) {
            StaticUtil.log("      (ReplaceAllActioneer: invalid parameter format)");
            return word;
        }
        try {
            word.replace(parts[0], parts[1]);
            StaticUtil.log("      (ReplaceAllActioneer: " + word + ")");
        } catch (Exception e) {
            StaticUtil.log("      (ReplaceAllActioneer: error replacing strng)");
        } return word;
    }
}
