package dev.tbm00.preprocessit.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import dev.tbm00.preprocessit.model.data.Component;
import dev.tbm00.preprocessit.model.data.Attribute;
import dev.tbm00.preprocessit.model.data.Qualifier;
import dev.tbm00.preprocessit.model.data.Token;
import dev.tbm00.preprocessit.model.data.Node;
import dev.tbm00.preprocessit.model.data.DoublyLinkedList;
import dev.tbm00.preprocessit.model.data.LineRule;
import dev.tbm00.preprocessit.model.data.LineResult;
import dev.tbm00.preprocessit.model.data.enums.Action;
import dev.tbm00.preprocessit.model.data.enums.ActionResult;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;
import dev.tbm00.preprocessit.model.data.enums.Word;
import dev.tbm00.preprocessit.model.data.enums.WordSpec;
import dev.tbm00.preprocessit.model.actioneer.ActioneerFactory;
import dev.tbm00.preprocessit.model.actioneer.ActioneerInterface;
import dev.tbm00.preprocessit.model.matcher.MatcherInterface;

/**
 * Handles the processing of input data line, by applying lines rules, evaluating tokens 
 * against component attributes' qualifiers and actioning as configured.
 */
public class LineProcessor {
    private Component component;
    private DoublyLinkedList<Token> tokenList;
    private Map<String, String> outputAttributes = new HashMap<>();

    private String INITIAL_LINE_COPY;
    private String INITIAL_TOKEN_COPY;
    private int skip_qualifier = 0;
    private MatcherInterface current_matcher = null;
    private Node<Token> current_node = null;
    private String prior_token_value = null;
    private String prior_working_word = null;
    private String working_word = null;

    private StringBuilder leftoverBuilder = new StringBuilder();
    private List<String> log = new ArrayList<String>();

    /**
     * Processes a single input line for the specified component.
     * 
     * <p>This method applies all input {@link LineRule}s to transform the entire line, then tokenizes the resulting line 
     * into a {@link DoublyLinkedList} of {@link Token}s, processes each component attribute by evaluating 
     * their qualifiers and executing actions, builds the formatted output line, and finally applies all 
     * output {@link LineRule}s to transform the entire line.</p>
     *
     * @param index The input line's index.
     * @return A {@code String} representing the processed output line.
     */
    public LineResult processLine(int index, String inputLine, Component component) {
        this.component = component;
        log.add(" ");
        log.add(" ");
        log.add(" ");
        log.add("------======||[ Starting Line "+index+" Processing ]||======------");

        // Process input LineRules
        log.add(" ");
        log.add("Processing inputLineRules for line " +index+ "...");
        prior_working_word = inputLine;
        working_word = inputLine;
        INITIAL_LINE_COPY = inputLine;
        inputLine = processLineRules(inputLine, "input").trim();
        if (inputLine.equals("$DELETE_ME$")) {
            return new LineResult(index, "", log);
        }

        // Reset local variables after processing InputLineRules
        skip_qualifier = 0;
        current_matcher = null;
        prior_working_word = null;
        working_word = null; 

        // Process input Attributes
        log.add(" ");
        log.add("Processing attributes for line " +index+ "...");
        tokenList = tokenizeLine(inputLine);
        outputAttributes.clear();
        processAttributes();
        
        // Reset local variables after processing Attributes
        skip_qualifier = 0;
        current_matcher = null;

        // Generate output Line
        String outputLine = buildOutputLine();
        prior_working_word = outputLine;
        working_word = outputLine;
        
        // Process output LineRules
        log.add(" ");
        log.add("Processing outputLineRules for line " +index+ "...");
        outputLine = processLineRules(outputLine, "output");
        if (outputLine.equals("$DELETE_ME$")) {
            outputLine = "";
        }

        return new LineResult(index, outputLine, log);
    }

