package dev.tbm00.preprocessit.model.actioneer;

import java.util.List;

import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class ReplaceAllActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString, List<String> log) {
        String param = actionSpec.getParameter();
        if (param == null || param.isEmpty()) {
            log.add("      (ReplaceAllActioneer: no parameter provided)");
            return word;
        }
        // Expecting parameter format: "from,to"
        String[] parts = param.split(",", 2);
        if (parts.length < 2) {
            log.add("      (ReplaceAllActioneer: invalid parameter format)");
            return word;
        }
        try {
            word.replace(parts[0], parts[1]);
            log.add("      (ReplaceAllActioneer: " + word + ")");
        } catch (Exception e) {
            log.add("      (ReplaceAllActioneer: error replacing strng)");
        } return word;
    }
}
