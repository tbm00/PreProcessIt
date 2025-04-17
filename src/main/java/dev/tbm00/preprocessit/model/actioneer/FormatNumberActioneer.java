package dev.tbm00.preprocessit.model.actioneer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class FormatNumberActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString, List<String> log) {
        String param = actionSpec.getParameter();
        if (param == null || param.isEmpty()) {
            log.add("      (FormatNumberActioneer: no parameter provided)");
            return word;
        }

        // Expecting parameter format: "####,useGrouping"
        String[] parts = param.split(",", 2);
        if (parts.length < 2) {
            log.add("      (FormatNumberActioneer: invalid parameter format)");
            return word;
        }

        BigDecimal originalValue;
        try {
            originalValue = new BigDecimal(word);
        } catch (NumberFormatException e) {
            log.add("      (FormatNumberActioneer: " + word + " is not a number)");
            return word;
        }

        // determine decimal count
        String patternSpec = parts[0].trim();
        int decimalPlaces = 0;
        int dotIndex = patternSpec.indexOf('.');
        if (dotIndex >= 0) {
            decimalPlaces = patternSpec.length() - dotIndex - 1;
        }

        boolean useGrouping = Boolean.parseBoolean(parts[1].trim());
        StringBuilder dfPattern = new StringBuilder();
        if (useGrouping) {
            // grouping pattern
            dfPattern.append("#,##0");
        } else {
            dfPattern.append("0");
        }
        if (decimalPlaces > 0) {
            dfPattern.append('.');
            for (int i = 0; i < decimalPlaces; i++) {
                dfPattern.append('0');
            }
        }

        DecimalFormat df = new DecimalFormat(dfPattern.toString());
        df.setRoundingMode(RoundingMode.DOWN); // truncate

        String outputWord = df.format(originalValue);
        log.add("      (FormatNumberActioneer: " + outputWord + ")");
        return outputWord;
    }
}
