package dev.tbm00.preprocessit.model.actioneer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import dev.tbm00.preprocessit.model.data.enums.ActionSpec;

public class RoundActioneer implements ActioneerInterface {
    
    @Override
    public String execute(String word, ActionSpec actionSpec, String matchedString, List<String> log) {
        String param = actionSpec.getParameter();
        if (param == null || param.isEmpty()) {
            log.add("      (RoundActioneer: no parameter provided)");
            return word;
        }
        // Expecting parameter format: "up/down/nearest,amount"
        String[] parts = param.split(",", 2);
        if (parts.length < 2) {
            log.add("      (RoundActioneer: invalid parameter format)");
            return word;
        }

        String mode = parts[0].trim().toLowerCase();
        BigDecimal step;
        try {
            step = new BigDecimal(parts[1].trim());
        } catch (NumberFormatException e) {
            log.add("      (RoundActioneer: invalid rounding amount: " + parts[1] + ")");
            return word;
        }

        if (BigDecimal.ZERO.compareTo(step) == 0) {
            log.add("      (RoundActioneer: rounding amount is zero)");
            return word;
        }

        BigDecimal originalValue;
        try {
            originalValue = new BigDecimal(word);
        } catch (NumberFormatException e) {
            log.add("      (RoundActioneer: " + word + " is not a number)");
            return word;
        }

        RoundingMode roundingMode;
        switch (mode) {
            case "nearest":
                roundingMode = RoundingMode.HALF_UP;
                break;
            case "up":
                roundingMode = RoundingMode.CEILING;
                break;
            case "down":
                roundingMode = RoundingMode.FLOOR;
                break;
            default:
                log.add("      (RoundActioneer: unknown rounding mode: " + mode + ")");
                return word;
        }

        // then round that quotient to an integer, then multiply back by step
        BigDecimal quotient = originalValue.divide(step, 10, RoundingMode.HALF_UP);
        BigDecimal roundedQuotient = quotient.setScale(0, roundingMode);
        BigDecimal finalValue = roundedQuotient.multiply(step);


        boolean givenAsDouble = word.contains(".");
        String result;
        if (!givenAsDouble) {
            result = finalValue.toBigIntegerExact().toString();
        } else {
            result = finalValue.stripTrailingZeros().toPlainString();
        }
    
        log.add("      (RoundActioneer: " + result + ")");
        return result;
    }
}
