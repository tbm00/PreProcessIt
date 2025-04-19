package dev.tbm00.preprocessit.model.actioneer;

import java.util.List;

import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class ReplaceFirstActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString, List<String> log) {
        String param = actionSpec.getParameter();
        if (param == null || param.isEmpty()) {
            log.add("      (ReplaceFirstActioneer: no parameter provided)");
            return word;
        }
        // Expecting parameter format: "from,to"
        String[] parts = param.split(",", 2);
        if (parts.length < 2) {
            log.add("      (ReplaceFirstActioneer: invalid parameter format)");
            return word;
        }
        try {
            word = word.replaceFirst(java.util.regex.Pattern.quote(parts[0]), parts[1]);
            log.add("      (ReplaceFirstActioneer: " + word + ")");
            log.add("      (" + parts[0] + " -> "+parts[1]+")");
        } catch (Exception e) {
            log.add("      (ReplaceFirstActioneer: error replacing strng)");
        } return word;
    }
}
