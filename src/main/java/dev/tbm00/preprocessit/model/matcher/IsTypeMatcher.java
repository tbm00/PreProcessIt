package dev.tbm00.preprocessit.model.matcher;

import java.util.UUID;

public class IsTypeMatcher implements MatcherInterface {
    private String type;
    
    public IsTypeMatcher(String value) {
        this.type = value.toUpperCase();
    }
    
    @Override
    public String match(String word) {
        switch (type) {
            case "INTEGER":
                try {
                    Integer.parseInt(word);
                    return word; 
                } catch (NumberFormatException e) {
                    return "";
                }
            case "DOUBLE":
                try {
                    Double.parseDouble(word);
                    if (word.contains(".") && Character.isDigit(word.charAt(word.length() - 1))) {
                        return word;
                    }
                    return "";
                } catch (NumberFormatException e) {
                    return "";
                }
            case "NUMBER":
                try {
                    Double.parseDouble(word);
                    if (Character.isDigit(word.charAt(word.length() - 1))) {
                        return word;
                    }
                    return "";
                } catch (NumberFormatException e) {
                    return "";
                }
                case "UUID":
                    try {
                        UUID.fromString(word);
                        return word;
                    } catch (NumberFormatException e) {
                        return "";
                    }
            case "STRING":
                if (word.matches("^[+-]?\\d*(\\.\\d+)?$")) {
                    return "";
                } else {
                    return word;
                }
            default:
                return "";
        }
    }
}