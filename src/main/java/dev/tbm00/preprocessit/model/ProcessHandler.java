package dev.tbm00.preprocessit.model;
import java.util.ArrayList;

import dev.tbm00.preprocessit.datastructures.DoublyLinkedList;
import dev.tbm00.preprocessit.datastructures.Component;
import dev.tbm00.preprocessit.datastructures.Attribute;
import dev.tbm00.preprocessit.datastructures.Qualifier;

public class ProcessHandler {
    private final Model model;

    public ProcessHandler(Model model) {
        this.model = model;
    }

    public void processData() {
        Component selectedComponent = model.getSelectedComponent();
        String inputText = model.getInputText();
        if (selectedComponent == null || selectedComponent.getAttributes() == null) {
            // No selected component or no attributes -> return original input or do nothing
            return;
        }

        StringBuilder newOutput = new StringBuilder();

        String[] inputLines = inputText.split("\\r?\\n"); // handle both \n & \r\n
        for (String inputLine : inputLines) {
            /*
            ArrayList<Attribute> attributePool = model.getSelectedComponent().getAttributes();
            String[] tokens = inputLine.split(" ");
            for (String token : tokens) {
                for (Attribute attribute : attributePool) {
                    for (Qualifier qualifier : attribute.getQualifiers()) {
                        // use qualifier to determine if the token is applicable, if so
                    }
                }
            }
            */
            
            // Example transformation: uppercase the line
            String transformedLine = inputLine.toUpperCase();
            newOutput.append(transformedLine).append("\n");
        }

        model.setOutputText(newOutput.toString());
    }
}
