package dev.tbm00.preprocessit.model.actioneer;

import java.util.List;

import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class ReplaceMatchAllActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString, List<String> log) {

        String param = actionSpec.getParameter();
        if (param == null || param.isEmpty()) {
            log.add("      (ReplaceMatchAllActioneer: no parameter provided)");
            return word;
        }

        if (matchedString != null && !matchedString.isEmpty()) {
            word = word.replaceAll(java.util.regex.Pattern.quote(matchedString), param);
        }

        log.add("      (ReplaceMatchAllActioneer: " + word + ")");
        return word;
    }
}
