package dev.tbm00.preprocessit.model.actioneer;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.Token;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class TokenTrimMatchFromEndActioneer implements ActioneerInterface {
    
    @Override
    public void execute(Token token, ActionSpec actionSpec, String matchedString) {

        String currentValue = token.getValue();
        if (matchedString != null && !matchedString.isEmpty() && currentValue.endsWith(matchedString)) {
            token.setValue(currentValue.substring(0, currentValue.length() - matchedString.length()));
        } else {
            token.consumeMatchedPart(matchedString);
        }
        
        StaticUtil.log("TokenTrimMatchFromEndActioneer: " + token.getValue());
    }
}
