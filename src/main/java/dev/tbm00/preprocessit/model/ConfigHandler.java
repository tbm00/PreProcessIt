package dev.tbm00.preprocessit.model;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import org.yaml.snakeyaml.error.YAMLException;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.data.Component;
import dev.tbm00.preprocessit.model.data.LineRule;
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
    private final Path appDirectory;
    private final Path configPath;
    private final File config;

    private boolean poolingEnabled;
    private int configuredPoolSize;

    /**
     * Constructs a new ConfigHandler instance.
     *
     * <p>This constructor initializes the configuration directory path based on the operating system.
     * For Windows and macOS, it uses the user's Documents folder. For Linux or other Unix-like systems,
     * it checks the XDG_CONFIG_HOME variable and falls back to the user's .config directory if needed.
     * It then sets up the path for the configuration file, obtains the configuration file
     * (or creates a default one if needed), and loads the configuration into the provided model.</p>
     *
     * @param model The model instance into which configuration components will be loaded.
     */
    public ConfigHandler(Model model) {
        this.model = model;

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win") || os.contains("mac")) {
            this.appDirectory = Paths.get(System.getProperty("user.home"), "Documents", "PreProcessIt");
        } else {
            String xdgConfig = System.getenv("XDG_CONFIG_HOME");
            if (xdgConfig != null && !xdgConfig.isEmpty()) {
                this.appDirectory = Paths.get(xdgConfig, "PreProcessIt");
            } else {
                this.appDirectory = Paths.get(System.getProperty("user.home"), ".config", "PreProcessIt");
            }
        }

        this.configPath = appDirectory.resolve("config.yml");
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
            Files.createDirectories(appDirectory);
            Files.copy(resourceStream, configPath, StandardCopyOption.REPLACE_EXISTING);
            log("Created config.yml in " + appDirectory);
            return file;
        } catch (Exception e) {
            log("Error creating config file in " + appDirectory + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Loads the application configuration from the specified YAML file into the model.
     *
     * <p>This method reads and parses the given YAML configuration file, verifies that the 
     * "components" section exists and is valid, and initializes the concurrent‑threading options.</p>
     *
     * @param givenYaml the configuration file to load; if {@code null}, the method returns immediately
     * @throws FileNotFoundException if the config file does not exist at the expected location
     * @throws IOException           if an I/O error occurs while opening or reading the file
     * @throws YAMLException         if the file contains malformed YAML
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

            if (!(data.get(StaticUtil.KEY_COMPONENTS) instanceof Map)) {
                log("Missing or invalid 'components' section; aborting load.");
                return;
            }

            // Load concurrent threading toggle
            Object poolObj = data.get(StaticUtil.KEY_CONCURRENT_THREADING);
            poolingEnabled = Boolean.TRUE.equals(poolObj);
            log(" ");
            log("Concurrent threading " + (poolingEnabled ? "enabled" : "disabled") + " in config");

            // Load thread pool size override
            Object overrideObj = data.get(StaticUtil.KEY_CONCURRENT_OVERRIDE);
            if (poolingEnabled && overrideObj != null) {
                try {
                    configuredPoolSize = Math.max(1,
                        Integer.parseInt(overrideObj.toString())
                    );
                    log("Loaded thread pool size override: " + configuredPoolSize);
                } catch (NumberFormatException ex) {
                    log("Invalid threadPoolSizeOverride, defaulting to 1");
                    configuredPoolSize = 1;
                }
            } else {
                configuredPoolSize = 1;
            } log(" ");

            Map<String, Object> componentMap = (Map<String, Object>) data.get(StaticUtil.KEY_COMPONENTS);
            if (componentMap == null) {
                log("No components found in config");
                return;
            } else {log("Loading component(s)...\n");}
            
            // Clear components before loading a new config
            model.clearComponents();
            int componentID = 0;
            
            // Iterate over each component entry
            int i = 1;
            for (Map.Entry<String, Object> componentEntry : componentMap.entrySet()) {
                log("[component: " + i +"]");
                Component component = loadComponent(componentID, componentEntry);
                if (component != null) {
                    model.addComponent(component);
                    componentID++;
                    log("- - - - Component Loaded: " + component.getName() + " " + component.getAttributeOrder() + "\n");
                }
                i++;
            }
            log("Loaded " + model.getComponents().size() + " component(s) from config");
        }  catch (FileNotFoundException e) {
            log("Config file not found in " + appDirectory + ": " + e.getMessage());
        } catch (IOException e) {
            log("I/O error reading config file in " + appDirectory + ": " + e.getMessage());
        } catch (YAMLException e) {
            log("YAML parsing error in " + appDirectory + "'s config.yml': " + e.getMessage());
        }
    }
    
    /**
     * Loads a component configuration entry from the YAML data.
     *
     * <p>This method extracts the component name, attribute order, and attribute entries.
     * If essential keys are missing (like "attributeOutputOrder" or "attributes"), the component is not loaded.</p>
     *
     * @param componentID   The identifier assigned to the component.
     * @param componentEntry A key-value pair representing the component entry from the YAML configuration.
     * @return A fully constructed Component instance, or {@code null} if required configuration sections are missing.
     */
    @SuppressWarnings("unchecked")
    private Component loadComponent(int componentID, Map.Entry<String, Object> componentEntry) {
        String componentName = componentEntry.getKey();
        Map<String, Object> componentMap = (Map<String, Object>) componentEntry.getValue();

        // Load inputLineRules
        LineRule inputLineRule = null;
        if (componentMap.get(StaticUtil.KEY_INPUT_LINE_RULES)!=null) {
            try {
                Map<String, Object> inputLineRuleMap = (Map<String, Object>) componentMap.get(StaticUtil.KEY_INPUT_LINE_RULES);
                log("Loading inputLineRules...");
                inputLineRule = new LineRule(componentID, null);
                if (inputLineRuleMap != null) {
                    ArrayList<Qualifier> lineRuleQualifiers = new ArrayList<>();
        
                    for (Map.Entry<String, Object> qualifierEntry : inputLineRuleMap.entrySet()) {
                        String key = qualifierEntry.getKey();
                        int qualifierIndex;
                        try {
                            qualifierIndex = Integer.parseInt(key);
                        } catch (NumberFormatException e) {
                            log("- - Invalid inputLineRules key for component " + componentName + ": " + key);
                            return null;
                        }
        
                        try {
                            Map<String, Object> qualMap = (Map<String, Object>) qualifierEntry.getValue();
                            Qualifier qualifier = loadQualifier(componentName, null, qualifierIndex, qualMap);
                            if (qualifier != null) {
                                lineRuleQualifiers.add(qualifier);
                                log("- - Input Line Rule Loaded: " + componentName + "'s " + qualifierEntry.getKey());
                            }
                        } catch (Exception e) {
                            log("– - Error in inputLineRules[" + key + "] for " + componentName + ": " + e.getMessage());
                            continue;
                        }
                    }
                    inputLineRule.setQualifiers(lineRuleQualifiers);
                }
            } catch (Exception e) {}
        }

        // Load outputLineRules
        LineRule outputLineRule = null;
        if (componentMap.get(StaticUtil.KEY_OUTPUT_LINE_RULES)!=null) {
            try {
                Map<String, Object> outputLineRuleMap = (Map<String, Object>) componentMap.get(StaticUtil.KEY_OUTPUT_LINE_RULES);
                log("Loading outputLineRules...");
                outputLineRule = new LineRule(componentID, null);
                if (outputLineRuleMap != null) {
                    ArrayList<Qualifier> lineRuleQualifiers = new ArrayList<>();
        
                    for (Map.Entry<String, Object> qualifierEntry : outputLineRuleMap.entrySet()) {
                        String key = qualifierEntry.getKey();
                        int qualifierIndex;
                        try {
                            qualifierIndex = Integer.parseInt(key);
                        } catch (NumberFormatException e) {
                            log("- - Invalid outputLineRules key for component " + componentName + ": " + key);
                            return null;
                        }
        
                        try {
                            Map<String, Object> qualMap = (Map<String, Object>) qualifierEntry.getValue();
                            Qualifier qualifier = loadQualifier(componentName, null, qualifierIndex, qualMap);
                            if (qualifier != null) {
                                lineRuleQualifiers.add(qualifier);
                                log("- - Output Line Rule Loaded: " + componentName + "'s " + qualifierEntry.getKey());
                            }
                        } catch (Exception e) {
                            log("– - Error in outputLineRules[" + key + "] for " + componentName + ": " + e.getMessage());
                            continue;
                        }
                    }
                    outputLineRule.setQualifiers(lineRuleQualifiers);
                }
            } catch (Exception e) {}
        }

        // Load appendLeftovers
        log("Loading appendLeftovers...");
        Object leftoverObj = componentMap.get(StaticUtil.KEY_APPEND_LEFTOVERS);
        boolean appendLeftovers = Boolean.TRUE.equals(leftoverObj);
        log("- Appending Leftovers: " + (appendLeftovers ? "enabled" : "disabled"));

        // Load attributeOutputOrder
        log("Loading attributeOutputOrder...");
        List<String> attributeOutputOrder = (List<String>) componentMap.get(StaticUtil.KEY_ATTRIBUTE_OUTPUT_ORDER);
        if (attributeOutputOrder == null) {
            log("- Component Not Loaded: " + componentName + " (no attributeOutputOrder found)");
            return null;
        } else {
            log("- Attribute Output Order Loaded: " + attributeOutputOrder.toString());
        }

        // Load attributeOutputDelimiter
        log("Loading attributeOutputDelimiter...");
        String attributeOutputDelimiter = (String) componentMap.get(StaticUtil.KEY_ATTRIBUTE_OUTPUT_DELIMITER);
        if (attributeOutputDelimiter == null) {
            log("- Component Not Loaded: " + componentName + " (no attributeOutputDelimiter found)");
            return null;
        } else {
            log("- Attribute Output Delimiter Loaded: " + attributeOutputDelimiter);
        }
        
        // Load each attribute
        log("Loading attributes...");
        Map<String, Object> attributeMap = (Map<String, Object>) componentMap.get(StaticUtil.KEY_ATTRIBUTES);
        if (attributeMap == null) {
            log("- Component Not Loaded: " + componentName + " (no attributes found)");
            return null;
        }
        ArrayList<Attribute> attributes = new ArrayList<>();
        int i = 1;
        for (Map.Entry<String, Object> attributeEntry : attributeMap.entrySet()) {
            log("[" + componentName + " attribute: "+ i +"]");
            try {
                Attribute attr = loadAttribute(componentName, attributeEntry, attributes.size());
                if (attr != null) {
                    attributes.add(attr);
                    log("- - - Attribute Loaded: " + componentName + "'s " + attr.getName());
                }
            } catch (FatalComponentException fce) {
                log("Fatal error loading attributes for component " + componentName + "!");
                log(fce.getMessage());
                return null;
            }
            i++;
        }
        
        return new Component(componentID, componentName, attributes, attributeOutputOrder, attributeOutputDelimiter, inputLineRule, outputLineRule, appendLeftovers);
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
        
        Map<String, Object> qualiferMap = (Map<String, Object>) rawQualifiers;
        List<Integer> sortedKeys = qualiferMap.keySet().stream()
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
        
        int i = 1;
        for (Integer key : sortedKeys) {
            log("[" + componentName + "'s " + attributeName+ "'s qualifier: "+ i +"]");
            Map<String, Object> qualMap = (Map<String, Object>) qualiferMap.get(key.toString());
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
            i++;
        }
        return new Attribute(attributeIndex, attributeName, qualifiers);
    }

    /**
     * Loads a qualifier for a specified component or input line rule from its configuration map.
     *
     * <p>This method processes the qualifier details including "word", "condition", "value",
     * and lists of qualified and unqualified actions. If any required information is missing or invalid,
     * it logs the error and returns {@code null}.</p>
     *
     * @param componentName The name of the component.
     * @param attributeName The name of the attribute for which the qualifier is being loaded (can be null for input line rule).
     * @param qualifierKey  The key identifier for the qualifier.
     * @param qualMap       A map representing the qualifier's configuration.
     * @return A constructed Qualifier object, or {@code null} if required data is missing or invalid.
     */
    @SuppressWarnings("unchecked")
    private Qualifier loadQualifier(String componentName, String attributeName, Integer qualifierKey, Map<String, Object> qualMap) {
        boolean isLineRule = (attributeName == null);

        // Process "word"
        String qualifierWordStr = (String) qualMap.get(StaticUtil.KEY_WORD);
        if (qualifierWordStr == null) {
            log("- Word Not Loaded: " + componentName + (isLineRule ? "" : "'s " + attributeName) + " (no words found)");
            return null;
        }
        Word word;
        try {
            word = Word.valueOf(qualifierWordStr.trim().replace("-", "_").toUpperCase());
            log("- Word Loaded: " + componentName + (isLineRule ? "" : "'s " + attributeName) + "'s " + qualifierWordStr);
        } catch (IllegalArgumentException ex) {
            log("- Word Not Loaded: " + componentName + (isLineRule ? "" : "'s " + attributeName) + "'s " + qualifierWordStr + " (no applicable ENUM)");
            return null;
        }

        // Process "condition"
        String qualifierConditionStr = (String) qualMap.get(StaticUtil.KEY_CONDITION);
        if (qualifierConditionStr == null) {
            log("- Condition Not Loaded: " + componentName + (isLineRule ? "" : "'s " + attributeName) + " (no condition found)");
            return null;
        }
        Condition condition;
        try {
            condition = Condition.valueOf(qualifierConditionStr.trim().replace("-", "_").toUpperCase());
            log("- Condition Loaded: " + componentName + (isLineRule ? "" : "'s " + attributeName) + "'s " + qualifierConditionStr);
        } catch (IllegalArgumentException ex) {
            log("- Condition Not Loaded: " + componentName + (isLineRule ? "" : "'s " + attributeName) + "'s " + qualifierConditionStr + " (no applicable ENUM)");
            return null;
        }

        // Process "value"
        String qualifierValue = (String) qualMap.get(StaticUtil.KEY_VALUE);
        if (qualifierValue == null) {
            log("- Value Not Loaded: " + componentName + (isLineRule ? "" : "'s " + attributeName) + " (no value found)");
            return null;
        }
        log("- Value Loaded: " + componentName + (isLineRule ? "" : "'s " + attributeName) + "'s " + qualifierValue);

        // Process "qualifiedActions"
        List<String> qualifiedActionList = (List<String>) qualMap.get(StaticUtil.KEY_QUALIFIED_ACTIONS);
        if (qualifiedActionList == null) {
            log("- Qualified Actions Not Loaded: " + componentName + (isLineRule ? "" : "'s " + attributeName) + " (no qualified actions found)");
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
        List<String> unqualifiedActionList = (List<String>) qualMap.get(StaticUtil.KEY_UNQUALIFIED_ACTIONS);
        if (unqualifiedActionList == null) {
            log("- Unqualified Actions Not Loaded: " + componentName + (isLineRule ? "" : "'s " + attributeName) + " (no unqualified actions found)");
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
    public Path getAppDirectory() {
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

    /**
     * Getter for the config's thread pool size.
     *
     * @return The {@code configuredPoolSize}.
     */
    public int getConfiguredPoolSize() {
        return configuredPoolSize;
    }

    /**
     * Getter for the config's concurrent threading toggle.
     *
     * @return The {@code poolingEnabled} toggle.
     */
    public boolean getPoolingEnabled() {
        return poolingEnabled;
    }
}
