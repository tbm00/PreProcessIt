package dev.tbm00.preprocessit.model.actioneer;

import java.util.List;

import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class TrimMatchStartActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString, List<String> log) {
        
        if (matchedString != null && !matchedString.isEmpty() && word.startsWith(matchedString)) {
            word = word.substring(matchedString.length());
        }

        log.add("      (TrimMatchStartActioneer: " + word + ")");
        return word;
    }
}
