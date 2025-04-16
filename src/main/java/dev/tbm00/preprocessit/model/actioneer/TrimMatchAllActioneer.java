package dev.tbm00.preprocessit.model.actioneer;

import java.util.List;

import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class TrimMatchAllActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString, List<String> log) {

        if (matchedString != null && !matchedString.isEmpty()) {
            word = word.replaceAll(java.util.regex.Pattern.quote(matchedString), "");
        }

        log.add("      (TrimMatchAllActioneer: " + word + ")");
        return word;
    }
}
