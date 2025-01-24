package dev.tbm00.preprocessit.model;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import dev.tbm00.preprocessit.datastructures.Component;
import dev.tbm00.preprocessit.datastructures.Attribute;
import dev.tbm00.preprocessit.datastructures.Qualifier;

public class ConfigHandler {
    private final Model model;
    private final String appDirectory;
    private final Path configPath;
    private File config;

    public ConfigHandler(Model model) {
        this.model = model;
        appDirectory = System.getProperty("user.home") + "/Documents/PreProcessIt";
        configPath = Paths.get(appDirectory, "config.yml");
        config = getConfigFile();
        loadConfig(config);
    }

    private File getConfigFile() {
        File configFile = configPath.toFile();

        // 1st: Find & return config file if already exists
        try {
            if (configFile.exists() && configFile.length()>3) {
                System.out.println("Found config.yml in " + appDirectory);
                return configFile;
            }
        } catch (Exception e) {
            System.out.println("Error finding config file in " + appDirectory + ": " + e.getMessage());
        }
        
        // 2nd: Create config file if not found or error
        // (by getting default config from /resources/config.yml and pasting in user directory)
        try {
            // (assuming resources are on the classpath, so we use getResourceAsStream)
            InputStream resourceStream = getClass().getResourceAsStream("/config.yml");
            if (resourceStream == null) {
                System.out.println("Could not find default config.yml in program's resources");
                return null; 
            }

            // Create directory if it doesn't exist
            try {
                Files.createDirectories(Paths.get(appDirectory));
            } catch (Exception e) {
                System.out.println("Error creating directory " + appDirectory + ": " + e.getMessage());
                return null;
            }

            // Copy file from resources to local config
            Files.copy(resourceStream, configPath, StandardCopyOption.REPLACE_EXISTING);
            resourceStream.close();
            System.out.println("Created config.yml in " + appDirectory);
            return configFile;
        } catch (Exception e) {
            System.out.println("Error creating config file in " + appDirectory + ": " + e.getMessage());
            return null;
        }
    }
    
    public void loadConfig(File givenYaml) {
        if (givenYaml == null) {
            System.out.println("No config.yml file provided to load");
            return;
        }
        
        try (FileInputStream fis = new FileInputStream(givenYaml)) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(fis);

            // Top level key: "componentEntries"
            Map<String, Object> componentEntries = (Map<String, Object>) data.get("componentEntries");
            if (componentEntries == null) {
                System.out.println("No 'componentEntries' found in config");
            } else { // Iterate over each componentEntry
                model.clearComponents();
                int componentID = 0;
                for (Map.Entry<String, Object> componentEntry : componentEntries.entrySet()) {
                    String componentName = componentEntry.getKey();
                    ArrayList<Attribute> attributes = new ArrayList<>();

                    Map<String, Object> componentMap = (Map<String, Object>) componentEntry.getValue();
                    Map<String, Object> attributeEntries = (Map<String, Object>) componentMap.get("attributeEntries");
                    if (attributeEntries == null) {
                        System.out.println("No 'attributeEntries' found in " + componentName + "'s config definition");
                    } else { // Iterate over each attributeEntry
                        for (Map.Entry<String, Object> attributeEntry : attributeEntries.entrySet()) {
                            String attributeName = attributeEntry.getKey();
                            ArrayList<Qualifier> qualifiers = new ArrayList<>();

                            Map<String, Object> attributeMap = (Map<String, Object>) attributeEntry.getValue();
                            Object rawQualifierEntries = attributeMap.get("qualifierEntries");
                            if (rawQualifierEntries == null) {
                                System.out.println("No 'qualifierEntries' found in " + componentName + "'s " + attributeName + "'s config definition");
                            } else if (rawQualifierEntries instanceof List) { // Iterate over each qualifierEntry
                                List<String> qualifierEntries = (List<String>) rawQualifierEntries;
                                int qualifierID = 0;
                                for (String qualifierEntry : qualifierEntries) {
                                    // qualifierEntry format is "LOCATION CONDITION VALUE"
                                    String[] parts = qualifierEntry.split(" ", 3);
                                    if (parts.length >= 3) {
                                        String qualifierLocation = parts[0];
                                        String qualifierCondition = parts[1];
                                        String qualifierValue = parts[2];
                                        Qualifier qualifier = new Qualifier(qualifierID++, qualifierLocation, qualifierCondition, qualifierValue);
                                        qualifiers.add(qualifier);
                                    } else {
                                        System.out.println("Invalid qualifier entry format: " + qualifierEntry);
                                    }
                                }
                            }

                            // Add local attribute into component's attributes
                            Attribute attribute = new Attribute(attributes.size(), attributeName, qualifiers);
                            attributes.add(attribute);
                        }
                    }

                    // Add local component into model's components
                    Component component = new Component(componentID++, componentName, attributes);
                    System.out.println("added Component: " + (componentID-1) + " " + componentName + " " + attributes);
                    model.addComponent(component);
                }
                System.out.println("Loaded " + model.getComponents().size() + " component(s) from config");
            }
        } catch (Exception e) {
            System.out.println("Error loading config in " + appDirectory + ": " + e.getMessage());
        }
    }

    public String getAppDirectory() {
        return appDirectory;
    }
}
