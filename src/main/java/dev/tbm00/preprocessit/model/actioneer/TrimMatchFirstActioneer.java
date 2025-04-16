package dev.tbm00.preprocessit.model.actioneer;

import java.util.List;

import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class TrimMatchFirstActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString, List<String> log) {

        if (matchedString != null && !matchedString.isEmpty()) {
            word = word.replaceFirst(java.util.regex.Pattern.quote(matchedString), "");
        }

        log.add("      (TrimMatchFirstActioneer: " + word + ")");
        return word;
    }
}
