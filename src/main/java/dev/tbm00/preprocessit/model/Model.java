package dev.tbm00.preprocessit.model;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import dev.tbm00.preprocessit.datastructures.Component;
import dev.tbm00.preprocessit.datastructures.DoublyLinkedList;

/**
 * Model handles the data & state of the application
 */
public class Model {
    private final String appDirectory;
    private List<Component> components;
    private Component selectedComponent;
    private String inputText;
    private String outputText;

    public Model() {
        appDirectory = System.getProperty("user.home") + "/Documents/PreProcessIt";
        components = null;
        selectedComponent = null;
        inputText = "";
        outputText = "";

        File config = getOrCreateConfig();
        loadConfig(config);
    }

    // Processes the input data
    public void processData() {
        if (selectedComponent != null && selectedComponent.getCommands()!=null) {
            DoublyLinkedList<String> dblLnkLst = selectedComponent.getCommands();
            String newOutput = "";
            String[] inputLines = inputText.split("\\r?\\n"); // Handles both \n and \r\n
            for (String inputLine : inputLines) {
                newOutput += (inputLine.toUpperCase() + "\n");

                // WIP

                /* do {
                    // do command on from current dblLnkLst node's string
                    // traverse forwards in DoublyLinkedList<String> dblLnkLst 
                }
                while (next node isnt null>); */
                // OR for(String cmd : dblLnkLst){}
            }
            setOutputText(newOutput);
        }
    }

    private File getOrCreateConfig() {
        Path destinationPath = Paths.get(appDirectory, "config.yml");
        File configFile = destinationPath.toFile();
        
        try {
            if (configFile.exists() && configFile.length()>3) {
                System.out.println("Found config.yml in " + appDirectory);
                return configFile;
            }

            // Create/copy from /resources/config.yml
            // Assuming resources are on the classpath, so we use getResourceAsStream
            InputStream resourceStream = getClass().getResourceAsStream("/config.yml");
            if (resourceStream == null) {
                System.out.println("Could not find default config.yml in program's resources!");
                return null; 
            }

            // Make sure directories exist
            // Create directory in user documents for saving
            try {
                Files.createDirectories(Paths.get(appDirectory));
            } catch (Exception e) {
                System.out.println("Error creating " + appDirectory + ": " + e.getMessage());
                return null;
            }

            // Copy file from resources to local config
            Files.copy(resourceStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            resourceStream.close();
            System.out.println("Created config.yml in " + appDirectory);

            return configFile;
        } catch (Exception e) {
            System.out.println("Error creating default config file in " + appDirectory + ": " + e.getMessage());
            return null;
        }
    }

    public void loadConfig(File givenYaml) {
        if (givenYaml == null) {
            System.out.println("No config file provided to load");
            return;
        }
        
        try (FileInputStream fis = new FileInputStream(givenYaml)) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(fis);

            // Top level key: "componentEntries"
            Map<String, Object> componentEntries = (Map<String, Object>) data.get("componentEntries");

            if (componentEntries == null) {
                System.out.println("No 'componentEntries' found in config");
                return;
            }

            // Initialize components list
            components = new ArrayList<>();

            // Iterate over each entry in componentEntries
            for (Map.Entry<String, Object> componentEntry : componentEntries.entrySet()) {
                Map<String, Object> componentMap = (Map<String, Object>) componentEntry.getValue();
                Component component = new Component();

                // Extract & save entry's name
                String name = (String) componentMap.get("name");
                component.setName(name);

                // Extract & save entry's attributes as ArrayList<String>
                String attrString = (String) componentMap.get("attributes");
                if (attrString != null && !attrString.trim().isEmpty()) {
                    String[] attrArray = attrString.split("\\s+");
                    ArrayList<String> attrList = new ArrayList<>(Arrays.asList(attrArray));
                    component.setAttributes(attrList);
                }

                // Extract & save entry's commands as DoublyLinkedList<String>
                List<String> commandsList = (List<String>) componentMap.get("commands");
                if (commandsList != null && !commandsList.isEmpty()) {
                    DoublyLinkedList<String> commandLinkedList = new DoublyLinkedList<>();
                    for (String cmd : commandsList) {
                        commandLinkedList.addLast(cmd);
                    }
                    component.setCommands(commandLinkedList);
                }

                // Add to component list
                components.add(component);
            }

            System.out.println("Loaded " + components.size() + " component(s) from config");

        } catch (Exception e) {
            System.out.println("Error loading config in " + appDirectory + ": " + e.getMessage());
        }
    }

    public String getAppDirectory() {
        return appDirectory;
    }

    public List<Component> getComponents() {
        return components;
    }

    public Component getSelectedComponent() {
        return selectedComponent;
    }

    public void setComponentByString(String componentString) {
        for (Component t : components) {
            if (t.getName().equalsIgnoreCase(componentString)) {
                this.selectedComponent = t;
                return;
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
