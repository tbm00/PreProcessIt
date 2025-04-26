package dev.tbm00.preprocessit;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import dev.tbm00.preprocessit.controller.Controller;
import dev.tbm00.preprocessit.model.Model;
import dev.tbm00.preprocessit.model.data.Component;
import dev.tbm00.preprocessit.view.View;

public class PreProcessIt {

    /**
     * Entry point for the PreProcessIt application. Determines whether to run in
     * headless (CLI) mode or GUI mode based on the environment (headless JVM) or
     * presence of the {@code --input} flag. In CLI mode, delegates to
     * {@link #runHeadless(String[]) runHeadless}; otherwise, initializes the
     * Swing-based GUI.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        boolean cliMode = GraphicsEnvironment.isHeadless() || Arrays.asList(args).contains("--input");
        // ----- CLI mode -----
        if (cliMode) {
            try {
                runHeadless(args);
            } catch (IOException e) {e.printStackTrace();}
            return;
        }

        // ----- GUI mode -----
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            StaticUtil.log("Error setting look and feel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            Model model = new Model();
            View view = new View();
            new Controller(model, view);
            
            // Show the view
            view.setVisible(true);
        });
    }

    /**
     * Runs the application in pure-CLI (headless) mode. Parses the provided
     * arguments for configuration, component selection, input and output file
     * paths. Loads the YAML configuration, optionally filters for the requested
     * component (or defaults to the first one), reads the input file, processes
     * the data, and writes the result to the specified output file.
     *
     * @param args command-line arguments. Supported flags:
     *             <ul>
     *               <li>{@code --config <path>}: path to YAML config file</li>
     *               <li>{@code --component <name>}: (optional) name of the component to select</li>
     *               <li>{@code --input <path>}: path to the input text file (triggers CLI mode)</li>
     *               <li>{@code --output <path>}: path to write the output CSV</li>
     *               <li>{@code --log}: (optional) output log to terminal</li>
     *             </ul>
     * @throws IOException if reading the input file or writing the output file fails.
     */
    private static void runHeadless(String[] args) throws IOException {
        Path configPath = null, inputPath = null, outputPath = null;
        String requestedComponent = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--config":  configPath = Paths.get(args[++i]); break;
                case "--input":   inputPath  = Paths.get(args[++i]); break;
                case "--output":  outputPath = Paths.get(args[++i]); break;
                case "--component": requestedComponent = args[++i]; break;
                case "--log": StaticUtil.enableConsoleLogging(); System.out.println("log enabled");break;
            }
        }
        if (configPath == null || inputPath == null || outputPath == null) {
            System.err.println(
                "Usage: java -jar PreProcessIt-0.1.9.1-beta.jar --config <config.yml> [--component <name>] --input <input.txt> --output <output.csv> [--log]"
            );
            System.exit(1);
        }

        // load config
        Model model = new Model();
        model.getConfigHandler().loadConfig(configPath.toFile());

        // pick component
        List<Component> comps = model.getComponents();
        if (comps == null || comps.isEmpty()) {
            System.err.println("Error: No components defined in your config.yml!");
            System.exit(2);
        }

        final String reqComp = requestedComponent;
        if (requestedComponent != null) {
            Optional<Component> match = comps.stream()
                .filter(c -> c.getName().equalsIgnoreCase(reqComp))
                .findFirst();
            if (!match.isPresent()) {
                System.err.printf("Error: Component ‘%s’ not found in config. Available:%n", requestedComponent);
                comps.forEach(c -> System.err.println("   • " + c.getName()));
                System.exit(3);
            }
            model.setSelectedComponent(match.get().getName());
        } else {
            model.setSelectedComponent(comps.get(0).getName());
        }

        // read input file
        byte[] inBytes = Files.readAllBytes(inputPath);
        String input = new String(inBytes, StandardCharsets.UTF_8);
        model.setInputText(input);

        // process
        String result = model.processData();

        // write output file
        byte[] outBytes = result.getBytes(StandardCharsets.UTF_8);
        Files.write(outputPath, outBytes);
    }
}