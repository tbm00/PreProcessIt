package dev.tbm00.preprocessit.model.actioneer;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class ReplaceMatchAllActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString) {

        String param = actionSpec.getParameter();
        if (param == null || param.isEmpty()) {
            StaticUtil.log("      (ReplaceMatchAllActioneer: no parameter provided)");
            return word;
        }

        if (matchedString != null && !matchedString.isEmpty()) {
            word = word.replaceAll(java.util.regex.Pattern.quote(matchedString), param);
        }

        StaticUtil.log("      (ReplaceMatchAllActioneer: " + word + ")");
        return word;
    }
}
