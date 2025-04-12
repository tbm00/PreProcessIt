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

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.data.Attribute;
import dev.tbm00.preprocessit.data.Component;
import dev.tbm00.preprocessit.data.Qualifier;
import dev.tbm00.preprocessit.data.enums.Action;
import dev.tbm00.preprocessit.data.enums.Condition;
import dev.tbm00.preprocessit.data.enums.Location;

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
                StaticUtil.log("Found config.yml in " + appDirectory);
                return configFile;
            }
        } catch (Exception e) {
            StaticUtil.log("Error finding config file in " + appDirectory + ": " + e.getMessage());
        }
        
        // 2nd: Create config file if not found or error
        // (by getting default config from /resources/config.yml and pasting in user directory)
        try {
            // (assuming resources are on the classpath, so we use getResourceAsStream)
            InputStream resourceStream = getClass().getResourceAsStream("/config.yml");
            if (resourceStream == null) {
                StaticUtil.log("Could not find default config.yml in program's resources");
                return null; 
            }

            // Create directory if it doesn't exist
            try {
                Files.createDirectories(Paths.get(appDirectory));
            } catch (Exception e) {
                StaticUtil.log("Error creating directory " + appDirectory + ": " + e.getMessage());
                return null;
            }

            // Copy file from resources to local config
            Files.copy(resourceStream, configPath, StandardCopyOption.REPLACE_EXISTING);
            resourceStream.close();
            StaticUtil.log("Created config.yml in " + appDirectory);
            return configFile;
        } catch (Exception e) {
            StaticUtil.log("Error creating config file in " + appDirectory + ": " + e.getMessage());
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public void loadConfig(File givenYaml) {
        if (givenYaml == null) {
            StaticUtil.log("No config.yml file provided to load");
            return;
        }
        
        try (FileInputStream fis = new FileInputStream(givenYaml)) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(fis);

            Map<String, Object> componentEntries = (Map<String, Object>) data.get("componentEntries");
            if (componentEntries != null) {
                // Unload old components
                model.clearComponents();

                int componentID = 0;
                componentLoop: // Iterate over each componentEntry configuration
                for (Map.Entry<String, Object> componentEntry : componentEntries.entrySet()) {
                    String componentName = componentEntry.getKey();
                    Map<String, Object> componentMap = (Map<String, Object>) componentEntry.getValue();

                    List<String> attributeOrder = (List<String>) componentMap.get("attributeOrder");
                    if (attributeOrder == null) {
                        StaticUtil.log("- Component Not Loaded: " + componentName + " (no attributeOrder found)");
                        continue componentLoop;
                    }

                    Map<String, Object> attributeEntries = (Map<String, Object>) componentMap.get("attributeEntries");
                    if (attributeEntries == null) {
                        StaticUtil.log("- Component Not Loaded: " + componentName + " (no attributeEntries found)");
                        continue componentLoop;
                    }
                    
                    ArrayList<Attribute> attributes = new ArrayList<>();

                    attributeLoop: // Iterate over each attributeEntry configuration
                    for (Map.Entry<String, Object> attributeEntry : attributeEntries.entrySet()) {
                        String attributeName = attributeEntry.getKey();
                        Map<String, Object> attributeMap = (Map<String, Object>) attributeEntry.getValue();
                        Object rawQualifierEntries = attributeMap.get("qualifierEntries");
                        ArrayList<Qualifier> qualifiers = new ArrayList<>();
                        
                        if (rawQualifierEntries != null && rawQualifierEntries instanceof Map) {
                            Map<String, Object> qualifierEntries = (Map<String, Object>) rawQualifierEntries;

                            qualifierLoop: // Iterate over each qualifier entry configuration (mapped with numeric keys)
                            for (Map.Entry<String, Object> entry : qualifierEntries.entrySet()) {
                                int qualifierID;
                                try {
                                    qualifierID = Integer.parseInt(entry.getKey());
                                } catch (NumberFormatException e) {
                                    StaticUtil.log("--- Qualifier Not Loaded: " + componentName + "'s " + attributeName + "'s " + entry.getKey() + " (invalid qualifier key, should be a number)");
                                    continue attributeLoop;
                                }

                                if (entry.getValue() instanceof Map) {
                                    Map<String, Object> qualMap = (Map<String, Object>) entry.getValue();
                                    
                                    // Process locations: convert into ENUMs
                                    String qualifierLocationStr = (String) qualMap.get("location");
                                    Location[] locations = null;
                                    if (qualifierLocationStr != null) {
                                        List<Location> locationList = new ArrayList<>();
                                        String[] locationStrings = qualifierLocationStr.split(",");

                                        // Iterate over each configured location
                                        for (String locStr : locationStrings) {
                                            try {
                                                locationList.add(Location.valueOf(locStr.trim().toUpperCase()));
                                                StaticUtil.log("---- Location Loaded: " + componentName + "'s " + attributeName + "'s " + locStr);
                                            } catch (IllegalArgumentException ex) {
                                                StaticUtil.log("---- Location Not Loaded: " + componentName + "'s " + attributeName + "'s " + locStr + " (no applicable enum)");
                                                continue qualifierLoop;
                                            }
                                        }
                                        locations = locationList.toArray(new Location[0]);
                                    } else {
                                        StaticUtil.log("---- Locations Not Loaded: " + componentName + "'s " + attributeName + " (no locations found)");
                                        continue qualifierLoop;
                                    }
                    
                                    // Process condition: convert into ENUM
                                    String qualifierConditionStr = (String) qualMap.get("condition");
                                    Condition condition = null;
                                    if (qualifierConditionStr != null) {
                                        try {
                                            condition = Condition.valueOf(qualifierConditionStr.trim().replace("-", "_").toUpperCase());
                                            StaticUtil.log("---- Condition Loaded: " + componentName + "'s " + attributeName + "'s " + qualifierConditionStr);
                                        } catch (IllegalArgumentException ex) {
                                            StaticUtil.log("---- Condition Not Loaded: " + componentName + "'s " + attributeName + "'s " + qualifierConditionStr + " (no applicable enum)");
                                            continue qualifierLoop;
                                        }
                                    } else {
                                        StaticUtil.log("---- Condition Not Loaded: " + componentName + "'s " + attributeName + " (no condition found)");
                                        continue qualifierLoop;
                                    }
                    
                                    // Process value: leave as String
                                    String qualifierValue = (String) qualMap.get("value");
                                    if (qualifierValue != null) {
                                        StaticUtil.log("---- Value Loaded: " + componentName + "'s " + attributeName + "'s " + qualifierValue);
                                    } else {
                                        StaticUtil.log("---- Value Not Loaded: " + componentName + "'s " + attributeName + " (no value found)");
                                        continue qualifierLoop;
                                    }
                    
                                    // Process qualifiedActions: convert into ENUMs
                                    List<String> qualifiedActionList = (List<String>) qualMap.get("qualifiedActions");
                                    List<Action> qualifiedActionsList = new ArrayList<>();
                                    if (qualifiedActionList != null) {
                                        // Iterate over each configured qualified action
                                        for (String actionStr : qualifiedActionList) {
                                            try {
                                                Action action = Action.valueOf(actionStr.trim().replace("-", "_").toUpperCase());
                                                qualifiedActionsList.add(action);
                                                StaticUtil.log("---- Qualified Action Loaded: " + componentName + "'s " + attributeName + "'s " + actionStr);
                                            } catch (IllegalArgumentException e) {
                                                StaticUtil.log("---- Qualified Action Not Loaded: " + componentName + "'s " + attributeName + "'s " + actionStr + " (no applicable enum)");
                                                continue qualifierLoop;
                                            }
                                        }
                                    } else {
                                        StaticUtil.log("---- Qualified Actions Not Loaded: " + componentName + "'s " + attributeName + " (no qualified actions found)");
                                        continue qualifierLoop;
                                    }
                                    Action[] qualifiedActions = qualifiedActionsList.toArray(new Action[0]);
                    
                                    // Process unqualifiedActions: convert into ENUMs
                                    List<String> unqualifiedActionList = (List<String>) qualMap.get("unqualifiedActions");
                                    List<Action> unqualifiedActionsList = new ArrayList<>();
                                    if (unqualifiedActionList != null) {
                                        // Iterate over each configured unqualified action
                                        for (String actionStr : unqualifiedActionList) {
                                            try {
                                                Action action = Action.valueOf(actionStr.trim().replace("-", "_").toUpperCase());
                                                unqualifiedActionsList.add(action);
                                                StaticUtil.log("---- Unqualified Action Loaded: " + componentName + "'s " + attributeName + "'s " + actionStr);
                                            } catch (IllegalArgumentException e) {
                                                StaticUtil.log("---- Unqualified Action Not Loaded: " + componentName + "'s " + attributeName + "'s " + actionStr + " (no applicable enum)");
                                                continue qualifierLoop;
                                            }
                                        }
                                    } else {
                                        StaticUtil.log("---- Unqualified Actions Not Loaded: " + componentName + "'s " + attributeName + " (no unqualified actions found)");
                                        continue qualifierLoop;
                                    }
                                    Action[] unqualifiedActions = unqualifiedActionsList.toArray(new Action[0]);
                    
                                    // Add the local qualifier into the local attribute
                                    Qualifier qualifier = new Qualifier(qualifierID, locations, condition, qualifierValue, qualifiedActions, unqualifiedActions);
                                    qualifiers.add(qualifier);
                                    StaticUtil.log("--- Qualifier Loaded: " + componentName + "'s " + attributeName + "'s " + entry.getKey());
                                } else {
                                    StaticUtil.log("--- Qualifier Not Loaded: " + componentName + "'s " + attributeName + "'s " + entry.getKey() + " (invalid qualifier entry format)");
                                }
                            }

                            // Add the local attribute into the local component
                            Attribute attribute = new Attribute(attributes.size(), attributeName, qualifiers);
                            attributes.add(attribute);
                            StaticUtil.log("-- Attribute Loaded: " + componentName + "'s " + attributeName);
                        } else {
                            StaticUtil.log("-- Attribute Not Loaded: " + componentName + "'s " + attributeName + " (no qualifiers found)");
                            continue componentLoop;
                        }
                    }
                    
                    // Add local component into model's components
                    Component component = new Component(componentID++, componentName, attributes, attributeOrder);
                    model.addComponent(component);
                    StaticUtil.log("- Component Loaded: " + componentName + " " + componentName + " " + attributeOrder);
                }
                StaticUtil.log("Loaded " + model.getComponents().size() + " componentEntries from config");
            } else {
                StaticUtil.log("No componentEntries found in config");
            }
        } catch (Exception e) {
            StaticUtil.log("Error loading config file in " + appDirectory + ": " + e.getMessage());
        }
    }

    public String getAppDirectory() {
        return appDirectory;
    }
}
