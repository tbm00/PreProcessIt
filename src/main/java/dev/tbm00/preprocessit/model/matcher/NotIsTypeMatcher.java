package dev.tbm00.preprocessit.model.matcher;

public class NotIsTypeMatcher implements MatcherInterface {
    private String type;
    
    public NotIsTypeMatcher(String value) {
        this.type = value.toUpperCase();
    }
    
    @Override
    public String match(String word) {
        switch (type) {
            case "INTEGER":
                try {
                    Integer.parseInt(word);
                    return "";
                } catch (NumberFormatException e) {
                    return word;
                }
            case "DOUBLE":
                try {
                    Double.parseDouble(word);
                    if (word.contains(".") && Character.isDigit(word.charAt(word.length() - 1))) {
                        return "";
                    }
                    return word;
                } catch (NumberFormatException e) {
                    return word;
                }
            case "NUMBER":
                try {
                    Double.parseDouble(word);
                    if (Character.isDigit(word.charAt(word.length() - 1))) {
                        return "";
                    }
                    return word;
                } catch (NumberFormatException e) {
                    return word;
                }
            case "STRING":
                if (word.matches("^[+-]?\\d*(\\.\\d+)?$")) {
                    return word;
                } else {
                    return "";
                }
            default:
                return word;
        }
    }
}
