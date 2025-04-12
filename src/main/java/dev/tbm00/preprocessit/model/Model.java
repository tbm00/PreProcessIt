package dev.tbm00.preprocessit.model;

import java.util.List;

import dev.tbm00.preprocessit.data.Component;

import java.util.ArrayList;

/**
 * Model handles the data & state of the application
 */
public class Model {
    private static ConfigHandler configHandler;
    private static ProcessHandler processHandler;
    private List<Component> components;
    private Component selectedComponent;
    private String inputText;
    private String outputText;


    public Model() {
        components = new ArrayList<>();
        //selectedComponent = null;
        inputText = "";
        outputText = "";
        configHandler = new ConfigHandler(this);
        processHandler = new ProcessHandler(this);
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public ProcessHandler getProcessHandler() {
        return processHandler;
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
