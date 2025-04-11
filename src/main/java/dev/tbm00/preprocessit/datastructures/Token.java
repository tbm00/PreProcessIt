package dev.tbm00.preprocessit.datastructures;

public class Token {
    private String value;
    private boolean processed;

    /**
     * Constructs a new Token with the provided value.
     * @param value the initial text of the token.
     */
    public Token(String value) {
        this.value = value;
        this.processed = false;
    }

    /**
     * Returns the current token value.
     * @return the token value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Updates the token with a new value.
     * @param value the new value for the token.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Checks if this token has been processed.
     * @return true if the token has been processed; false otherwise.
     */
    public boolean isProcessed() {
        return processed;
    }

    /**
     * Marks the token as processed or unprocessed.
     * @param processed boolean indicating the new processed state.
     */
    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    /**
     * Consumes (removes) the specified matched part from the token's value.
     * This is useful when an attribute has been extracted from the token,
     * and you need to update the token to reflect that removal.
     *
     * @param matchedPart the substring to be removed from the token
     */
    public void consumeMatchedPart(String matchedPart) {
        if (matchedPart != null && !matchedPart.isEmpty() && value.contains(matchedPart)) {
            // Use regex quoting to match the literal substring.
            value = value.replaceFirst(java.util.regex.Pattern.quote(matchedPart), "");
        }
    }
    
    @Override
    public String toString() {
        return value;
    }
}
