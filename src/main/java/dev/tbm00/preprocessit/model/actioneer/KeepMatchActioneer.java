package dev.tbm00.preprocessit.model.actioneer;

import java.util.List;

import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class KeepMatchActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString, List<String> log) {
        word = matchedString;
        log.add("      (KeepMatchActioneer: " + word + ")");
        return word;
    }
}
