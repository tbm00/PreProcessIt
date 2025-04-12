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
    private String working_word = null;
    private int skipNextQualifier = 0;

    public ProcessHandler(Model model) {
        this.model = model;
    }

    public void processData() {
        Component selectedComponent = model.getSelectedComponent();
        if (selectedComponent == null || selectedComponent.getAttributes() == null) return;

        StringBuilder newOutput = new StringBuilder();
        String inputText = model.getInputText();
        String[] inputLines = inputText.split("\\r?\\n");
    
        for (String inputLine : inputLines) {
            // Tokenize the current line (each token wrapped in a Token object)
            String[] tokenStrings = inputLine.split("\\s+");
            DoublyLinkedList<Token> tokenList = new DoublyLinkedList<>();
            for (String tokenStr : tokenStrings) {
                tokenList.addLast(new Token(tokenStr));
            }
    
            // Clear outputAttributes map and prepare a builder for unmatched tokens
            outputAttributes.clear();
            StringBuilder leftoverBuilder = new StringBuilder();

            attribute_loop:
            for (Attribute attribute : selectedComponent.getAttributes()) {
                if (outputAttributes.containsKey(attribute.getName())) continue attribute_loop;
                
                // Go through each token in the token list to find a match for this attribute.
                Node<Token> current = tokenList.getHead();
                token_loop:
                while (current != null) {
                    Token token = current.getData();
                    
                    // Only try tokens that have not been processed and are not empty.
                    if (!token.isProcessed() && !token.getValue().isEmpty()) {
                        String initialWord = token.getValue();
                        working_word = initialWord;
                        
                        // For each qualifier in the attribute, check if it matches the token.
                        qualifier_loop:
                        for (Qualifier qualifier : attribute.getQualifiers()) {
                            if (skipNextQualifier>0) {
                                skipNextQualifier--;
                                continue qualifier_loop;
                            }

                            MatcherInterface matcher = qualifier.getMatcher();

                            if (qualifier.getWord().equals(Word.INITIAL_TOKEN_COPY)) {
                                working_word = initialWord;
                            } else if (qualifier.getWord().equals(Word.LEFT_NEIGHBOR)) {
                                if (current.getPrior()==null) continue token_loop;
                                working_word = current.getPrior().getData().getValue();
                            } else if (qualifier.getWord().equals(Word.RIGHT_NEIGHBOR)) {
                                if (current.getNext()==null) continue token_loop;
                                working_word = current.getNext().getData().getValue();
                            }
                            
                            // Check match & get actions
                            String matchedString = matcher.match(working_word);
                            ActionSpec[] actionSpecs;
                            if (!matchedString.isEmpty()) {
                                StaticUtil.log("! MATCHED !: " + matchedString);
                                actionSpecs = qualifier.getQualifiedActions();
                            } else {
                                actionSpecs = qualifier.getUnqualifiedActions();
                            }

                            // Execute actions
                            boolean tokenShipped = false;
                            for (ActionSpec actionSpec : actionSpecs) {
                                if (actionSpec.getAction().equals(Action.SHIP)) {
                                    StaticUtil.log("! SHIPPED !: " + working_word);
                                    outputAttributes.put(attribute.getName(), working_word);
                                    token.setProcessed(true);
                                    current = current.getNext();
                                    tokenShipped = true;
                                    break;
                                } else if (actionSpec.getAction().equals(Action.EXIT_TO_NEXT_TOKEN_ITERATION)) {
                                    current = current.getNext();
                                    continue token_loop;
                                } else if (actionSpec.getAction().equals(Action.EXIT_TO_NEXT_ATTRIBUTE_ITERATION)) {
                                    continue attribute_loop;
                                } else if (actionSpec.getAction().equals(Action.CONTINUE)) {
                                    continue qualifier_loop;
                                } else if (actionSpec.getAction().equals(Action.CONTINUE_AND_SKIP_NEXT_QUALIFIER)) {
                                    Integer amount = Integer.valueOf(actionSpec.getParameter());
                                    if (amount==null || amount<0) amount = 1;
                                    StaticUtil.log("Skipping next qualifiers: " + amount);
                                    skipNextQualifier = amount;
                                    continue qualifier_loop;
                                } else if (actionSpec.getAction().equals(Action.TRY_NEIGHBORS)) {
                                    Integer distance = Integer.valueOf(actionSpec.getParameter());
                                    if (distance==null || distance<0) distance = 1;
                                    StaticUtil.log("Trying neighbors: " + distance);
                                    if (tryNeighbors(current, working_word, distance, matcher, attribute.getName())) {
                                        continue token_loop;
                                    }
                                } else if (actionSpec.getAction().equals(Action.REMOVE_MATCH_FROM_LEFT_NEIGHBOR)) {
                                    if (current.getPrior()==null) continue token_loop;
                                    ActioneerInterface actioneer = ActioneerFactory.getActioneer(actionSpec.getAction());
                                    if (actioneer != null) {
                                        current.getPrior().getData().setValue(actioneer.execute(working_word, actionSpec, matchedString));
                                        StaticUtil.log("removed match from left neigbor, left neightbor now: " + working_word);
                                        continue qualifier_loop;
                                    } else {
                                        StaticUtil.log("No executor found for action: " + actionSpec);
                                        continue attribute_loop;
                                    }
                                } else if (actionSpec.getAction().equals(Action.REMOVE_MATCH_FROM_RIGHT_NEIGHBOR)) {
                                    if (current.getNext()==null) continue token_loop;
                                    ActioneerInterface actioneer = ActioneerFactory.getActioneer(actionSpec.getAction());
                                    if (actioneer != null) {
                                        current.getNext().getData().setValue(working_word);
                                        StaticUtil.log("removed match from right neigbor, right neightbor now: " + working_word);
                                        continue qualifier_loop;
                                    } else {
                                        StaticUtil.log("No executor found for action: " + actionSpec);
                                        continue attribute_loop;
                                    }
                                } else {
                                    ActioneerInterface actioneer = ActioneerFactory.getActioneer(actionSpec.getAction());
                                    if (actioneer != null) {
                                        working_word = actioneer.execute(working_word, actionSpec, matchedString);
                                        StaticUtil.log("working_word_2: " + working_word);
                                    } else {
                                        StaticUtil.log("No executor found for action: " + actionSpec);
                                        continue attribute_loop;
                                    }
                                }
                            }
                            
                            // Exit qualifier loop since we shipped the token
                            if (tokenShipped) {
                                break; // Exit qualifier loop; attribute has been assigned.
                            }
                        }
                    }
                    // Exit token loop since the current attribute was shipped
                    if (outputAttributes.containsKey(attribute.getName())) {
                        break;
                    }
                    current = current.getNext();
                }
            }

            // Add leftovers
            Node<Token> current = tokenList.getHead();
            while (current != null) {
                Token token = current.getData();
                if (!token.isProcessed()) {
                    leftoverBuilder.append(token.getValue()).append(" ");
                }
                current = current.getNext();
            }

            // Format the output attribute line
            List<String> attributeOrder = selectedComponent.getAttributeOrder();
            StringBuilder formattedLine = new StringBuilder();
            for (String attrName : attributeOrder) {
                String value = outputAttributes.getOrDefault(attrName, "");
                formattedLine.append(value).append(",");
            }
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
     * @param maxDistance The max number of characters to try from the neighbor.
     * @param matcher The matcher used to verify whether the combined token now qualifies.
     * @param attributeName The name of the attribute that might get added.
     * @return true if a match is found by borrowing from a neighbor; false otherwise.
     */
    private boolean tryNeighbors(Node<Token> current, String working_word, int maxDistance, MatcherInterface matcher, String attributeName) {
        for (int distance = 1; distance <= maxDistance; distance++) {
            if (tryLeftNeighbor(current, working_word, distance, matcher, attributeName)) {
                return true;
            }
            if (tryRightNeighbor(current, working_word, distance, matcher, attributeName)) {
                return true;
            }
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
            // Borrow up to charDistance characters from the end of the left token.
            int effectiveDistance = Math.min(charDistance, leftValue.length());
            String neighborPart = leftValue.substring(leftValue.length() - effectiveDistance);
            String candidate = neighborPart + working_word;
            String matchResult = matcher.match(candidate);
            if (!matchResult.isEmpty()) {
                // Remove the used portion from the left token.
                String remaining = leftValue.substring(0, leftValue.length() - effectiveDistance);
                leftNode.getData().setValue(remaining);
                if (remaining.isEmpty()) {
                    leftNode.getData().setProcessed(true);
                }
                // Update the current token with the merged candidate and mark as processed.
                current.getData().setValue(candidate);
                current.getData().setProcessed(true);
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
            // Borrow up to charDistance characters from the beginning of the right token.
            int effectiveDistance = Math.min(charDistance, rightValue.length());
            String neighborPart = rightValue.substring(0, effectiveDistance);
            String candidate = working_word + neighborPart;
            String matchResult = matcher.match(candidate);
            if (!matchResult.isEmpty()) {
                // Remove the used portion from the right token.
                String remaining = rightValue.substring(effectiveDistance);
                rightNode.getData().setValue(remaining);
                if (remaining.isEmpty()) {
                    rightNode.getData().setProcessed(true);
                }
                // Update the current token with the merged candidate and mark as processed.
                current.getData().setValue(candidate);
                current.getData().setProcessed(true);
                outputAttributes.put(attributeName, candidate);
                return true;
            }
        }
        return false;
    }
}
