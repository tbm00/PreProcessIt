package dev.tbm00.preprocessit.model;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.actioneer.ActioneerFactory;
import dev.tbm00.preprocessit.model.actioneer.ActioneerInterface;
import dev.tbm00.preprocessit.model.data.Attribute;
import dev.tbm00.preprocessit.model.data.Component;
import dev.tbm00.preprocessit.model.data.DoublyLinkedList;
import dev.tbm00.preprocessit.model.data.Node;
import dev.tbm00.preprocessit.model.data.Qualifier;
import dev.tbm00.preprocessit.model.data.Token;
import dev.tbm00.preprocessit.model.data.enums.Action;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;
import dev.tbm00.preprocessit.model.data.enums.Word;
import dev.tbm00.preprocessit.model.matcher.MatcherInterface;

public class ProcessHandler {
    private final Model model;

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
            Map<String, String> outputAttributes = new HashMap<>();
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
                    continue;
                }
    
                boolean tokenMatched = false;
    
                // Process each attribute from the selected component.
                attribute_iteration:
                for (Attribute attribute : selectedComponent.getAttributes()) {
                    StaticUtil.log(attribute.getName());
                    // Make sure the attribute's list of qualifiers is not null.
                    if (attribute.getQualifiers() == null) {
                        continue;
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
                        ActionSpec[] actionSpecs;
                        
                        if (!matchedString.isEmpty() && matchedString!="") {
                            actionSpecs = qualifier.getQualifiedActions();
                            tokenMatched = true;
                        } else {
                            actionSpecs = qualifier.getUnqualifiedActions();
                        }

                        for (ActionSpec actionSpec : actionSpecs) {
                            if (actionSpec.getAction().equals(Action.TOKEN_SHIP)) {
                                outputAttributes.put(attribute.getName(), token.getValue());
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
                                Integer character_distance = Integer.valueOf(actionSpec.getParameter());
                                if (character_distance==null || character_distance<0) character_distance = 1;
                                // TODO (possibly recursive) code to try the prior and next neigbhors (append X characters from each)
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
}
