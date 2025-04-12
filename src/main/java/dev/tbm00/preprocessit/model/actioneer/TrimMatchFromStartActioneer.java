package dev.tbm00.preprocessit.model.actioneer;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class TrimMatchFromStartActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString) {
        
        if (matchedString != null && !matchedString.isEmpty() && word.startsWith(matchedString)) {
            word = word.substring(matchedString.length());
        }

        StaticUtil.log("TrimMatchFromStartActioneer: " + word);
        return word;
    }
}
