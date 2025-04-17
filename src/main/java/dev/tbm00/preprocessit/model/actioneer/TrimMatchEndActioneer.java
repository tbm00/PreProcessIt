package dev.tbm00.preprocessit.model.actioneer;

import java.util.List;

import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class TrimMatchEndActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString, List<String> log) {

        if (matchedString != null && !matchedString.isEmpty() && word.endsWith(matchedString)) {
            word = word.substring(0, word.length() - matchedString.length());
        }
        
        log.add("      (TrimMatchEndActioneer: " + word + ")");
        return word;
    }
}
