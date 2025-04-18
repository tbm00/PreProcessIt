package dev.tbm00.preprocessit.model;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.Component;
import dev.tbm00.preprocessit.model.data.LineResult;

/**
 * Model handles the data & state of the application.
 *
 * <p>This class manages the application data including the list of components, the currently selected component, the
 * input text, and the output text. It supports processing of the input data either sequentially or concurrently.</p>
 */
public class Model {
    private static ConfigHandler configHandler;
    private List<Component> components;
    private Component selectedComponent;
    private String inputText;
    private String outputText;

    /**
     * Constructs a new Model instance.
     *
     * <p>This constructor initializes the component list, creates a ConfigHandler for configuration management,
     * and sets the default input and output texts to empty strings.</p>
     */
    public Model() {
        components = new ArrayList<>();
        configHandler = new ConfigHandler(this);
        inputText = "";
        outputText = "";
    }

    /**
     * Processes the input data based on the configured pooling mode.
     *
     * <p>This method determines whether to process the input data concurrently or sequentially based on the configuration.
     * If pooling is enabled, it calls {@link #processDataConcurrent()}; otherwise, it calls {@link #processDataSequential()}.
     * The processed output text is returned as a {@code String}.</p>
     *
     * @return A {@code String} representing the processed output data.
     */
    public String processData() {
        if (configHandler.getPoolingEnabled())
            return processDataConcurrent();
        else return processDataSequential();
    }

    /**
     * Processes input data sequentially, line by line.
     *
     * <p>This method retrieves the selected component and splits the input text into individual lines.
     * Each line is processed using a {@code LineProcessor} instance, and the processed lines are concatenated
     * with line breaks to form the final output text.</p>
     *
     * @return A {@code String} representing the processed output text.
     */
    private String processDataSequential() {
        Component component = getSelectedComponent();
        if (component == null || component.getAttributes() == null) return "";

        String[] lines = getInputText().split("\\r?\\n");
        StringBuilder newOutput = new StringBuilder();
        LineProcessor lineProcessor = new LineProcessor();

        // Process each line
        for (int i = 0; i < lines.length; i++) {
            LineResult result = lineProcessor.processLine(i+1, lines[i], component);
            if (result.output!=null) {
                if (!result.output.equals("") && !result.output.isEmpty() && !result.output.equals("null"))
                    newOutput.append(result.output).append("\n");
            }
            for (String line : result.log) {
                StaticUtil.log(line);
            }
        }

        return newOutput.toString();
    }

    /**
     * Processes input data concurrently using a thread pool.
     *
     * <p>This method retrieves the selected component and determines the thread pool size based on the available processors
     * or a configured pool size. It then processes each input line concurrently by submitting tasks to an executor service.
     * Once processing is complete, the results are reassembled in the original order to create the final output text.
     * If any exceptions are encountered during processing, they are logged.</p>
     *
     * @return A {@code String} representing the processed output text generated by concurrent execution.
     */
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
                return lineProcessor.processLine(lineNumber, line, component);
            });
            futureList.add(future);
        }

        // Prepare output with original order
        String[] outputs = new String[lines.length];
        try {
            for (Future<LineResult> future : futureList) {
                LineResult result = future.get();
                if (result.output!=null) {
                    outputs[result.lineNumber - 1] = result.output;
                }
                for (String line : result.log) {
                    StaticUtil.log(line);
                }
            }
        } catch (Exception e) {
            StaticUtil.log("Exception throw when preparing output!");
            e.printStackTrace();
        }

        // Shutdown executor service
        executor.shutdown();

        // Build and return final string
        StringBuilder newOutput = new StringBuilder();
        for (String outputLine : outputs) {
            if (outputLine.equals(null)) {
                StaticUtil.log("outputLine.equals(null)");
            } else if (outputLine.equals("null")) {
                StaticUtil.log("outputLine.equals(\"null\")");
            } else if (outputLine.isEmpty()) {
                StaticUtil.log("outputLine.isEmpty()");
            } else if (outputLine.equals("")) {
                StaticUtil.log("outputLine.equals(\"\")");
            } else {
                newOutput.append(outputLine).append("\n");
            }
        }
        return newOutput.toString().trim();
    }

    /**
     * Returns the configuration handler associated with this model.
     *
     * @return The {@code ConfigHandler} instance managing application configuration.
     */
    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    /**
     * Retrieves the list of components.
     *
     * <p>This method returns the internal list of components managed by the model.
     * Each component represents a distinct processing entity for the input data.</p>
     *
     * @return A {@code List<Component>} containing all added components.
     */
    public List<Component> getComponents() {
        return components;
    }

    /**
     * Adds a new component to the model.
     *
     * <p>This method appends the specified {@code Component} to the internal list of components for later processing.</p>
     *
     * @param component The {@code Component} to be added.
     */
    public void addComponent(Component component) {
        components.add(component);
    }

    /**
     * Clears all components from the model.
     *
     * <p>This method removes all components from the internal list, effectively resetting the components state.</p>
     */
    public void clearComponents() {
        components.clear();
    }

    /**
     * Retrieves the currently selected component.
     *
     * <p>This method returns the component that has been designated for processing.
     * If no component has been selected, it returns {@code null}.</p>
     *
     * @return The currently selected {@code Component}, or {@code null} if no selection has been made.
     */
    public Component getSelectedComponent() {
        return selectedComponent;
    }

    /**
     * Sets the selected component based on the provided component name.
     *
     * <p>This method iterates over the list of components to find one whose name matches the provided string
     * (case-insensitive). If a match is found, it is set as the currently selected component; otherwise,
     * the selected component is set to {@code null}.</p>
     *
     * @param componentString The name of the component to be selected.
     */
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

    /**
     * Retrieves the current input text.
     *
     * <p>This method returns the text content set as input for processing.</p>
     *
     * @return A {@code String} containing the input text.
     */
    public String getInputText() {
        return inputText;
    }

    /**
     * Sets the input text for processing.
     *
     * <p>This method updates the model's input text to the provided string.</p>
     *
     * @param inputText The input text to be set.
     */
    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    /**
     * Retrieves the processed output text.
     *
     * <p>This method returns the text produced after processing the input data.</p>
     *
     * @return A {@code String} containing the processed output text.
     */
    public String getOutputText() {
        return outputText;
    }

    /**
     * Sets the output text for the model.
     *
     * <p>This method updates the model's output text to the provided string.</p>
     *
     * @param outputText The processed output text to be set.
     */
    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }

    /**
     * Clears the input and output data in the model.
     *
     * <p>This method resets both the input and output texts to empty strings,
     * effectively clearing any previously processed data.</p>
     */
    public void clearData() {
        this.inputText = "";
        this.outputText = "";
    }
}
