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
import dev.tbm00.preprocessit.model.data.enums.ActionResult;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;
import dev.tbm00.preprocessit.model.data.enums.Word;
import dev.tbm00.preprocessit.model.actioneer.ActioneerFactory;
import dev.tbm00.preprocessit.model.actioneer.ActioneerInterface;
import dev.tbm00.preprocessit.model.matcher.MatcherInterface;

/**
 * Handles the processing of input data line-by-line, by evaluating tokens against component attributes,
 * applying qualifiers and actioning as configured.
 */
public class ProcessHandler {
    private final Model model;
    private Map<String, String> outputAttributes = new HashMap<>();

    private int skip_qualifier = 0;
    private MatcherInterface current_matcher = null;
    private Node<Token> current_node = null;
    private String working_word = null;

    /**
     * Constructs a new ProcessHandler instance.
     *
     * <p>This constructor initializes the ProcessHandler with the provided model which contains the input data,
     * selected component, and other processing-related information.</p>
     *
     * @param model The model that holds the input text, selected component, and holds the processing results.
     */
    public ProcessHandler(Model model) {
        this.model = model;
    }

    /**
     * Processes the input data by tokenizing each input line, processing component attributes,
     * and building the final output.
     *
     * <p>This method retrieves the selected component from the model and processes each line of the input text.
     * It tokenizes the line, processes the attributes of the component, and rebuilds the line based on processed tokens
     * and attribute order, then returns the the output text as a String (to the model).</p>
     *
     * @return A {@code String} representing the processed output text, or the untouched input text if no valid component is selected.
     */
    public String processData() {
        Component component = model.getSelectedComponent();
        if (component == null || component.getAttributes() == null) return "";

        StringBuilder newOutput = new StringBuilder();
        String[] lines = model.getInputText().split("\\r?\\n");

        int i = 1;
        for (String line : lines) {
            StaticUtil.log(" ");
            StaticUtil.log(" ");
            StaticUtil.log(" ");
            StaticUtil.log("-=-=-=-=-=-=-=- Line "+i+" -=-=-=-=-=-=-=-");
            DoublyLinkedList<Token> tokenList = tokenizeLine(line);
            outputAttributes.clear();
            processComponentAttributes(tokenList, component);
            newOutput.append(buildOutputLine(tokenList, component.getAttributeOrder())).append("\n");
            StaticUtil.log("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            i++;
        }
        
        return newOutput.toString();
    }

    /**
     * Processes each attribute for the current component on the given token list.
     *
     * <p>This method iterates over all attributes of the specified component. For each attribute, it avoids
     * processing if an output for that attribute is already present. Otherwise, it calls {@link #processAttribute(DoublyLinkedList, Attribute)}
     * to process individual tokens.</p>
     *
     * @param tokenList The list of tokens generated from a line of input text.
     * @param component The component whose attributes are to be processed.
     */
    private void processComponentAttributes(DoublyLinkedList<Token> tokenList, Component component) {
        for (Attribute attribute : component.getAttributes()) {
            if (outputAttributes.containsKey(attribute.getName())) {
                continue;
            }
            processAttribute(tokenList, attribute);
            StaticUtil.log("[-] attribute processed");
        }
    }

    /**
     * Processes a single attribute by iterating through the token list and applying its qualifiers.
     *
     * <p>This method starts processing at the head of the token list. For each unprocessed token, it applies the attribute's
     * qualifiers via {@link #processQualifiers(String, Attribute)}. Depending on the result, it either continues to the next token,
     * or returns early if processing of the attribute is complete.</p>
     *
     * @param tokenList The list of tokens for the current line.
     * @param attribute The attribute whose qualifiers need to be processed.
     */
    private void processAttribute(DoublyLinkedList<Token> tokenList, Attribute attribute) {
        current_node = tokenList.getHead();

        StaticUtil.log(" ");
        StaticUtil.log("  --==[ Processing Attribute "+attribute.getName()+" ]==--");
        
        tokenLoop:
        while (current_node != null) {
            Token token = current_node.getData();
            if (!token.isProcessed() && !token.getValue().isEmpty()) {
                String initialWord = token.getValue();
                working_word = initialWord;

                // Process each qualifier for the attribute.
                ActionResult result = processQualifiers(initialWord, attribute);
                if (result.equals(ActionResult.NEXT_TOKEN)) {
                    current_node = current_node.getNext();
                    StaticUtil.log("[-] attriubte continuing tokenLoop");
                    continue tokenLoop;
                } else {
                    current_node = current_node.getNext();
                    StaticUtil.log("[-] attribute bumped the current node to the next neighbor!");
                    return;
                }
            } else {
                // If token is processed or empty, skip it.
                current_node = current_node.getNext();
                //StaticUtil.log(" ");
                if (token.isProcessed()) 
                    StaticUtil.log("[-] attribute bumped the current node to the next neighbor because the current node was already processed!");
                else 
                    StaticUtil.log("[-] attribute bumped the current node to the next neighbor because the current node was empty/null!");
            }
        }
    }

    /**
     * Evaluates all qualifiers for a given attribute using the provided initial word.
     *
     * <p>This method iterates over each qualifier in the attribute. It first determines the working word based on the qualifier,
     * then obtains a matched string from the qualifier's matcher. Depending on whether a match is found, the method chooses a set
     * of actions to execute. Execution results are used to decide whether to continue with further qualifiers or tokens.</p>
     *
     * @param initialWord The original token value to be evaluated.
     * @param attribute   The attribute whose qualifiers are to be processed.
     * @return An {@code ActionResult} indicating the next processing step.
     */
    private ActionResult processQualifiers(String initialWord, Attribute attribute) {

        // For each qualifier in the attribute
        qualifierLoop:
        for (Qualifier qualifier : attribute.getQualifiers()) {
            if (skip_qualifier > 0) {
                skip_qualifier--;
                continue qualifierLoop;
            }

            // Determine which word to use based on the qualifier type.
            working_word = determineWorkingWord(initialWord, qualifier.getWord());
            current_matcher = qualifier.getMatcher();
            String matchedString = current_matcher.match(working_word);

            StaticUtil.log(" ");
            StaticUtil.log(attribute.getName()+"'s "+qualifier.getWord().name()+" "+qualifier.getCondition().name()+" '"+qualifier.getValues() +"'  ::  '"+ working_word + "' -> '" + matchedString + "'");

            // Decide which set of actions to use
            ActionSpec[] actionSpecs = (matchedString.isEmpty()) ? qualifier.getUnqualifiedActions()
                                                                 : qualifier.getQualifiedActions();

            if (!matchedString.isEmpty()) {
                StaticUtil.log("[-] therefore qualified actions will run");
            } else StaticUtil.log("[-] therefore unqualified actions will run");

            // Execute the actions; if one action “ships” (matches) the attribute then exit.
            ActionResult result = executeActions(matchedString, actionSpecs, attribute.getName());
            if (result.equals(ActionResult.NEXT_QUALIFIER)) {
                StaticUtil.log("[-] qualifier continuing qualifierLoop");
                continue qualifierLoop;
            } else {
                //StaticUtil.log("[-] qualifier returning result: " + result.name());
                return result;
            }
            
        }
        return ActionResult.NEXT_TOKEN;
    }


    /**
     * Executes all actions for a given qualifier.
     *
     * <p>This method iterates over an array of action specifications and calls {@link #executeAction(String, ActionSpec, String)}
     * for each action. Depending on the result from each action, it decides whether to continue with the next action or
     * returns the result immediately.</p>
     *
     * @param matchedString The string matched by the qualifier's matcher.
     * @param actionSpecs   An array of action specifications to execute.
     * @param attributeName The name of the attribute currently being processed.
     * @return An {@code ActionResult} indicating the next step after executing the actions.
     */
    private ActionResult executeActions(String matchedString, ActionSpec[] actionSpecs, String attributeName) {
        executeLoop:
        for (ActionSpec actionSpec : actionSpecs) {
            ActionResult result = executeAction(matchedString, actionSpec, attributeName);
            if (result.equals(ActionResult.NEXT_ACTION)) {
                StaticUtil.log("[-] action continuing executeLoop");
                continue executeLoop;
            } else {
                //StaticUtil.log("[-] action returning result: " + result.name());
                return result;
            }
        }

        return ActionResult.NEXT_QUALIFIER;
    }

    /**
     * Executes a single action for the current qualifier.
     *
     * <p>This method performs the specific action as defined by the given action specification.
     * It handles various action types (such as SHIP, EXIT, CONTINUE, or neighbor-based actions) using a switch statement.
     * Based on the action executed, the working token is updated, tokens may be marked as processed, and the appropriate
     * {@code ActionResult} is returned.</p>
     *
     * @param matchedString The string that was matched by the qualifier's matcher.
     * @param actionSpec    The specification of the action to be executed.
     * @param attributeName The name of the attribute associated with the action.
     * @return An {@code ActionResult} indicating the outcome of the action execution.
     */
    private ActionResult executeAction(String matchedString, ActionSpec actionSpec, String attributeName) {
        Action action = actionSpec.getAction();
        StaticUtil.log("[-] executing action " + action.name() + "...");
        // Using a switch (or if/else) here helps centralize the different action behaviors.
        switch (action) {
            case SHIP:
                StaticUtil.log("      (shipping " + working_word + ")");
                outputAttributes.put(attributeName, working_word);
                current_node.getData().setProcessed(true);
                return ActionResult.NEXT_ATTRIBUTE;
            case EXIT_TO_NEXT_TOKEN_ITERATION:
                // The calling loop will get the next token.
                return ActionResult.NEXT_TOKEN;
            case EXIT_TO_NEXT_ATTRIBUTE_ITERATION:
                // Exit evaluation for this attribute.
                return ActionResult.NEXT_ATTRIBUTE;
            case CONTINUE:
                // Just continue processing qualifiers.
                return ActionResult.NEXT_QUALIFIER;
            case CONTINUE_AND_SKIP_NEXT_QUALIFIER:
                int skipAmount = parsePositiveIntOrDefault(actionSpec.getParameter(), 1);
                StaticUtil.log("      (skipping " + skipAmount + " qualifiers)");
                skip_qualifier = skipAmount;
                return ActionResult.NEXT_QUALIFIER;
            case TRY_NEIGHBORS:
                int distance = parsePositiveIntOrDefault(actionSpec.getParameter(), 1);
                StaticUtil.log("      (trying " + distance + "*2 neighbor characters)");
                if (tryNeighbors(distance, attributeName)) {
                    return ActionResult.NEXT_TOKEN;
                } else return ActionResult.NEXT_QUALIFIER;
            case REMOVE_MATCH_FROM_LEFT_NEIGHBOR:
                if (current_node.getPrior() != null) {
                    ActioneerInterface actioneer = ActioneerFactory.getActioneer(action);
                    if (actioneer != null) {
                        String newValue = actioneer.execute(working_word, actionSpec, matchedString);
                        current_node.getPrior().getData().setValue(newValue);
                        StaticUtil.log("      (removed match from left neighbor, updated neigbhor: " + newValue + ")");
                    } else {
                        StaticUtil.log("      (no executor found for Action." + actionSpec.getAction().name() + ")");
                    }
                }
                return ActionResult.NEXT_ACTION;
            case REMOVE_MATCH_FROM_RIGHT_NEIGHBOR:
                if (current_node.getNext() != null) {
                    ActioneerInterface actioneer = ActioneerFactory.getActioneer(action);
                    if (actioneer != null) {
                        String newValue = actioneer.execute(working_word, actionSpec, matchedString);
                        current_node.getNext().getData().setValue(newValue);
                        StaticUtil.log("      (removed match from right neighbor, updated neigbhor: " + newValue + ")");
                    } else {
                        StaticUtil.log("      (no executor found for Action." + actionSpec.getAction().name() + ")");
                    }
                }
                return ActionResult.NEXT_ACTION;
            default:
                // For any other action, attempt to execute it.
                ActioneerInterface actioneer = ActioneerFactory.getActioneer(action);
                if (actioneer != null) {
                    working_word = actioneer.execute(working_word, actionSpec, matchedString);
                    StaticUtil.log("      (updated working word to: " + working_word + ")");
                } else {
                    StaticUtil.log("      (no executor found for Action." + actionSpec.getAction().name() + ")");
                }
                return ActionResult.NEXT_ACTION;
        }
    }

    /**
     * Tokenizes an input line into a doubly linked list of tokens.
     *
     * <p>This method splits the given line by whitespace and creates a {@code Token} for each token string.
     * The tokens are added sequentially to a {@code DoublyLinkedList} which is returned for further processing.</p>
     *
     * @param line The input line to tokenize.
     * @return A {@code DoublyLinkedList} of {@code Token} objects extracted from the input line.
     */
    private DoublyLinkedList<Token> tokenizeLine(String line) {
        DoublyLinkedList<Token> tokenList = new DoublyLinkedList<>();
        String[] tokenStrings = line.split("\\s+");
        for (String tokenStr : tokenStrings) {
            tokenList.addLast(new Token(tokenStr));
        }
        return tokenList;
    }

    /**
     * Determines the working word based on the provided qualifier word type.
     *
     * <p>This method selects the appropriate word to operate on depending on the qualifier type.
     * It may return the current working token, a copy of the initial token, or a value from a neighbor token.</p>
     *
     * @param initialWord   The original token value.
     * @param qualifierWord The qualifier word type indicating which token value to use.
     * @return The token value to be used as the working word.
     */
    private String determineWorkingWord(String initialWord, Word qualifierWord) {
        if (qualifierWord.equals(Word.WORKING_TOKEN)) {
            return working_word;
        } else if (qualifierWord.equals(Word.INITIAL_TOKEN_COPY)) {
            return initialWord;
        } else if (qualifierWord.equals(Word.LEFT_NEIGHBOR)) {
            return (current_node.getPrior() != null) ? current_node.getPrior().getData().getValue() : initialWord;
        } else if (qualifierWord.equals(Word.RIGHT_NEIGHBOR)) {
            return (current_node.getNext() != null) ? current_node.getNext().getData().getValue() : initialWord;
        }
        return initialWord;
    }

    
    /**
     * Parses a string to a positive integer. If parsing fails or the value is negative, a default value is returned.
     *
     * <p>This utility method attempts to convert the provided string into a positive integer.
     * In case of failure or if the number is less than 0, it returns the provided default value.</p>
     *
     * @param input        The string input to parse.
     * @param defaultValue The default integer value to return if parsing fails.
     * @return A positive integer parsed from the input, or the default value.
     */
    private int parsePositiveIntOrDefault(String input, int defaultValue) {
        try {
            int parsed = Integer.parseInt(input);
            return (parsed < 0) ? defaultValue : parsed;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Attempts to adjust the current token using neighbor tokens within a maximum distance.
     *
     * <p>This method iterates from 1 to the given maximum distance and attempts to use either the left or the right neighbor
     * tokens to form a candidate token value that matches using the current matcher.
     * If a match is found, the token is updated and the method returns {@code true}.</p>
     *
     * @param maxDistance   The maximum number of characters to borrow from neighbor tokens.
     * @param attributeName The name of the attribute being processed.
     * @return {@code true} if a successful match is found by borrowing neighbor characters; {@code false} otherwise.
     */
    private boolean tryNeighbors(int maxDistance, String attributeName) {
        for (int distance = 1; distance <= maxDistance; distance++) {
            if (tryLeftNeighbor(distance, attributeName)) {
                return true;
            }
            if (tryRightNeighbor(distance, attributeName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Attempts to form a candidate token by borrowing characters from the left neighbor.
     *
     * <p>This method checks if the left neighbor token is available and unprocessed. It then borrows up to the specified number
     * of characters from the end of the left neighbor, concatenates it with the current working word, and checks if it matches.
     * If matched, the left token is updated accordingly and the candidate is recorded as output for the attribute.</p>
     *
     * @param charDistance  The maximum number of characters to borrow from the left neighbor.
     * @param attributeName The name of the attribute currently being processed.
     * @return {@code true} if the neighbor-based candidate forms a valid match; {@code false} otherwise.
     */
    private boolean tryLeftNeighbor(int charDistance, String attributeName) {
        Node<Token> leftNode = current_node.getPrior();
        if (leftNode != null && !leftNode.getData().isProcessed()) {
            String leftValue = leftNode.getData().getValue();
            if (leftValue.isEmpty()) return false;
            
            // Borrow up to charDistance characters from the end of the left token.
            int effectiveDistance = Math.min(charDistance, leftValue.length());
            String neighborPart = leftValue.substring(leftValue.length() - effectiveDistance);
            String candidate = neighborPart + working_word;
            String matchResult = current_matcher.match(candidate);
            if (!matchResult.isEmpty()) {
                // Remove the used portion from the left token.
                String remaining = leftValue.substring(0, leftValue.length() - effectiveDistance);
                leftNode.getData().setValue(remaining);
                if (remaining.isEmpty()) {
                    leftNode.getData().setProcessed(true);
                }
                // Update the current token with the merged candidate and mark as processed.
                current_node.getData().setValue(candidate);
                current_node.getData().setProcessed(true);
                outputAttributes.put(attributeName, candidate);
                return true;
            }
        }
        return false;
    }

    /**
     * Attempts to form a candidate token by borrowing characters from the right neighbor.
     *
     * <p>This method checks if the right neighbor token is available and unprocessed.
     * It then borrows up to the specified number of characters from the beginning of the right neighbor,
     * appends it to the current working word, and validates the candidate using the matcher.
     * If a valid candidate is found, the right token is updated and the candidate is output for the attribute.</p>
     *
     * @param charDistance  The maximum number of characters to borrow from the right neighbor.
     * @param attributeName The name of the attribute currently being processed.
     * @return {@code true} if the candidate formed by the right neighbor is valid; {@code false} otherwise.
     */
    private boolean tryRightNeighbor(int charDistance, String attributeName) {
        Node<Token> rightNode = current_node.getNext();
        if (rightNode != null && !rightNode.getData().isProcessed()) {
            String rightValue = rightNode.getData().getValue();
            if (rightValue.isEmpty()) return false;

            // Borrow up to charDistance characters from the beginning of the right token
            int effectiveDistance = Math.min(charDistance, rightValue.length());
            String neighborPart = rightValue.substring(0, effectiveDistance);
            String candidate = working_word + neighborPart;
            String matchResult = current_matcher.match(candidate);
            if (!matchResult.isEmpty()) {
                // Remove the used portion from the right token
                String remaining = rightValue.substring(effectiveDistance);
                rightNode.getData().setValue(remaining);
                if (remaining.isEmpty()) {
                    rightNode.getData().setProcessed(true);
                }
                // Update the current token with the merged candidate and mark as processed
                current_node.getData().setValue(candidate);
                current_node.getData().setProcessed(true);
                outputAttributes.put(attributeName, candidate);
                return true;
            }
        }
        return false;
    }

    /**
     * Builds the final output line from the tokens and attribute order.
     *
     * <p>This method traverses the token list to concatenate any tokens that were not processed.
     * It then builds a formatted line by first appending attribute values (in the order specified by {@code attributeOutputOrder})
     * and then appending any leftover tokens, creating the final output string.</p>
     *
     * @param tokenList      The doubly linked list of tokens representing the processed input line.
     * @param attributeOutputOrder The list defining the order in which attribute values should appear.
     * @return A {@code String} representing the final formatted output line.
     */
    private String buildOutputLine(DoublyLinkedList<Token> tokenList, List<String> attributeOutputOrder) {
        StringBuilder leftoverBuilder = new StringBuilder();
        Node<Token> current = tokenList.getHead();
        while (current != null) {
            Token token = current.getData();
            if (!token.isProcessed()) {
                leftoverBuilder.append(token.getValue()).append(" ");
            }
            current = current.getNext();
        }
        StringBuilder formattedLine = new StringBuilder();
        for (String attrName : attributeOutputOrder) {
            String value = outputAttributes.getOrDefault(attrName, "");
            formattedLine.append(value).append(",");
        }
        formattedLine.append(leftoverBuilder.toString().trim());
        return formattedLine.toString();
    }
}