    /**
     * Processes each line rule for the current component on the given token list.
     *
     * <p>This method iterates over all qualifiers for the specified component's LineRules.
     * It calls {@link #processQualifiers(String, Component, Attribute)} to process the entire line using the qualifiers.</p>
     *
     * @param tokenList The list of tokens generated from a line of input text.
     * @param type      The type of line rules that are running (input/output)
     */
    private String processLineRules(String line, String type) {
        LineRule lineRule;
        if (type.equals("input")) lineRule = component.getInputLineRule();
        else if (type.equals("output")) lineRule = component.getOutputLineRule();
        else lineRule = null;

        if (lineRule!=null) {
            ActionResult result = processQualifiers(line, component, null, lineRule.getQualifiers());
            switch (result) {
                case REMOVE_LINE:
                    log.add("[-] "+type+" line rule(s) processed and is deleting the line");
                    return "$DELETE_ME$";
                default:
                    log.add("[-] "+type+" line rule(s) processed, returning: "+ working_word);
                    return working_word;
            }
            
        } else {
            log.add("[-] no "+type+" line rule(s) found");
            return line;
        }
    }

    /**
     * Processes each attribute for the current component on the given token list.
     *
     * <p>This method iterates over all attributes of the specified component. For each attribute, it avoids
     * processing if an output for that attribute is already present. Otherwise, it calls {@link #processAttribute(DoublyLinkedList, Attribute)}
     * to process individual tokens.</p>
     *
     * @param tokenList The list of tokens generated from a line of input text.
     */
    private void processAttributes() {

        attributeLoop:
        for (Attribute attribute : component.getAttributes()) {
            if (outputAttributes.containsKey(attribute.getName())) {
                continue;
            }
            ActionResult result = processAttribute(tokenList, component, attribute);
            if (result.equals(ActionResult.NEXT_ATTRIBUTE)) {
                log.add("[-] attribute processed, going to next attribute");
                continue attributeLoop;
            } else {
                log.add("[-] attribute processed, going to next line");
                return;
            }
        }
    }

    /**
     * Processes a single attribute by iterating through the token list and applying its qualifiers.
     *
     * <p>This method starts processing at the head of the token list. For each unprocessed token, it applies the attribute's
     * qualifiers via {@link #processQualifiers(String, Component, Attribute)}. Depending on the result, it either continues to the next token,
     * or returns early if processing of the attribute is complete.</p>
     *
     * @param tokenList The list of tokens for the current line.
     * @param component The component whose attributes are being processed.
     * @param attribute The attribute whose qualifiers need to be processed.
     * @return An {@code ActionResult} indicating the next processing step.
     */
    private ActionResult processAttribute(DoublyLinkedList<Token> tokenList, Component component, Attribute attribute) {
        current_node = tokenList.getHead();
        prior_token_value = current_node.getData().getValue();

        log.add(" ");
        log.add(" ");
        log.add("---===|[ Starting Attribute "+attribute.getName()+" Processing ]|===---");
        
        tokenLoop:
        while (current_node != null) {
            Token token = current_node.getData();
            if (!token.isProcessed() && !token.getValue().isEmpty()) {
                INITIAL_TOKEN_COPY = token.getValue();
                prior_working_word = null;
                working_word = INITIAL_TOKEN_COPY;

                // Process each qualifier for the attribute.
                ActionResult result = processQualifiers(INITIAL_TOKEN_COPY, component, attribute, attribute.getQualifiers());
                if (result.equals(ActionResult.NEXT_TOKEN)) {
                    prior_token_value = current_node.getData().getValue();
                    current_node = current_node.getNext();
                    log.add("[-] attribute continuing tokenLoop");
                    continue tokenLoop;
                } else {
                    prior_token_value = current_node.getData().getValue();
                    current_node = current_node.getNext();
                    log.add("[-] attribute bumped the current token to the next neighbor!");
                    if (current_node != null) log.add("      (bumped to token: "+current_node.getData().getValue()+")");
                    else log.add("      (bumped token is non-existent!)");
                    return result;
                }
            } else {
                // If token is processed or empty, skip it.
                prior_token_value = current_node.getData().getValue();
                current_node = current_node.getNext();
                if (token.isProcessed()) {
                    log.add("[-] attribute bumped the current token to the next neighbor because the current token was already processed!");
                    if (current_node != null) log.add("      (bumped to token: "+current_node.getData().getValue()+")");
                    else log.add("      (bumped token is non-existent!)");
                } else {
                    log.add("[-] attribute bumped the current token to the next neighbor because the current token was non-existent!");
                    if (current_node != null) log.add("      (bumped to token: "+current_node.getData().getValue()+")");
                    else log.add("      (bumped token is non-existent!)");
                }
            }
        }
        return ActionResult.NEXT_ATTRIBUTE;
    }

