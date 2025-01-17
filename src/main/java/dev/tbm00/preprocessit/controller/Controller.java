package dev.tbm00.preprocessit.controller;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

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
import java.util.List;

import dev.tbm00.preprocessit.model.Model;
import dev.tbm00.preprocessit.model.Template;
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

        String selected = (String) view.getTemplateSelector().getSelectedItem();
        model.setTemplateByString(selected);
        initListeners();
    }

    private void initListeners() {
        // Listener for Load Templates button
        view.getInputTemplatesButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLoadTemplates();
            }
        });

        // Listener for Template Selection dropdown
        view.getTemplateSelector().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleTemplateSelection();
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

    // Load templates from file YML
    private void handleLoadTemplates() {
        JFileChooser fc = new JFileChooser(model.getAppDirectory());

        fc.setFileFilter(new FileNameExtensionFilter(".YML", "yml"));
        int fileChoice = fc.showOpenDialog(view);
        if (fileChoice == JFileChooser.APPROVE_OPTION) {
            File templateFile = fc.getSelectedFile();
            String filename = templateFile.getName().toLowerCase();
            if (!filename.endsWith(".yml")) return;

            // Read/load YAML file/config
            model.loadConfig(templateFile);
            updateDropdown();
        }
    }

    public void updateDropdown() {
        // Add each templates name to view
        List<Template> templateList = model.getTemplates();
        if (templateList.isEmpty() || templateList==null) return;
        view.getTemplateSelector().removeAllItems();
        for(Template t : templateList) {
            view.getTemplateSelector().addItem(t.getName());
        }
    }

    // Process the data in the Model
    private void handleTemplateSelection() {
        String selected = (String) view.getTemplateSelector().getSelectedItem();
        model.setTemplateByString(selected);
    }

    // Load input from CSV or TXT
    private void handleLoadInputData() {
        JFileChooser fc = new JFileChooser(model.getAppDirectory());

        fc.setFileFilter(new FileNameExtensionFilter(".CSV or .TXT", "csv","txt"));
        int choice = fc.showOpenDialog(view);
        if(choice == JFileChooser.APPROVE_OPTION) {
            File dataFile = fc.getSelectedFile();
            String filename = dataFile.getName().toLowerCase();
            String type = "";
            if (filename.endsWith(".csv")) type = "CSV";
            else if (filename.endsWith(".txt")) type = "TXT";
            else return;

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
        // Use selectedTemplate in the model to process the data
        model.processData();

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

        // Show a save dialog with CSV/TXT filters
        JFileChooser fc = new JFileChooser(model.getAppDirectory());
        fc.setFileFilter(new FileNameExtensionFilter(".CSV or .TXT", "csv","txt"));
        int choice = fc.showSaveDialog(view);

        if (choice == JFileChooser.APPROVE_OPTION) {
            File outFile = fc.getSelectedFile();
            
            // Optionally, auto-append .txt if no extension was provided, or prompt user.
            // For simplicity, just check if it ends with csv or txt
            String filename = outFile.getName().toLowerCase();
            String type = "";
            if (filename.endsWith(".csv")) type = "CSV";
            else if (filename.endsWith(".txt")) type = "TXT";
            else return;

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
                writer.write(output);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Failed to save file " + e.getMessage());
            }
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
            view.getTitleLabel().setText("<html><b>PreProcessIt</b> v0.0.6-beta, <br/>" +
                                         "<a href='' style='color: purple; text-decoration: underline;'>README</a>, <i>made by @tbm00</i></html>");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Failed to open README link: " + e.getMessage());
        }
    }
}