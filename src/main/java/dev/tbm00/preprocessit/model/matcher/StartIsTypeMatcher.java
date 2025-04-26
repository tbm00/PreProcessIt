package dev.tbm00.preprocessit.model.matcher;

import java.util.UUID;

public class StartIsTypeMatcher implements MatcherInterface {
    private String type;
    
    public StartIsTypeMatcher(String value) {
        this.type = value.toUpperCase();
    }
    
    @Override
    public String match(String word) {
        for (int len = word.length(); len > 0; len--) {
            String subWord = word.substring(0, len);
            switch (type) {
                case "INTEGER":
                    try {
                        Integer.parseInt(subWord);
                        return subWord;
                    } catch (NumberFormatException e) {}
                    break;
                case "DOUBLE":
                    try {
                        Double.parseDouble(subWord);
                        if (subWord.contains(".") && Character.isDigit(subWord.charAt(subWord.length() - 1))) {
                            return subWord;
                        }
                    } catch (NumberFormatException e) {}
                    break;
                case "NUMBER":
                    try {
                        Double.parseDouble(subWord);
                        if (Character.isDigit(subWord.charAt(subWord.length() - 1))) {
                            return subWord;
                        }
                    } catch (NumberFormatException e) {}
                    break;
                case "UUID":
                    try {
                        UUID.fromString(subWord);
                        return subWord;
                    } catch (IllegalArgumentException e) {}
                    break;
                case "STRING":
                    if (!subWord.matches("^[+-]?\\d*(\\.\\d+)?$")) {
                        return subWord;
                    }
                    break;
                default:
                    return "";
            }
        }
        return "";
    }
}
