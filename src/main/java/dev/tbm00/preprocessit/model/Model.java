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

/**
 * Model handles the data & state of the application
 */
public class Model {
    private String appDirectory;
    private List<Template> templates;
    private Template selectedTemplate;
    private String inputText;
    private String outputText;

    public Model() {
        appDirectory = System.getProperty("user.home") + "/Documents/PreProcessIt";
        templates = null;
        selectedTemplate = null;
        inputText = "";
        outputText = "";

        File config = getOrCreateConfig();
        loadConfig(config);
    }


    private File getOrCreateConfig() {
        Path destinationPath = Paths.get(appDirectory, "config.yml");
        File configFile = destinationPath.toFile();
        
        try {
            if (configFile.exists() && configFile.length()>3) {
                return configFile;
            }

            // Create/copy from /resources/config.yml
            // Assuming resources are on the classpath, so we use getResourceAsStream
            InputStream resourceStream = getClass().getResourceAsStream("/config.yml");
            if (resourceStream == null) {
                System.out.println("Could not find config.yml in resources!");
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

            return configFile;
        } catch (Exception e) {
            System.out.println("Error creating default config file in " + appDirectory + ": " + e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public void loadConfig(File givenYaml) {
        if (givenYaml == null) {
            System.out.println("No config file provided to load.");
            return;
        }
        
        try (FileInputStream fis = new FileInputStream(givenYaml)) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(fis);

            // Top level key: "templateEntries"
            Map<String, Object> templateEntries = (Map<String, Object>) data.get("templateEntries");

            if (templateEntries == null) {
                System.out.println("No 'templateEntries' found in config.");
                return;
            }

            // Initialize templates list
            templates = new ArrayList<>();

            // Iterate over each entry in templateEntries
            for (Map.Entry<String, Object> templateEntry : templateEntries.entrySet()) {
                Map<String, Object> templateMap = (Map<String, Object>) templateEntry.getValue();
                Template template = new Template();

                // Extract & save entry's name
                String name = (String) templateMap.get("name");
                template.setName(name);

                // Extract & save entry's attributes as ArrayList<String>
                String attrString = (String) templateMap.get("attributes");
                if (attrString != null && !attrString.trim().isEmpty()) {
                    String[] attrArray = attrString.split("\\s+");
                    ArrayList<String> attrList = new ArrayList<>(Arrays.asList(attrArray));
                    template.setAttributes(attrList);
                }

                // Extract & save entry's commands as DoublyLinkedList<String>
                List<String> commandsList = (List<String>) templateMap.get("commands");
                if (commandsList != null && !commandsList.isEmpty()) {
                    DoublyLinkedList<String> commandLinkedList = new DoublyLinkedList<>();
                    for (String cmd : commandsList) {
                        commandLinkedList.addLast(cmd);
                    }
                    template.setCommands(commandLinkedList);
                }

                // Add to template list
                templates.add(template);
            }

            System.out.println("Loaded " + templates.size() + " template(s) from config.");

        } catch (Exception e) {
            System.out.println("Error loading config in " + appDirectory + ": " + e.getMessage());
        }
    }

    public String getAppDirectory() {
        return appDirectory;
    }

    public List<Template> getTemplates() {
        return templates;
    }

    public Template getSelectedTemplate() {
        return selectedTemplate;
    }

    public void setTemplateByString(String templateString) {
        for (Template t : templates) {
            if (t.getName().equalsIgnoreCase(templateString)) {
                this.selectedTemplate = t;
                return;
            }
        }
        this.selectedTemplate = null;
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

    // Processes the input data
    public void processData() {
        if (selectedTemplate != null && selectedTemplate.getCommands()!=null) {
            DoublyLinkedList<String> dblLnkLst = selectedTemplate.getCommands();
            String newOutput = "";
            String[] inputLines = inputText.split("\\r?\\n"); // Handles both \n and \r\n
            for (String inputLine : inputLines) {
                newOutput += inputLine.toUpperCase();
                /* do {
                    // do command on from current dblLnkLst node's string
                    // traverse forwards in DoublyLinkedList<String> dblLnkLst 
                }
                while (next node isnt null>); */
            }
            setOutputText(newOutput);
        }
    }

    // Clear the input and output
    public void clearData() {
        this.inputText = "";
        this.outputText = "";
    }
}