    /**
     * Evaluates all qualifiers for a given attribute using the provided initial word.
     *
     * <p>This method iterates over each passed-in qualifier. It first determines the working word based on the qualifier,
     * then obtains a matched string from the qualifier's matcher. Depending on whether a match is found, the method chooses a set
     * of actions to execute. Execution results are used to decide whether to continue with further qualifiers or tokens.</p>
     *
     * @param initialWord The original token value to be evaluated.
     * @param component   The component whose input line rule qualifiers are to be processed.
     * @param attribute   The attribute whose qualifiers are to be processed.
     * @param qualifiers  The qualifiers to be processed.
     * @return An {@code ActionResult} indicating the next processing step.
     */
    private ActionResult processQualifiers(String initialWord, Component component, Attribute attribute, ArrayList<Qualifier> qualifiers) {
        boolean isLineRule = (attribute == null);

        // For each qualifier in the attribute
        qualifierLoop:
        for (Qualifier qualifier : qualifiers) {
            if (skip_qualifier > 0) {
                skip_qualifier--;
                continue qualifierLoop;
            }

            // Determine which word to use based on the qualifier type.
            prior_working_word = working_word;
            working_word = determineWorkingWord(qualifier.getWordSpec());
            current_matcher = qualifier.getMatcher();
            String matchedString = current_matcher.match(working_word);

            log.add(" ");
            if (tokenList!=null) log.add("Current token list: " + tokenList.getForwards());
            log.add("Starting qualifier...");
            if (isLineRule) log.add(component.getName()+"'s LineRule's "+qualifier.getWordSpec().toString()+" "+qualifier.getCondition().name()+" '"+qualifier.getValues() +"'  ::  '"+ working_word + "' -> '" + matchedString + "'");
            else log.add(attribute.getName()+"'s "+qualifier.getWordSpec().toString()+" "+qualifier.getCondition().name()+" '"+qualifier.getValues() +"'  ::  '"+ working_word + "' -> '" + matchedString + "'");

            // Decide which set of actions to use
            ActionSpec[] actionSpecs = (matchedString.isEmpty()) ? qualifier.getUnqualifiedActions()
                                                                 : qualifier.getQualifiedActions();

            if (!matchedString.isEmpty()) {
                log.add("[-] therefore qualified actions will run");
            } else log.add("[-] therefore unqualified actions will run");

            // Execute the actions; if one action “ships” (matches) the attribute then exit.
            ActionResult result;
            if (attribute==null) result = executeActions(matchedString, actionSpecs, null);
            else result = executeActions(matchedString, actionSpecs, attribute.getName());
            if (result.equals(ActionResult.NEXT_QUALIFIER)) {
                log.add("[-] qualifier continuing qualifierLoop");
                continue qualifierLoop;
            } else {
                //log.add("[-] qualifier returning result: " + result.name());
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
                log.add("[-] action continuing executeLoop");
                continue executeLoop;
            } else {
                //log.add("[-] action returning result: " + result.name());
                return result;
            }
        }

        return ActionResult.NEXT_QUALIFIER;
    }

