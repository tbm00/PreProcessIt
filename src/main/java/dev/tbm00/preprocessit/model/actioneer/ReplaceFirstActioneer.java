package dev.tbm00.preprocessit.model.actioneer;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class ReplaceFirstActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString) {
        String param = actionSpec.getParameter();
        if (param == null || param.isEmpty()) {
            StaticUtil.log("      (ReplaceFirstActioneer: no parameter provided)");
            return word;
        }
        // Expecting parameter format: "from,to"
        String[] parts = param.split(",", 2);
        if (parts.length < 2) {
            StaticUtil.log("      (ReplaceFirstActioneer: invalid parameter format)");
            return word;
        }
        try {
            word.replaceFirst(parts[0], parts[1]);
            StaticUtil.log("      (ReplaceFirstActioneer: " + word + ")");
        } catch (Exception e) {
            StaticUtil.log("      (ReplaceFirstActioneer: error replacing strng)");
        } return word;
    }
}
