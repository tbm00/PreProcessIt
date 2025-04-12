package dev.tbm00.preprocessit.model.matcher;

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
                    if (word.contains(".")) {
                        return word;
                    }
                    return "";
                } catch (NumberFormatException e) {
                    return "";
                }
            case "NUMBER":
                try {
                    Double.parseDouble(word);
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
