package dev.tbm00.preprocessit.model;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.Component;
import dev.tbm00.preprocessit.model.data.Attribute;
import dev.tbm00.preprocessit.model.data.Qualifier;
import dev.tbm00.preprocessit.model.data.Token;
import dev.tbm00.preprocessit.model.data.Node;
import dev.tbm00.preprocessit.model.data.DoublyLinkedList;
import dev.tbm00.preprocessit.model.data.enums.Action;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;
import dev.tbm00.preprocessit.model.data.enums.Word;
import dev.tbm00.preprocessit.model.actioneer.ActioneerFactory;
import dev.tbm00.preprocessit.model.actioneer.ActioneerInterface;
import dev.tbm00.preprocessit.model.matcher.MatcherInterface;

public class ProcessHandler {
    private final Model model;
    private Map<String, String> outputAttributes = new HashMap<>();

    public ProcessHandler(Model model) {
        this.model = model;
    }

    public void processData() {
        Component selectedComponent = model.getSelectedComponent();
        String inputText = model.getInputText();
    
        // If the component or its attributes are null, nothing to do
        if (selectedComponent == null || selectedComponent.getAttributes() == null) {
            return;
        }
    
        StringBuilder newOutput = new StringBuilder();

        // Split input text into lines
        String[] inputLines = inputText.split("\\r?\\n");
    
        for (String inputLine : inputLines) {
            // Tokenize the current line on whitespace
            // Each token is wrapped in a Token object
            String[] tokenStrings = inputLine.split("\\s+");
    
            // Create a doubly linked list of tokens
            DoublyLinkedList<Token> tokenList = new DoublyLinkedList<>();
            for (String tokenStr : tokenStrings) {
                tokenList.addLast(new Token(tokenStr));
            }
    
            // A map to hold values for attributes for the current line
            outputAttributes.clear();

            // Builder for any tokens or parts that did not match.
            StringBuilder leftoverBuilder = new StringBuilder();
    
            // Iterate over tokens using the doubly linked list
            // (Assumes that your DoublyLinkedList provides a method getHead() to get the first node)
            Node<Token> current = tokenList.getHead();
            token_iteration:
            while (current != null) {
                Token token = current.getData();
                StaticUtil.log(token);

                if (token.getValue().isEmpty()) {
                    current = current.getNext();
                    StaticUtil.log("");
                    continue token_iteration;
                }

                if (token.isProcessed()) {
                    current = current.getNext();
                    StaticUtil.log("");
                    continue token_iteration;
                }
    
                boolean tokenMatched = false;
    
                // Process each attribute from the selected component.
                attribute_iteration:
                for (Attribute attribute : selectedComponent.getAttributes()) {
                    StaticUtil.log(attribute.getName());
                    // Make sure the attribute's list of qualifiers is not null.
                    if (attribute.getQualifiers() == null) {
                        continue attribute_iteration;
                    }
    
                    // If we already extracted a value for this attribute on this line, skip it.
                    if (outputAttributes.containsKey(attribute.getName())) {
                        continue;
                    }
    
                    // Iterate through the qualifiers for this attribute.
                    String intitial_word = current.getData().getValue();
                    qualifier_iteration:
                    for (Qualifier qualifier : attribute.getQualifiers()) {
                        StaticUtil.log(qualifier.getID());

                        // Retrieve the pre-built matcher from the qualifier.
                        MatcherInterface matcher = qualifier.getMatcher();

                        String wordToMatch;
                        if (qualifier.getWord().equals(Word.INITIAL_TOKEN_COPY)) {
                            wordToMatch = intitial_word;
                        } else {
                            wordToMatch = current.getData().getValue();
                        }

                        // Check if the current word String matches the qualifier rule.
                        String matchedString = matcher.match(wordToMatch);
                        if (!matchedString.isEmpty()) StaticUtil.log("MATCHED!!!: " + matchedString);
                        ActionSpec[] actionSpecs;

                        if (!matchedString.isEmpty()) {
                            actionSpecs = qualifier.getQualifiedActions();
                            tokenMatched = true;
                        } else {
                            actionSpecs = qualifier.getUnqualifiedActions();
                        }

                        String working_word = current.getData().getValue();

                        for (ActionSpec actionSpec : actionSpecs) {
                            if (actionSpec.getAction().equals(Action.TOKEN_SHIP)) {
                                outputAttributes.put(attribute.getName(), working_word);
                                current.getData().setProcessed(true);
                                current = current.getNext();
                                continue token_iteration;
                            } else if (actionSpec.getAction().equals(Action.EXIT_TO_NEXT_TOKEN_ITERATION)) {
                                current = current.getNext();
                                continue token_iteration;
                            } else if (actionSpec.getAction().equals(Action.EXIT_TO_NEXT_ATTRIBUTE_ITERATION)) {
                                continue attribute_iteration;
                            } else if (actionSpec.getAction().equals(Action.CONTINUE)) {
                                continue qualifier_iteration;
                            } else if (actionSpec.getAction().equals(Action.TRY_NEIGHBORS)) {
                                Integer distance = Integer.valueOf(actionSpec.getParameter());
                                if (distance==null || distance<0) distance = 1;
                                for (int i = 1; i<=distance; i++) {
                                    if (tryNeighbors(current, working_word, i, matcher, attribute.getName())) {
                                        continue token_iteration;
                                    }
                                }
                            } else {
                                ActioneerInterface actioneer = ActioneerFactory.getActioneer(actionSpec.getAction());
                                if (actioneer != null) {
                                    actioneer.execute(token, actionSpec, matchedString);
                                } else {
                                    StaticUtil.log("No executor found for action: " + actionSpec);
                                }
                            }
                        }
                    }
                    if (tokenMatched) {
                        break;
                    }
                }
                // If no qualifier successfully matched this token, consider it leftover
                if (!tokenMatched) {
                    leftoverBuilder.append(token.getValue()).append(" ");
                }
                current = current.getNext();
            }
    
            // Define the order of attributes in the output line (adjust as necessary)
            List<String> attributeOrder = selectedComponent.getAttributeOrder();
            StringBuilder formattedLine = new StringBuilder();
    
            // Append outputAttributes in the defined order
            for (String attrName : attributeOrder) {
                String value = outputAttributes.getOrDefault(attrName, "");
                formattedLine.append(value).append(",");
            }
            // Append any leftover text
            formattedLine.append(leftoverBuilder.toString().trim());
    
            newOutput.append(formattedLine.toString()).append("\n");
        }
    
        model.setOutputText(newOutput.toString());
    }

