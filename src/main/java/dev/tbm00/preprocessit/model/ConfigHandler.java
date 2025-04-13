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
import java.util.Objects;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.Yaml;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.Component;
import dev.tbm00.preprocessit.model.data.Attribute;
import dev.tbm00.preprocessit.model.data.Qualifier;
import dev.tbm00.preprocessit.model.data.enums.Action;
import dev.tbm00.preprocessit.model.data.enums.ActionSpec;
import dev.tbm00.preprocessit.model.data.enums.Condition;
import dev.tbm00.preprocessit.model.data.enums.Word;

/**
 * Handles configuration file operations such as loading, validating, and creating configurations.
 */
public class ConfigHandler {
    private final Model model;
    private final String appDirectory;
    private final Path configPath;
    private final File config;

    /**
     * Constructs a new ConfigHandler instance.
     *
     * <p>This constructor initializes the configuration directory path using the current user's documents directory,
     * sets up the path for the configuration file, obtains the configuration file (or creates a default one if needed),
     * and loads the configuration into the provided model.</p>
     *
     * @param model The model instance into which configuration components will be loaded.
     */
    public ConfigHandler(Model model) {
        this.model = model;
        this.appDirectory = System.getProperty("user.home") + "/Documents/PreProcessIt";
        this.configPath = Paths.get(appDirectory, "config.yml");
        this.config = getConfigFile();
        loadConfig(config);
    }

    /**
     * Retrieves the configuration file.
     *
     * <p>This method verifies if the configuration file is valid; if it is,
     * the file is returned. Otherwise, it attempts to create a default configuration file using the program's resources.</p>
     *
     * @return The valid configuration file or a newly created default file, or {@code null} if creation fails.
     */
    private File getConfigFile() {
        File file = configPath.toFile();
        if (isValidConfigFile(file)) {
            log("Found config.yml in " + appDirectory);
            return file;
        }
        return createDefaultConfigFile(file);
    }
    
