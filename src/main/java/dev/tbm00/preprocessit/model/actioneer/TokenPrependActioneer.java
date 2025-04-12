package dev.tbm00.preprocessit.model.actioneer;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.Token;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class TokenPrependActioneer implements ActioneerInterface {
    
    @Override
    public void execute(Token token, ActionSpec actionSpec, String matchedString) {
        String prependStr = actionSpec.getParameter();
        if (prependStr == null) {
            prependStr = "";
        }
        // Prepend the parameter to the token's value.
        String newVal = prependStr + token.getValue();
        token.setValue(newVal);
        StaticUtil.log("TokenPrependActioneer: " + token.getValue());
    }
}
