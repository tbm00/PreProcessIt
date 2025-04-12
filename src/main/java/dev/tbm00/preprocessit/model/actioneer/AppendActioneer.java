package dev.tbm00.preprocessit.model.actioneer;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.Token;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class TokenAppendActioneer implements ActioneerInterface {
    
    @Override
    public void execute(Token token, ActionSpec actionSpec, String matchedString) {
        String appendStr = actionSpec.getParameter();
        if (appendStr == null) appendStr = "";

        String newVal = token.getValue() + appendStr;
        token.setValue(newVal);
        StaticUtil.log("TokenAppendActioneer: " + token.getValue());
    }
}
