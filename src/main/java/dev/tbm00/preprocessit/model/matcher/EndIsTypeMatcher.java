package dev.tbm00.preprocessit.model.matcher;

import java.util.UUID;

public class EndIsTypeMatcher implements MatcherInterface {
    private String type;
    
    public EndIsTypeMatcher(String value) {
        this.type = value.toUpperCase();
    }
    
    @Override
    public String match(String word) {
        for (int i = 0; i < word.length(); i++) {
            String subWord = word.substring(i);
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
                    } catch (NumberFormatException e) {}
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