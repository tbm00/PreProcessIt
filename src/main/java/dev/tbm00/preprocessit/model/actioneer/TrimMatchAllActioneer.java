package dev.tbm00.preprocessit.model.actioneer;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class TrimMatchAllActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString) {

        if (matchedString != null && !matchedString.isEmpty()) {
            word = word.replaceAll(java.util.regex.Pattern.quote(matchedString), "");
        }

        StaticUtil.log("      (TrimMatchAllActioneer: " + word + ")");
        return word;
    }
}