    /**
     * Checks if the specified file is a valid configuration file.
     *
     * <p>A valid configuration file must exist and have a length greater than 3 bytes.
     * Any exceptions encountered during validation are caught and logged.</p>
     *
     * @param file The file to validate.
     * @return {@code true} if the file exists and appears valid; {@code false} otherwise.
     */
    private boolean isValidConfigFile(File file) {
        try {
            return file.exists() && file.length() > 3;
        } catch (Exception e) {
            log("Error finding config file in " + appDirectory + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Creates a default configuration file by copying the default config from the program's resources.
     *
     * <p>If the default configuration resource cannot be found or an error occurs during creation,
     * the error is logged and {@code null} is returned.</p>
     *
     * @param file The file that represents the desired configuration file location.
     * @return The newly created configuration file or {@code null} if creation fails.
     */
    private File createDefaultConfigFile(File file) {
        try (InputStream resourceStream = getClass().getResourceAsStream("/config.yml")) {
            if (resourceStream == null) {
                log("Could not find default config.yml in program's resources");
                return null;
            }
            Files.createDirectories(Paths.get(appDirectory));
            Files.copy(resourceStream, configPath, StandardCopyOption.REPLACE_EXISTING);
            log("Created config.yml in " + appDirectory);
            return file;
        } catch (Exception e) {
            log("Error creating config file in " + appDirectory + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Loads the configuration from the provided YAML file into the model.
     *
     * <p>The method reads the YAML file into a Map, extracts component entries, clears any existing components from the model,
     * and iterates over each component entry to load its corresponding component. If the configuration file is {@code null}
     * or component entries are missing, it logs appropriate messages.</p>
     *
     * @param givenYaml The YAML configuration file to load.
     */
    @SuppressWarnings("unchecked")
    public void loadConfig(File givenYaml) {
        if (givenYaml == null) {
            log("No config.yml file provided to load");
            return;
        }
        
        try (FileInputStream fis = new FileInputStream(givenYaml)) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(fis);
            
            Map<String, Object> componentEntries = (Map<String, Object>) data.get("componentEntries");
            if (componentEntries == null) {
                log("No componentEntries found in config");
                return;
            }
            
            // Clear components before loading a new config
            model.clearComponents();
            int componentID = 0;
            
            // Iterate over each component entry
            for (Map.Entry<String, Object> componentEntry : componentEntries.entrySet()) {
                Component component = loadComponent(componentID, componentEntry);
                if (component != null) {
                    model.addComponent(component);
                    componentID++;
                    log("- - - - Component Loaded: " + component.getName() + " " + component.getAttributeOrder());
                }
            }
            log("Loaded " + model.getComponents().size() + " componentEntries from config");
        } catch (Exception e) {
            log("Error loading config file in " + appDirectory + ": " + e.getMessage());
        }
    }
    
    /**
     * Loads a component configuration entry from the YAML data.
     *
     * <p>This method extracts the component name, attribute order, and attribute entries.
     * If essential keys are missing (like "attributeOrder" or "attributeEntries"), the component is not loaded.</p>
     *
     * @param componentID   The identifier assigned to the component.
     * @param componentEntry A key-value pair representing the component entry from the YAML configuration.
     * @return A fully constructed Component instance, or {@code null} if required configuration sections are missing.
     */
    @SuppressWarnings("unchecked")
    private Component loadComponent(int componentID, Map.Entry<String, Object> componentEntry) {
        String componentName = componentEntry.getKey();
        Map<String, Object> componentMap = (Map<String, Object>) componentEntry.getValue();
        
        // Ensure essential keys exist
        List<String> attributeOrder = (List<String>) componentMap.get("attributeOrder");
        if (attributeOrder == null) {
            log("- - - - Component Not Loaded: " + componentName + " (no attributeOrder found)");
            return null;
        }
        Map<String, Object> attributeEntries = (Map<String, Object>) componentMap.get("attributeEntries");
        if (attributeEntries == null) {
            log("- - - - Component Not Loaded: " + componentName + " (no attributeEntries found)");
            return null;
        }
        
        ArrayList<Attribute> attributes = new ArrayList<>();
        // Process each attribute entry.
        for (Map.Entry<String, Object> attributeEntry : attributeEntries.entrySet()) {
            try {
                Attribute attr = loadAttribute(componentName, attributeEntry, attributes.size());
                if (attr != null) {
                    attributes.add(attr);
                    log("- - - Attribute Loaded: " + componentName + "'s " + attr.getName());
                }
            } catch (FatalComponentException fce) {
                // A fatal error in this attribute causes us to skip the entire component.
                log(fce.getMessage());
                return null;
            }
        }
        
        return new Component(componentID, componentName, attributes, attributeOrder);
    }
    
    /**
     * Loads an attribute for a specific component from the YAML configuration.
     *
     * <p>This method extracts the attribute name and its qualifier information. If no qualifier data is provided,
     * a {@link FatalComponentException} is thrown to indicate that the component should be skipped.
     * It returns a constructed Attribute instance if successful.</p>
     *
     * @param componentName  The name of the component to which the attribute belongs.
     * @param attributeEntry The key-value pair representing the attribute entry from the YAML configuration.
     * @param attributeIndex The index position of the attribute within its component.
     * @return The constructed Attribute instance, or {@code null} if attribute loading fails.
     * @throws FatalComponentException if a fatal error is encountered during attribute processing.
     */
    @SuppressWarnings("unchecked")
    private Attribute loadAttribute(String componentName, Map.Entry<String, Object> attributeEntry, int attributeIndex)
            throws FatalComponentException {
        String attributeName = attributeEntry.getKey();
        Object rawQualifiers = attributeEntry.getValue();
        
        // If no qualifier information is provided, treat as fatal for this component
        if (!(rawQualifiers instanceof Map)) {
            throw new FatalComponentException("- - - - Attribute Not Loaded: " + componentName + "'s " 
                    + attributeName + " (no qualifiers found)");
        }
        
        Map<String, Object> qualifierEntries = (Map<String, Object>) rawQualifiers;
        List<Integer> sortedKeys = qualifierEntries.keySet().stream()
                .map(key -> {
                    try {
                        return Integer.valueOf(key);
                    } catch (NumberFormatException nfe) {
                        log("- - Qualifier key not numeric: " + key + ", skipping this qualifier.");
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
                
        ArrayList<Qualifier> qualifiers = new ArrayList<>();
        
        for (Integer key : sortedKeys) {
            Map<String, Object> qualMap = (Map<String, Object>) qualifierEntries.get(key.toString());
            if (qualMap != null) {
                // If loading the qualifier fails, skip this attribute entirely
                Qualifier qualifier = loadQualifier(componentName, attributeName, key, qualMap);
                if (qualifier == null) {
                    log("- Skipping attribute " + attributeName + " due to qualifier errors.");
                    return null;
                }
                qualifiers.add(qualifier);
                log("- - Qualifier Loaded: " + componentName + "'s " + attributeName + "'s " + key.toString());
            } else {
                log("- - Qualifier Not Loaded: " + componentName + "'s " + attributeName 
                        + "'s " + key.toString() + " (invalid qualifier entry format)");
            }
        }
        return new Attribute(attributeIndex, attributeName, qualifiers);
    }
    
    /**
     * Loads a qualifier for a specified attribute within a component from its configuration map.
     *
     * <p>This method processes the qualifier details including "word", "condition", "value",
     * and lists of qualified and unqualified actions. If any required information is missing or invalid,
     * it logs the error and returns {@code null}.</p>
     *
     * @param componentName The name of the component.
     * @param attributeName The name of the attribute for which the qualifier is being loaded.
     * @param qualifierKey  The key identifier for the qualifier.
     * @param qualMap       A map representing the qualifier's configuration.
     * @return A constructed Qualifier object, or {@code null} if required data is missing or invalid.
     */
    @SuppressWarnings("unchecked")
    private Qualifier loadQualifier(String componentName, String attributeName, Integer qualifierKey, 
                                    Map<String, Object> qualMap) {
        // Process "word"
        String qualifierWordStr = (String) qualMap.get("word");
        if (qualifierWordStr == null) {
            log("- Words Not Loaded: " + componentName + "'s " + attributeName + " (no words found)");
            return null;
        }
        Word word;
        try {
            word = Word.valueOf(qualifierWordStr.trim().replace("-", "_").toUpperCase());
            log("- Word Loaded: " + componentName + "'s " + attributeName + "'s " + qualifierWordStr);
        } catch (IllegalArgumentException ex) {
            log("- Word Not Loaded: " + componentName + "'s " + attributeName + "'s " + qualifierWordStr + " (no applicable ENUM)");
            return null;
        }
        
        // Process "condition"
        String qualifierConditionStr = (String) qualMap.get("condition");
        if (qualifierConditionStr == null) {
            log("- Condition Not Loaded: " + componentName + "'s " + attributeName + " (no condition found)");
            return null;
        }
        Condition condition;
        try {
            condition = Condition.valueOf(qualifierConditionStr.trim().replace("-", "_").toUpperCase());
            log("- Condition Loaded: " + componentName + "'s " + attributeName + "'s " + qualifierConditionStr);
        } catch (IllegalArgumentException ex) {
            log("- Condition Not Loaded: " + componentName + "'s " + attributeName + "'s " + qualifierConditionStr + " (no applicable ENUM)");
            return null;
        }
        
        // Process "value"
        String qualifierValue = (String) qualMap.get("value");
        if (qualifierValue == null) {
            log("- Value Not Loaded: " + componentName + "'s " + attributeName + " (no value found)");
            return null;
        }
        log("- Value Loaded: " + componentName + "'s " + attributeName + "'s " + qualifierValue);
        
        // Process "qualifiedActions"
        List<String> qualifiedActionList = (List<String>) qualMap.get("qualifiedActions");
        if (qualifiedActionList == null) {
            log("- Qualified Actions Not Loaded: " + componentName + "'s " + attributeName + " (no qualified actions found)");
            return null;
        }
        List<ActionSpec> qualifiedActionsList = new ArrayList<>();
        for (String actionStr : qualifiedActionList) {
            ActionSpec spec = parseAction(actionStr);
            if (spec != null) {
                qualifiedActionsList.add(spec);
                log("- Qualified Action Loaded: " + spec);
            } else {
                log("- Qualified Action Not Loaded: " + actionStr + " for qualifier key: " + qualifierKey);
                return null;
            }
        }
        ActionSpec[] qualifiedActions = qualifiedActionsList.toArray(new ActionSpec[0]);
        
        // Process "unqualifiedActions"
        List<String> unqualifiedActionList = (List<String>) qualMap.get("unqualifiedActions");
        if (unqualifiedActionList == null) {
            log("- Unqualified Actions Not Loaded: " + componentName + "'s " + attributeName + " (no unqualified actions found)");
            return null;
        }
        List<ActionSpec> unqualifiedActionsList = new ArrayList<>();
        for (String actionStr : unqualifiedActionList) {
            ActionSpec spec = parseAction(actionStr);
            if (spec != null) {
                unqualifiedActionsList.add(spec);
                log("- Unqualified Action Loaded: " + spec);
            } else {
                log("- Unqualified Action Not Loaded: " + actionStr + " for qualifier key: " + qualifierKey);
                return null;
            }
        }
        ActionSpec[] unqualifiedActions = unqualifiedActionsList.toArray(new ActionSpec[0]);
        
        return new Qualifier(qualifierKey, word, condition, qualifierValue, qualifiedActions, unqualifiedActions);
    }

    /**
     * Parses an action specification string to create an ActionSpec instance.
     *
     * <p>The method handles action strings with or without parameters. Action names are transformed to uppercase and
     * formatted appropriately before matching them to their corresponding enum constants. If parsing fails, it logs
     * an error and returns {@code null}.</p>
     *
     * @param actionStr The action specification string (e.g., "ACTION_NAME(param)").
     * @return An {@code ActionSpec} representing the parsed action, or {@code null} if parsing fails.
     */
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
                log(" Invalid Action Enum: " + actionStr);
                return null;
            }
        } else {
            try {
                Action action = Action.valueOf(actionStr.replace("-", "_").toUpperCase());
                return new ActionSpec(action, "1");
            } catch (IllegalArgumentException e) {
                log(" Invalid Action Enum: " + actionStr);
                return null;
            }
        }
    }
    
    /**
     * Returns the application directory used for storing configuration files.
     *
     * @return The absolute path of the application's directory.
     */
    public String getAppDirectory() {
        return appDirectory;
    }
    
    /**
     * Logs a message via the static logging utility.
     *
     * @param message The message to log.
     */
    private void log(String message) {
        StaticUtil.log(message);
    }
    
    /**
     * Exception type used to indicate a fatal error when loading a component (such as a misformed attribute block)
     * which should lead to skipping the entire component.
     */
    private static class FatalComponentException extends Exception {
        /**
         * Constructs a new FatalComponentException with the specified detail message.
         *
         * @param message The detail message explaining the exception.
         */
        public FatalComponentException(String message) {
            super(message);
        }
    }
}
