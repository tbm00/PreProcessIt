package dev.tbm00.preprocessit.controller;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileFilter;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.datatransfer.*;
import java.awt.Desktop;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import dev.tbm00.preprocessit.StaticUtil;
import dev.tbm00.preprocessit.model.Model;
import dev.tbm00.preprocessit.model.data.Component;
import dev.tbm00.preprocessit.view.View;

/**
 * Controller handles interaction:
 * - updates the Model based on user input
 * - updates the View based on changes in the Model
 */
public class Controller {
    private Model model;
    private View view;
    private static final String README_URL = "https://github.com/tbm00/PreProcessit";

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        updateDropdown();

        String componentName = (String) view.getComponentSelector().getSelectedItem();
        model.setSelectedComponent(componentName);
        initListeners();
    }

    private void initListeners() {
        // Listener for Load Components button
        view.getInputComponentsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLoadComponents();
            }
        });

        // Listener for Component Selection dropdown
        view.getComponentSelector().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleComponentSelection();
            }
        });

        // Listener for Load Input button
        view.getInputDataButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLoadInputData();
            }
        });
        
        // Listener for Paste Input button
        view.getPasteDataButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePasteData();
            }
        });

        // Listener for Process Data button
        view.getProcessDataButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleProcessData();
            }
        });

        // Listener for Copy Output button
        view.getCopyOutputButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCopyOutput();
            }
        });

        // Listener for Save Output button
        view.getSaveOutputButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSaveOutput();
            }
        });

        // Listener for Clear Data button
        view.getClearButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleClearData();
            }
        });

        // MouseListener for title README link
        view.getTitleLabel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleReadmeClick();
            }
        });

        // Listener for changes in the input text area in real time
        view.getInputTextArea().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateModelInput();
            }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateModelInput();
            }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateModelInput();
            }

            private void updateModelInput() {
                model.setInputText(view.getInputTextArea().getText());
            }
        });
    }

    // Load components from file YML
    private void handleLoadComponents() {
        JFileChooser fc = new JFileChooser(model.getConfigHandler().getAppDirectory().toFile());

        fc.setFileFilter(new FileNameExtensionFilter("YAML files (*.yml)", "yml"));
        int fileChoice = fc.showOpenDialog(view);
        if (fileChoice == JFileChooser.APPROVE_OPTION) {
            File configFile = fc.getSelectedFile();
            String filename = configFile.getName().toLowerCase();
            if (!filename.endsWith(".yml")) return;

            // Open terminal
            try {
                Path logFile = StaticUtil.initLogFile();
                TerminalLauncher.openLogTailer(logFile);
            } catch (Exception ex) {ex.printStackTrace();}

            // Read/load YAML file/config
            model.getConfigHandler().loadConfig(configFile);
            updateDropdown();
        }
    }

    public void updateDropdown() {
        // Add each components name to view
        List<Component> componentList = model.getComponents();
        if (componentList==null || componentList.isEmpty() ) return;
        view.getComponentSelector().removeAllItems();
        for(Component t : componentList) {
            view.getComponentSelector().addItem(t.getName());
        }
    }

    // Process the data in the Model
    private void handleComponentSelection() {
        String componentString = (String) view.getComponentSelector().getSelectedItem();
        model.setSelectedComponent(componentString);
    }

    // Load input from CSV or TXT
    private void handleLoadInputData() {
        JFileChooser fc = new JFileChooser(model.getConfigHandler().getAppDirectory().toFile());

        fc.setFileFilter(new FileNameExtensionFilter("CSV (*.csv) or TXT (*.txt) files", "csv","txt"));
        int choice = fc.showOpenDialog(view);
        if(choice == JFileChooser.APPROVE_OPTION) {
            File dataFile = fc.getSelectedFile();

            try {
                List<String> lines = Files.readAllLines(dataFile.toPath());
                String content = String.join("\n", lines);
                view.getInputTextArea().setText(content);
                model.setInputText(content);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Error reading file: " + e.getMessage());
            }
        }
    }

    // Process the data in the Model
    private void handleProcessData() {
        try {
            Path logFile = StaticUtil.initLogFile();
            TerminalLauncher.openLogTailer(logFile);
        } catch (Exception ex) {ex.printStackTrace();}

        // Use selectedComponent in the model to process the data
        model.setOutputText(model.processData());

        // Update view with new data
        view.getOutputTextArea().setText(model.getOutputText());
    }

    // Copy output text to the system clipboard
    private void handleCopyOutput() {
        String output = view.getOutputTextArea().getText();
        StringSelection stringSelection = new StringSelection(output);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    // Save output to CSV or TXT
    private void handleSaveOutput() {
        String output = view.getOutputTextArea().getText();
        
        JFileChooser fc = new JFileChooser(model.getConfigHandler().getAppDirectory().toFile());
        // CSV and TXT filters
        FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV files (*.csv)", "csv");
        FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Text files (*.txt)", "txt");
        fc.addChoosableFileFilter(csvFilter);
        fc.addChoosableFileFilter(txtFilter);
        
        // Re-enable the “All Files” filter
        fc.setAcceptAllFileFilterUsed(true);
        
        // Optionally set CSV as the default
        fc.setFileFilter(csvFilter);
        
        int choice = fc.showSaveDialog(view);
        if (choice != JFileChooser.APPROVE_OPTION) return;
        
        File selected = fc.getSelectedFile();
        // Determine extension to append only if user picked CSV or TXT filter
        FileFilter chosen = fc.getFileFilter();
        String path = selected.getAbsolutePath();
        
        if (chosen == csvFilter) {
            if (!path.toLowerCase().endsWith(".csv")) path += ".csv";
        } else if (chosen == txtFilter) {
            if (!path.toLowerCase().endsWith(".txt")) path += ".txt";
        }
        // if “All Files” is selected, we leave the exact filename the user typed
        
        File outFile = new File(path);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
            writer.write(output);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(view,
                "Failed to save file:\n" + e.getMessage(),
                "Save Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Clear input & output data
    private void handleClearData() {
        model.clearData();
        view.getInputTextArea().setText("");
        view.getOutputTextArea().setText("");
    }

    private void handlePasteData() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable contents = clipboard.getContents(null);
            
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String clipboardText = (String) contents.getTransferData(DataFlavor.stringFlavor);
                // Overwrite existing contents
                view.getInputTextArea().setText(clipboardText);
    
                // Also update the Model
                model.setInputText(view.getInputTextArea().getText());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error pasting from clipboard: " + e.getMessage());
        }
    }

    // Handle clicks on the README link
    private void handleReadmeClick() {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(README_URL));
            view.getTitleLabel().setText("<html><b>PreProcessIt</b> v0.1.8.1-beta, <br/>" +
                                         "<a href='' style='color: purple; text-decoration: underline;'>README</a>, <i>made by @tbm00</i></html>");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Failed to open README link: " + e.getMessage());
        }
    }
}