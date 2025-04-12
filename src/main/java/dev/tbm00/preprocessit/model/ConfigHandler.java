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
import dev.tbm00.preprocessit.model.data.Attribute;
import dev.tbm00.preprocessit.model.data.Component;
import dev.tbm00.preprocessit.model.data.Qualifier;
import dev.tbm00.preprocessit.model.data.enums.Action;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;
import dev.tbm00.preprocessit.model.data.enums.Condition;
import dev.tbm00.preprocessit.model.data.enums.Word;

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
                                    
                                    // Process word: convert into ENUM
                                    String qualifierWordStr = (String) qualMap.get("word");
                                    Word word = null;
                                    if (qualifierWordStr != null) {
                                        try {
                                            word = Word.valueOf(qualifierWordStr.trim().replace("-", "_").toUpperCase());
                                            StaticUtil.log("---- Word Loaded: " + componentName + "'s " + attributeName + "'s " + qualifierWordStr);
                                        } catch (IllegalArgumentException ex) {
                                            StaticUtil.log("---- Word Not Loaded: " + componentName + "'s " + attributeName + "'s " + qualifierWordStr + " (no applicable ENUM)");
                                            continue qualifierLoop;
                                        }
                                    } else {
                                        StaticUtil.log("---- Words Not Loaded: " + componentName + "'s " + attributeName + " (no words found)");
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
                                            StaticUtil.log("---- Condition Not Loaded: " + componentName + "'s " + attributeName + "'s " + qualifierConditionStr + " (no applicable ENUM)");
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
                                    List<ActionSpec> qualifiedActionsList = new ArrayList<>();
                                    if (qualifiedActionList != null) {
                                        // Iterate over each configured qualified action
                                        for (String actionStr : qualifiedActionList) {
                                            ActionSpec spec = parseAction(actionStr);
                                            if (spec != null) {
                                                qualifiedActionsList.add(spec);
                                                StaticUtil.log("---- Qualified Action Loaded: " + spec);
                                            } else {
                                                StaticUtil.log("---- Qualified Action Not Loaded: " + actionStr + " for qualifier key: " + entry.getKey());
                                                continue qualifierLoop;
                                            }
                                        }
                                    } else {
                                        StaticUtil.log("---- Qualified Actions Not Loaded: " + componentName + "'s " + attributeName + " (no qualified actions found)");
                                        continue qualifierLoop;
                                    }
                                    ActionSpec[] qualifiedActions = qualifiedActionsList.toArray(new ActionSpec[0]);
                    
                                    // Process unqualifiedActions: convert into ENUMs
                                    List<String> unqualifiedActionList = (List<String>) qualMap.get("unqualifiedActions");
                                    List<ActionSpec> unqualifiedActionsList = new ArrayList<>();
                                    if (unqualifiedActionList != null) {
                                        // Iterate over each configured unqualified action
                                        for (String actionStr : unqualifiedActionList) {
                                            ActionSpec spec = parseAction(actionStr);
                                            if (spec != null) {
                                                unqualifiedActionsList.add(spec);
                                                StaticUtil.log("---- Unqualified Action Loaded: " + spec);
                                            } else {
                                                StaticUtil.log("---- Unqualified Action Not Loaded: " + actionStr + " for qualifier key: " + entry.getKey());
                                                continue qualifierLoop;
                                            }
                                        }
                                    } else {
                                        StaticUtil.log("---- Unqualified Actions Not Loaded: " + componentName + "'s " + attributeName + " (no unqualified actions found)");
                                        continue qualifierLoop;
                                    }
                                    ActionSpec[] unqualifiedActions = unqualifiedActionsList.toArray(new ActionSpec[0]);
                    
                                    // Add the local qualifier into the local attribute
                                    Qualifier qualifier = new Qualifier(qualifierID, word, condition, qualifierValue, qualifiedActions, unqualifiedActions);
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

    private ActionSpec parseAction(String actionStr) {
        actionStr = actionStr.trim();

        if (actionStr.contains("(") && actionStr.endsWith(")")) {
            int startIndex = actionStr.indexOf('(');
            String actionName = actionStr.substring(0, startIndex).trim().replace("-", "_").toUpperCase();
            String param = actionStr.substring(startIndex + 1, actionStr.length() - 1).trim();

            if ((param.startsWith("\"") && param.endsWith("\"")) || (param.startsWith("'") && param.endsWith("'"))) {
                param = param.substring(1, param.length() - 1);
            }

            try {
                Action action = Action.valueOf(actionName);
                return new ActionSpec(action, param);
            } catch (IllegalArgumentException e) {
                StaticUtil.log("----- Invalid Action Enum: " + actionStr);
                return null;
            }
        } else {
            try {
                Action action = Action.valueOf(actionStr.replace("-", "_").toUpperCase());
                return new ActionSpec(action, null);
            } catch (IllegalArgumentException e) {
                StaticUtil.log("----- Invalid Action Enum: " + actionStr);
                return null;
            }
        }
    }

    public String getAppDirectory() {
        return appDirectory;
    }
}
