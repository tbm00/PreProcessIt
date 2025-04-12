package dev.tbm00.preprocessit.model.actioneer;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.Token;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class TokenInsertAtActioneer implements ActioneerInterface {
    
    @Override
    public void execute(Token token, ActionSpec actionSpec, String matchedString) {
        String param = actionSpec.getParameter();
        if (param == null || param.isEmpty()) {
            StaticUtil.log("No parameter provided for TOKEN_INSERT_AT action. Expected format: TOKEN_INSERT_AT(index,substring)");
            return;
        }
        // Expecting parameter format: "index,substring"
        String[] parts = param.split(",", 2);
        if (parts.length < 2) {
            StaticUtil.log("Invalid parameter format for TOKEN_INSERT_AT action. Expected format: TOKEN_INSERT_AT(index,substring)");
            return;
        }
        try {
            int index = Integer.parseInt(parts[0].trim());
            String insertStr = parts[1];
            String tokenVal = token.getValue();
            // Ensure the index is within bounds.
            if (index < 0) {
                index = 0;
            }
            if (index > tokenVal.length()) {
                index = tokenVal.length();
            }
            String newVal = tokenVal.substring(0, index) + insertStr + tokenVal.substring(index);
            token.setValue(newVal);
            StaticUtil.log("TokenInsertAtActioneer: " + token.getValue());
        } catch (NumberFormatException e) {
            StaticUtil.log("Error parsing index for TOKEN_INSERT_AT: " + e.getMessage());
        }
    }
}
