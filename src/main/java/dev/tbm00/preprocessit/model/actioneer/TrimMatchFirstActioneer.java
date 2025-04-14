package dev.tbm00.preprocessit.model.actioneer;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class TrimMatchFirstActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString) {

        if (matchedString != null && !matchedString.isEmpty()) {
            word = word.replaceFirst(java.util.regex.Pattern.quote(matchedString), "");
        }

        StaticUtil.log("      (TrimMatchFirstActioneer: " + word + ")");
        return word;
    }
}
