package dev.tbm00.preprocessit.model;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.tbm00.preprocessit.datastructures.DoublyLinkedList;
import dev.tbm00.preprocessit.datastructures.Node;
import dev.tbm00.preprocessit.datastructures.Component;
import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.datastructures.Attribute;
import dev.tbm00.preprocessit.datastructures.Qualifier;
import dev.tbm00.preprocessit.datastructures.Token;
import dev.tbm00.preprocessit.model.matcher.QualifierMatcher;

public class ProcessHandler {
    private final Model model;

    public ProcessHandler(Model model) {
        this.model = model;
    }

    public void processData() {
        Component selectedComponent = model.getSelectedComponent();
        String inputText = model.getInputText();
    
        // If the component or its attributes are null, nothing to do.
        if (selectedComponent == null || selectedComponent.getAttributes() == null) {
            return;
        }
    
        StringBuilder newOutput = new StringBuilder();

        // Split input text into lines.
        String[] inputLines = inputText.split("\\r?\\n");
    
        for (String inputLine : inputLines) {
            // Tokenize the current line on whitespace.
            // Each token is wrapped in a Token object.
            String[] tokenStrings = inputLine.split("\\s+");
    
            // Create a doubly linked list of tokens.
            DoublyLinkedList<Token> tokenList = new DoublyLinkedList<>();
            for (String tokenStr : tokenStrings) {
                tokenList.addLast(new Token(tokenStr));
            }
    
            // A map to hold standardized values for attributes for the current line.
            Map<String, String> standardizedAttributes = new HashMap<>();
            // Builder for any tokens or parts that did not match.
            StringBuilder leftoverBuilder = new StringBuilder();
    
            // Iterate over tokens using the doubly linked list.
            // (Assumes that your DoublyLinkedList provides a method getHead() to get the first node.)
            Node<Token> current = tokenList.getHead();
            while (current != null) {
                Token token = current.getData();
                StaticUtil.log(token);
                if (token.getValue().isEmpty()) {
                    // Skip tokens that are empty.
                    current = current.getNext();
                    continue;
                }
    
                boolean tokenMatched = false;
    
                // Process each attribute from the selected component.
                for (Attribute attribute : selectedComponent.getAttributes()) {
                    StaticUtil.log(attribute.getName());
                    // Make sure the attribute's list of qualifiers is not null.
                    if (attribute.getQualifiers() == null) {
                        continue;
                    }
    
                    // If we already extracted a value for this attribute on this line, skip it.
                    if (standardizedAttributes.containsKey(attribute.getName())) {
                        continue;
                    }
    
                    // Iterate through the qualifiers for this attribute.
                    for (Qualifier qualifier : attribute.getQualifiers()) {
                        StaticUtil.log(qualifier.getID());
                        // Retrieve the pre-built matcher from the qualifier.
                        QualifierMatcher matcher = qualifier.getMatcher();
    
                        // Get the previous and next token values if they exist.
                        String prev = (current.getBack() != null) ? current.getBack().getData().getValue() : "";
                        String next = (current.getNext() != null) ? current.getNext().getData().getValue() : "";
    
                        // Check if the current token (with context) matches the qualifier rule.
                        if (matcher.process(token.getValue(), prev, next, qualifier.getValue(), qualifier.getLocation(), qualifier.getQualifiedActions(), qualifier.getUnqualifiedActions())) {
                            // Standardize the value.
                            String standardizedValue = matcher.standardize(token.getValue());
                            standardizedAttributes.put(attribute.getName(), standardizedValue);
    
                            // Remove the matched part from the token.
                            token.consumeMatchedPart(standardizedValue);
                            tokenMatched = true;
                            // Assuming qualifier instructions tell us to move to the next token.
                            break;
                        }
                    }
                    if (tokenMatched) {
                        break;
                    }
                }
                // If no qualifier successfully matched this token, consider it leftover.
                if (!tokenMatched) {
                    leftoverBuilder.append(token.getValue()).append(" ");
                }
                current = current.getNext();
            }
    
            // Define the order of attributes in the output line (adjust as necessary).
            List<String> attributeOrder = selectedComponent.getAttributeOrder();
            StringBuilder formattedLine = new StringBuilder();
    
            // Append standardized attribute values in the defined order.
            for (String attrName : attributeOrder) {
                String value = standardizedAttributes.getOrDefault(attrName, "");
                formattedLine.append(value).append(",");
            }
            // Append any leftover text.
            formattedLine.append(leftoverBuilder.toString().trim());
    
            newOutput.append(formattedLine.toString()).append("\n");
        }
    
        model.setOutputText(newOutput.toString());
    }
}
