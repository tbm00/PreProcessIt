package dev.tbm00.preprocessit.model.actioneer;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class ReplaceMatchFirstActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString) {

        String param = actionSpec.getParameter();
        if (param == null || param.isEmpty()) {
            StaticUtil.log("      (ReplaceMatchFirstActioneer: no parameter provided)");
            return word;
        }

        if (matchedString != null && !matchedString.isEmpty()) {
            word = word.replaceFirst(java.util.regex.Pattern.quote(matchedString), param);
        }

        StaticUtil.log("      (ReplaceMatchFirstActioneer: " + word + ")");
        return word;
    }
}
