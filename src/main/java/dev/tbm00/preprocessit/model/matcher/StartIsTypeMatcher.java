package dev.tbm00.preprocessit.model.matcher;

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
                        if (subWord.contains(".")) {
                            return subWord;
                        }
                    } catch (NumberFormatException e) {}
                    break;
                case "NUMBER":
                    try {
                        Double.parseDouble(subWord);
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
