package dev.tbm00.preprocessit.model.actioneer;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.Token;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class TokenKeepMatchActioneer implements ActioneerInterface {
    
    @Override
    public void execute(Token token, ActionSpec actionSpec, String matchedString) {
        token.setValue(matchedString);
        StaticUtil.log("TokenKeepMatchActioneer: " + token.getValue());
    }
}
