package dev.tbm00.preprocessit.model.actioneer;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class TrimMatchEndActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString) {

        if (matchedString != null && !matchedString.isEmpty() && word.endsWith(matchedString)) {
            word = word.substring(0, word.length() - matchedString.length());
        }
        
        StaticUtil.log("TrimMatchEndActioneer: " + word);
        return word;
    }
}
