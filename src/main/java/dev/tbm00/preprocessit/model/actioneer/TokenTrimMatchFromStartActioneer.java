package dev.tbm00.preprocessit.model.actioneer;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.Token;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class TokenTrimMatchFromStartActioneer implements ActioneerInterface {
    
    @Override
    public void execute(Token token, ActionSpec actionSpec, String matchedString) {
        
        StaticUtil.log("TokenTrimMatchFromStartActioneer: " + token.getValue());
    }
}
