package dev.tbm00.preprocessit.model.actioneer;

import java.util.List;

import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class SetCasingActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString, List<String> log) {

        String param = actionSpec.getParameter();
        if (param == null || param.isEmpty()) {
            log.add("      (SetCasingActioneer: no parameter provided)");
            return word;
        }

        String mode = actionSpec.getParameter() == null ? "" : actionSpec.getParameter().trim().toUpperCase();
        String output;
        switch (mode) {
            case "UPPER":
            case "UPPERCASE":
                output = word.toUpperCase();
                break;
            case "LOWER":
            case "LOWERCASE":
                output = word.toLowerCase();
                break;
            default:
                log.add("      (SetCasingActioneer: invalid parameter '" + mode + "')");
                return word;
        }

        log.add("      (SetCasingActioneer: " + output + ")");
        return output;
    }
}