    /**
     * Attempts to adjust the current token by checking both left and right neighbors.
     * If either neighbor contributes enough characters to produce a match from the matcher,
     * the token is updated and the consumed characters are removed from the neighbor.
     * 
     * @param current The current node containing the token, use to get neighbors.
     * @param working_word The current node's string to work on.
     * @param charDistance The number of characters to try from the neighbor.
     * @param matcher The matcher used to verify whether the combined token now qualifies.
     * @param attributeName The name of the attribute that might get added.
     * @return true if a match is found by borrowing from a neighbor; false otherwise.
     */
    private boolean tryNeighbors(Node<Token> current, String working_word, int charDistance, MatcherInterface matcher, String attributeName) {
        if (tryLeftNeighbor(current, working_word, charDistance, matcher, attributeName)) {
            return true;
        }
        if (tryRightNeighbor(current, working_word, charDistance, matcher, attributeName)) {
            return true;
        }
        return false;
    }

    private boolean tryLeftNeighbor(Node<Token> current, String working_word, int charDistance, MatcherInterface matcher, String attributeName) {
        Node<Token> leftNode = current.getPrior();
        if (leftNode != null && !leftNode.getData().isProcessed()) {
            String leftValue = leftNode.getData().getValue();
            if (leftValue.isEmpty()) {
                return false;
            }
            // Grab up to 'charDistance' characters from the end of the left token
            int startIndex = Math.max(0, leftValue.length() - charDistance);
            String neighborPart = leftValue.substring(startIndex);
            String candidate = neighborPart + working_word;
            String matchResult = matcher.match(candidate);
            if (!matchResult.isEmpty()) {
                // Consume the used portion from the left token
                String remaining = leftValue.substring(0, startIndex);
                leftNode.getData().setValue(remaining);
                if (remaining.isEmpty()) {
                    leftNode.getData().setProcessed(true);
                }

                outputAttributes.put(attributeName, candidate);
                return true;
            }
        }
        return false;
    }

    private boolean tryRightNeighbor(Node<Token> current, String working_word, int charDistance, MatcherInterface matcher, String attributeName) {
        Node<Token> rightNode = current.getNext();
        if (rightNode != null && !rightNode.getData().isProcessed()) {
            String rightValue = rightNode.getData().getValue();
            if (rightValue.isEmpty()) {
                return false;
            }
            // Get up to 'charDistance' characters from the beginning of the right token
            int lengthToTake = Math.min(charDistance, rightValue.length());
            String neighborPart = rightValue.substring(0, lengthToTake);
            String candidate = current.getData().getValue() + neighborPart;
            String matchResult = matcher.match(candidate);
            if (!matchResult.isEmpty()) {
                // Consume the used portion from the right token
                String remaining = rightValue.substring(lengthToTake);
                rightNode.getData().setValue(remaining);
                if (remaining.isEmpty()) {
                    rightNode.getData().setProcessed(true);
                }

                outputAttributes.put(attributeName, candidate);
                return true;
            }
        }
        return false;
    }
}
