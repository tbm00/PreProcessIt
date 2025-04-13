package dev.tbm00.preprocessit.model.actioneer;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class InsertAtActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString) {
        String param = actionSpec.getParameter();
        if (param == null || param.isEmpty()) {
            StaticUtil.log("      (InsertAtActioneer: no parameter provided)");
            return word;
        }
        // Expecting parameter format: "index,substring"
        String[] parts = param.split(",", 2);
        if (parts.length < 2) {
            StaticUtil.log("      (InsertAtActioneer: invalid parameter format)");
            return word;
        }
        try {
            int index = Integer.parseInt(parts[0].trim());
            String insertStr = parts[1];
            String tokenVal = word;
            // Ensure the index is within bounds.
            if (index < 0) {
                index = 0;
            }
            if (index > tokenVal.length()) {
                index = tokenVal.length();
            }
            String newVal = tokenVal.substring(0, index) + insertStr + tokenVal.substring(index);
            word = newVal;
            StaticUtil.log("      (InsertAtActioneer: " + word + ")");
        } catch (NumberFormatException e) {
            StaticUtil.log("      (InsertAtActioneer: error parsing index)");
        } return word;
    }
}
