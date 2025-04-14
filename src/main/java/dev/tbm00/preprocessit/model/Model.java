package dev.tbm00.preprocessit.model;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.Component;
import dev.tbm00.preprocessit.model.data.LineResult;

/**
 * Model handles the data & state of the application
 */
public class Model {
    private static ConfigHandler configHandler;
    private List<Component> components;
    private Component selectedComponent;
    private String inputText;
    private String outputText;

    public Model() {
        components = new ArrayList<>();
        configHandler = new ConfigHandler(this);
        inputText = "";
        outputText = "";
    }

    public String processData() {
        if (configHandler.getPoolingEnabled())
            return processDataConcurrent();
        else return processDataSequential();
    }

    private String processDataSequential() {
        Component component = getSelectedComponent();
        if (component == null || component.getAttributes() == null) return "";

        String[] lines = getInputText().split("\\r?\\n");
        StringBuilder newOutput = new StringBuilder();
        LineProcessor lineProcessor = new LineProcessor();

        // Process each line
        for (int i = 0; i < lines.length; i++) {
            newOutput.append(lineProcessor.processLine(i+1, lines[i], component)).append("\n");
        }

        return newOutput.toString();
    }

    private String processDataConcurrent() {
        Component component = getSelectedComponent();
        if (component == null || component.getAttributes() == null) return "";

        // Build thread pool
        int availableProcessors  = Runtime.getRuntime().availableProcessors();
        int poolSize = availableProcessors;
        if (configHandler.getConfiguredPoolSize() > 0) {
            poolSize = Math.max(availableProcessors, configHandler.getConfiguredPoolSize());
        }
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        List<Future<LineResult>> futureList = new ArrayList<>();

        String[] lines = getInputText().split("\\r?\\n");

        // Process each line
        for (int i = 0; i < lines.length; i++) {
            final int lineNumber = i + 1;
            final String line = lines[i];

            // Submit future task that processes one line
            Future<LineResult> future = executor.submit(() -> {
                LineProcessor lineProcessor = new LineProcessor();
                String processedOutput = lineProcessor.processLine(lineNumber, line, component);
                return new LineResult(lineNumber, processedOutput);
            });
            futureList.add(future);
        }

        // Prepare output with originate order
        String[] outputs = new String[lines.length];
        try {
            for (Future<LineResult> future : futureList) {
                LineResult result = future.get();
                outputs[result.lineNumber - 1] = result.output;
            }
        } catch (InterruptedException | ExecutionException e) {
            StaticUtil.log("Exception throw when preparing output!");
            e.printStackTrace();
        }

        // Shutdown executor service
        executor.shutdown();

        // Build and return final string
        StringBuilder newOutput = new StringBuilder();
        for (String outputLine : outputs) {
            newOutput.append(outputLine).append("\n");
        }
        return newOutput.toString().trim();
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void addComponent(Component component) {
        components.add(component);
    }

    public void clearComponents() {
        components.clear();
    }

    public Component getSelectedComponent() {
        return selectedComponent;
    }

    public void setSelectedComponent(String componentString) {
        if (components == null) this.selectedComponent = null;
        else {
            for (Component component : components) {
                if (component.getName().equalsIgnoreCase(componentString)) {
                    this.selectedComponent = component;
                    return;
                }
            }
        }
        this.selectedComponent = null;
    }

    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public String getOutputText() {
        return outputText;
    }

    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }

    public void clearData() {
        this.inputText = "";
        this.outputText = "";
    }
}
