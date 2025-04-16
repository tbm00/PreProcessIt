package dev.tbm00.preprocessit.model.matcher;

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