package dev.tbm00.preprocessit.model.actioneer;

import java.util.List;

import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class AppendActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString, List<String> log) {

        String param = actionSpec.getParameter();
        if (param == null || param.isEmpty()) {
            log.add("      (AppendActioneer: no parameter provided)");
            return word;
        }

        word = word + param;
        log.add("      (AppendActioneer: " + word + ")");
        return word;
    }
}