    /**
     * Executes a single action for the current qualifier.
     *
     * <p>This method performs the specific action as defined by the given action specification.
     * It handles various action types (such as SHIP, EXITs, CONTINUEs, & more actions) using a switch statement.
     * Based on the action executed, the working token is updated, tokens may be marked as processed, and the appropriate
     * {@code ActionResult} is returned.</p>
     *
     * @param matchedString The string that was matched by the qualifier's matcher.
     * @param actionSpec    The specification of the action to be executed.
     * @param attributeName The name of the attribute associated with the action.
     * @return An {@code ActionResult} indicating the outcome of the action execution.
     */
    private ActionResult executeAction(String matchedString, ActionSpec actionSpec, String attributeName) {
        boolean isLineRule = (attributeName == null);
        Action action = actionSpec.getAction();
        log.add("[-] executing action " + action.name() + "...");

        switch (action) {
            case DELETE_LINE:
                if (isLineRule) {
                    return ActionResult.REMOVE_LINE;
                } else {
                    log.add("      (can only use DELETE_LINE in LineRules)");
                    return ActionResult.NEXT_ACTION;
                }
            case EXIT_TO_NEXT_LINE_ITERATION:
                // Exit evaluation for this line
                return ActionResult.NEXT_LINE;
            case EXIT_TO_NEXT_ATTRIBUTE_ITERATION:
                if (!isLineRule) {
                    // Exit evaluation for this attribute
                    return ActionResult.NEXT_ATTRIBUTE;
                } else {
                    log.add("      (cannot use EXIT_TO_NEXT_ATTRIBUTE_ITERATION in LineRules)");
                    return ActionResult.NEXT_ACTION;
                }
            case EXIT_TO_NEXT_TOKEN_ITERATION:
                if (!isLineRule) {
                    // The calling loop will get the next token
                    return ActionResult.NEXT_TOKEN;
                } else {
                    log.add("      (cannot use EXIT_TO_NEXT_TOKEN_ITERATION in LineRules)");
                    return ActionResult.NEXT_ACTION;
                }
            case CONTINUE_TO_NEXT_QUALIFIER:
                // Just continue processing qualifiers
                return ActionResult.NEXT_QUALIFIER;
            case CONTINUE_AND_SKIP_NEXT_QUALIFIER:
                int skipAmount = parsePositiveIntOrDefault(actionSpec.getParameter(), 1);
                log.add("      (skipping " + skipAmount + " qualifiers)");
                skip_qualifier = skipAmount;
                return ActionResult.NEXT_QUALIFIER;
            case SHIP:
                log.add("      (shipping " + working_word + ")");
                if (!isLineRule) {
                    outputAttributes.put(attributeName, working_word);
                }
                return ActionResult.NEXT_ACTION;
            case DECLARE_TOKEN_PROCESSED:
                if (!isLineRule) {
                    log.add("      (declaring token as processed)");
                    current_node.getData().setProcessed(true);
                    return ActionResult.NEXT_ACTION;
                } else {
                    log.add("      (cannot use DECLARE_TOKEN_PROCESSED in LineRules)");
                    return ActionResult.NEXT_ACTION;
                }
            case SET_WORKING_WORD: {
                working_word = actionSpec.getParameter();

                String leftovers = leftoverBuilder.toString().trim();
                if (INITIAL_TOKEN_COPY!=null) working_word = working_word.replace("$INITIAL_TOKEN_COPY$", INITIAL_TOKEN_COPY);
                if (INITIAL_LINE_COPY!=null) working_word = working_word.replace("$INITIAL_LINE_COPY$", INITIAL_LINE_COPY);
                if (leftovers!=null) working_word = working_word.replace("$LEFTOVERS$", leftovers);
                if (prior_working_word!=null) working_word = working_word.replace("$PRIOR_WORKING_WORD$", prior_working_word);
                if (prior_token_value!=null) working_word = working_word.replace("$PRIOR_TOKEN_VALUE$", prior_token_value);

                log.add("      (set working word to: "+working_word+")");
                return ActionResult.NEXT_ACTION;
            }
            case SET_TOKEN_VALUE: {
                current_node.getData().setValue(actionSpec.getParameter());
                log.add("      (set token value to: "+actionSpec.getParameter()+")");
                return ActionResult.NEXT_ACTION;
            }
            case TRY_NEIGHBORS:
                if (!isLineRule) {
                    String param = actionSpec.getParameter();
                    String[] parts = param.split(",", 2);
                    if (parts.length < 2) {
                        log.add("      (trying neighbors failed, invalid parameter format)");
                        return ActionResult.NEXT_ACTION;
                    }

                    int distance = parsePositiveIntOrDefault(parts[0], 1);
                    Integer nIndex = Integer.valueOf(parts[1]);
                    if (nIndex==null) nIndex = 1;

                    log.add("      (trying neighbors " + nIndex + " away, appending " + distance + " characters from each)");
                    if (tryNeighbors(distance, attributeName, nIndex)) {
                        return ActionResult.NEXT_ACTION;
                    } else {
                        return ActionResult.NEXT_ACTION;
                    }
                } else {
                    log.add("      (cannot use TRY_NEIGHBORS in LineRules)");
                    return ActionResult.NEXT_ACTION;
                }
            case TRIM_MATCH_FROM_LEFT_NEIGHBOR:
                if (!isLineRule) {
                    Integer nIndex = Integer.valueOf(actionSpec.getParameter());
                    if (nIndex==null) {
                        nIndex = 1;
                    }

                    Node<Token> leftNode = current_node;
                    for (int i=0; i<nIndex; ++i) {
                        leftNode = leftNode.getPrior();
                    }

                    if (leftNode != null) {
                        ActioneerInterface actioneer = ActioneerFactory.getActioneer(action);
                        if (actioneer != null) {
                            String newValue = actioneer.execute(leftNode.getData().getValue(), actionSpec, matchedString, log);
                            leftNode.getData().setValue(newValue);
                            if (newValue==null || newValue.isEmpty() || newValue.equals("")) {
                                leftNode.getData().setProcessed(true);
                                log.add("      (removed match from left neighbor, updated neighbor token: " + newValue + " (value is empty, therefore marked as processed))");
                            } else {
                                log.add("      (removed match from left neighbor, updated neighbor token: " + newValue + ")");
                            }
                        } else {
                            log.add("      (no executor found for Action." + actionSpec.getAction().name() + ")");
                        }
                    } else {
                        log.add("      (left neighbor " + nIndex + " is non-existent!)");
                    }
                    return ActionResult.NEXT_ACTION;
                } else {
                    log.add("      (cannot use TRIM_MATCH_FROM_LEFT_NEIGHBOR in LineRules)");
                    return ActionResult.NEXT_ACTION;
                }
            case TRIM_MATCH_FROM_RIGHT_NEIGHBOR:
                if (!isLineRule) {
                    Integer nIndex = Integer.valueOf(actionSpec.getParameter());
                    if (nIndex==null) {
                        nIndex = 1;
                    }

                    Node<Token> rightNode = current_node;
                    for (int i=0; i<nIndex; ++i) {
                        rightNode = rightNode.getNext();
                    }

                    if (rightNode != null) {
                        ActioneerInterface actioneer = ActioneerFactory.getActioneer(action);
                        if (actioneer != null) {
                            String newValue = actioneer.execute(rightNode.getData().getValue(), actionSpec, matchedString, log);
                            rightNode.getData().setValue(newValue);
                            if (newValue==null || newValue.isEmpty() || newValue.equals("")) {
                                rightNode.getData().setProcessed(true);
                                log.add("      (removed match from right neighbor, updated neighbor token: " + newValue + " (value is empty, therefore marked as processed))");
                            } else {
                                log.add("      (removed match from right neighbor, updated neighbor token: " + newValue + ")");
                            }
                        } else {
                            log.add("      (no executor found for Action." + actionSpec.getAction().name() + ")");
                        }
                    } else {
                        log.add("      (right neighbor " + nIndex + " is non-existent!)");
                    }
                    return ActionResult.NEXT_ACTION;
                } else {
                    log.add("      (cannot use TRIM_MATCH_FROM_RIGHT_NEIGHBOR in LineRules)");
                    return ActionResult.NEXT_ACTION;
                }
            case TRIM_MATCH_FROM_TOKEN:
                if (!isLineRule) {
                    if (current_node != null) {
                        ActioneerInterface actioneer = ActioneerFactory.getActioneer(action);
                        if (actioneer != null) {
                            prior_token_value = current_node.getData().getValue();
                            String newValue = actioneer.execute(current_node.getData().getValue(), actionSpec, matchedString, log);
                            current_node.getData().setValue(newValue);
                            if (newValue==null || newValue.isEmpty() || newValue.equals("")) {
                                current_node.getData().setProcessed(true);
                                log.add("      (removed match from current token, updated token: " + newValue + " (value is empty, therefore marked as processed))");
                            } else {
                                log.add("      (removed match from current token, updated token: " + newValue + ")");
                            }
                        } else {
                            log.add("      (no executor found for Action." + actionSpec.getAction().name() + ")");
                        }
                    }
                    return ActionResult.NEXT_ACTION;
                } else {
                    log.add("      (cannot use TRIM_MATCH_FROM_TOKEN in LineRules)");
                    return ActionResult.NEXT_ACTION;
                }
            case TRIM_UNMATCHED_FROM_TOKEN:
                if (!isLineRule) {
                    if (current_node != null) {
                        ActioneerInterface actioneer = ActioneerFactory.getActioneer(action);
                        if (actioneer != null) {
                            String unmatchedString = actioneer.execute(working_word, actionSpec, matchedString, log);
                            if (unmatchedString.isEmpty()) {
                                log.add("      (token was not modified because because unmatched value is empty)");
                                return ActionResult.NEXT_ACTION;
                            }

                            prior_token_value = current_node.getData().getValue();
                            String newValue = actioneer.execute(current_node.getData().getValue(), actionSpec, unmatchedString, log);
                            current_node.getData().setValue(newValue);
                            if (newValue==null || newValue.isEmpty() || newValue.equals("")) {
                                current_node.getData().setProcessed(true);
                                log.add("      (removed unmatched from current token, updated token: " + newValue + " (value is empty, therefore marked as processed))");
                            } else {
                                log.add("      (removed unmatched from current token, updated token: " + newValue + ")");
                            }
                        } else {
                            log.add("      (no executor found for Action." + actionSpec.getAction().name() + ")");
                        }
                    }
                    return ActionResult.NEXT_ACTION;
                } else {
                    log.add("      (cannot use TRIM_UNMATCHED_FROM_TOKEN in LineRules)");
                    return ActionResult.NEXT_ACTION;
                }
            case NEW_TOKEN_FROM_MATCH:
                if (!isLineRule) {
                    if (matchedString.isEmpty()) {
                        log.add("      (no new token created because matched value is empty)");
                        return ActionResult.NEXT_ACTION;
                    }
                    tokenList.addAfter(current_node, new Token(matchedString));
                    log.add("      (added new token after current token, matched value: " + matchedString + ")");
                    return ActionResult.NEXT_ACTION;
                } else {
                    log.add("      (cannot use NEW_TOKEN_FROM_MATCH in LineRules)");
                    return ActionResult.NEXT_ACTION;
                }
            case NEW_TOKEN_FROM_UNMATCHED:
                if (!isLineRule) {
                    ActioneerInterface actioneer = ActioneerFactory.getActioneer(action);
                    if (actioneer != null) {
                        String unmatchedString = actioneer.execute(working_word, actionSpec, matchedString, log);
                        if (unmatchedString.isEmpty()) {
                            log.add("      (no new token created because unmatched value is empty)");
                            return ActionResult.NEXT_ACTION;
                        }
                        tokenList.addAfter(current_node, new Token(unmatchedString));
                        log.add("      (added new token after current token, unmatched value: " + unmatchedString + ")");
                    } else {
                        log.add("      (no executor found for Action.NEW_TOKEN_FROM_UNMATCHED)");
                    }
                    return ActionResult.NEXT_ACTION;
                } else {
                    log.add("      (cannot use NEW_TOKEN_FROM_UNMATCHED in LineRules)");
                    return ActionResult.NEXT_ACTION;
                }
            default:
                // For any other action, attempt to execute it
                ActioneerInterface actioneer = ActioneerFactory.getActioneer(action);
                if (actioneer != null) {
                    working_word = actioneer.execute(working_word, actionSpec, matchedString, log);

                    String leftovers = leftoverBuilder.toString().trim();
                    if (INITIAL_TOKEN_COPY!=null) working_word = working_word.replace("$INITIAL_TOKEN_COPY$", INITIAL_TOKEN_COPY);
                    if (INITIAL_LINE_COPY!=null) working_word = working_word.replace("$INITIAL_LINE_COPY$", INITIAL_LINE_COPY);
                    if (leftovers!=null) working_word = working_word.replace("$LEFTOVERS$", leftovers);
                    if (prior_working_word!=null) working_word = working_word.replace("$PRIOR_WORKING_WORD$", prior_working_word);
                    if (prior_token_value!=null) working_word = working_word.replace("$PRIOR_TOKEN_VALUE$", prior_token_value);

                    log.add("      (updated working word to: " + working_word + ")");
                } else {
                    log.add("      (no executor found for Action." + actionSpec.getAction().name() + ")");
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
     * @param wordSpec The qualifier word spec indicating which token value to use.
     * @return The token value to be used as the working word.
     */
    private String determineWorkingWord(WordSpec wordSpec) {

        Integer nIndex = Integer.valueOf(wordSpec.getParameter());
        if (nIndex==null) {
            nIndex = 1;
        }

        if (wordSpec.getWord().equals(Word.WORKING_TOKEN)||wordSpec.getWord().equals(Word.WORKING_LINE)) {
            return working_word;
        } else if (wordSpec.getWord().equals(Word.INITIAL_TOKEN_COPY)) {
            return INITIAL_TOKEN_COPY;
        } else if (wordSpec.getWord().equals(Word.INITIAL_LINE_COPY)) {
            return INITIAL_LINE_COPY;
        } else if (wordSpec.getWord().equals(Word.LEFT_NEIGHBOR) || wordSpec.getWord().equals(Word.RIGHT_NEIGHBOR)) {
            Node<Token> leftNode = current_node, rightNode = current_node;
            for (int i=0; i<nIndex; ++i) {
                if (leftNode!=null) leftNode = leftNode.getPrior();
                if (rightNode!=null) rightNode = rightNode.getNext();
            }
            if (wordSpec.getWord().equals(Word.LEFT_NEIGHBOR)) return (leftNode != null) ? leftNode.getData().getValue() : "";
            if (wordSpec.getWord().equals(Word.RIGHT_NEIGHBOR)) return (rightNode != null) ? rightNode.getData().getValue() : "";
        } 
        return INITIAL_TOKEN_COPY;
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
     * Attempts to adjust the current working word using neighbor tokens within a maximum distance.
     *
     * <p>This method iterates from 1 to the given maximum distance and attempts to use either the left or the 
     * right neighbor tokens to form a candidate token value that matches using the current matcher.
     * If a match is found, the method returns the updated working word is updated and the method returns {@code true}.</p>
     *
     * @param maxDistance   The maximum number of characters to borrow from neighbor tokens.
     * @param attributeName The name of the attribute being processed.
     * @param attributeName The neighbor index to try (1 for first, 2 for second, etc.).
     * @return {@code true} if a successful match is found by borrowing neighbor characters; {@code false} otherwise.
     */
    private boolean tryNeighbors(int maxDistance, String attributeName, int index) {
        Node<Token> leftNode = current_node, rightNode = current_node;
        for (int i=0; i<index; ++i) {
            leftNode = leftNode.getPrior();
            rightNode = rightNode.getNext();
        }
        for (int distance = 1; distance <= maxDistance; distance++) {
            if (tryLeftNeighbor(distance, attributeName, leftNode)) {
                return true;
            }
            if (tryRightNeighbor(distance, attributeName, rightNode)) {
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
     * @param node The neighboring token node to be tried.
     * @return {@code true} if the neighbor-based candidate forms a valid match; {@code false} otherwise.
     */
    private boolean tryLeftNeighbor(int charDistance, String attributeName, Node<Token> node) {
        if (node != null && !node.getData().isProcessed()) {
            String leftValue = node.getData().getValue();
            if (leftValue.isEmpty()) return false;
            
            // Borrow up to charDistance characters from the end of the left token.
            int effectiveDistance = Math.min(charDistance, leftValue.length());
            String neighborPart = leftValue.substring(leftValue.length() - effectiveDistance);
            String candidate = neighborPart + working_word;
            String matchResult = current_matcher.match(candidate);
            if (!matchResult.isEmpty()) {
                working_word = candidate;
                prior_token_value = current_node.getData().getValue();
                current_node.getData().setValue(candidate);

                String newValue = leftValue.substring(0, (leftValue.length() - effectiveDistance)-1);
                node.getData().setValue(newValue);
                if (newValue==null || newValue.isEmpty() || newValue.equals("")) {
                    node.getData().setProcessed(true);
                    log.add("      (matched with left neighboring token, updated current token: " + candidate);
                    log.add("                                          , updated neighbor token: " + newValue + " (value is empty, therefore marked as processed) )");
                } else {
                    log.add("      (matched with left neighboring token, updated current token: " + candidate);
                    log.add("                                          , updated neighbor token: " + newValue + ")");
                }
                return true;
            } else {
                log.add("      (no match with left neighboring token)");
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
     * @param node The neighboring token node to be tried.
     * @return {@code true} if the candidate formed by the right neighbor is valid; {@code false} otherwise.
     */
    private boolean tryRightNeighbor(int charDistance, String attributeName, Node<Token> node) {
        if (node != null && !node.getData().isProcessed()) {
            String rightValue = node.getData().getValue();
            if (rightValue.isEmpty()) return false;

            // Borrow up to charDistance characters from the beginning of the right token
            int effectiveDistance = Math.min(charDistance, rightValue.length());
            String neighborPart = rightValue.substring(0, effectiveDistance);
            String candidate = working_word + neighborPart;
            String matchResult = current_matcher.match(candidate);
            if (!matchResult.isEmpty()) {
                working_word = candidate;
                prior_token_value = current_node.getData().getValue();
                current_node.getData().setValue(candidate);

                String newValue = rightValue.substring(effectiveDistance, rightValue.length()-1);
                node.getData().setValue(newValue);
                if (newValue==null || newValue.isEmpty() || newValue.equals("")) {
                    node.getData().setProcessed(true);
                    log.add("      (matched with right neighboring token, updated current token: " + candidate);
                    log.add("                                           , updated neighbor token: " + newValue + " (value is empty, therefore marked as processed) )");
                } else {
                    log.add("      (matched with right neighboring token, updated current token: " + candidate);
                    log.add("                                           , updated neighbor token: " + newValue + ")");
                }
                return true;
            } else {
                log.add("      (no match with right neighboring token)");
            }
        }
        return false;
    }

    /**
     * Builds the final output line from the tokens and attribute order.
     *
     * <p>This method traverses the token list to build leftovers from any tokens that were not processed.
     * It then builds a formatted line by first appending attribute values (in the order specified by {@code attributeOutputOrder})
     * then it returns the final output string.</p>
     *
     * @return A {@code String} representing the final formatted output line.
     */
    private String buildOutputLine() {
        List<String> attributeOutputOrder = component.getAttributeOrder();
        Node<Token> current = tokenList.getHead();
        String delimiter = component.getAttributeDelimiter();
        while (current != null) {
            Token token = current.getData();
            if (!token.isProcessed()) {
                leftoverBuilder.append(token.getValue()).append(" ");
            }
            current = current.getNext();
        }

        StringBuilder formattedLine = new StringBuilder();
        int commasToAdd = attributeOutputOrder.size()-1;
        int commaCount = 0;
        for (String attrName : attributeOutputOrder) {
            String value = outputAttributes.getOrDefault(attrName, "");
            formattedLine.append(value);
            if (commaCount<commasToAdd) {
                formattedLine.append(delimiter);
                ++commaCount;
            }
        }

        return formattedLine.toString();
    }
}
