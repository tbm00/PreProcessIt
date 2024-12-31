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

        fc.setFileFilter(new FileNameExtensionFilter("", "yml"));
        int fileChoice = fc.showOpenDialog(view);
        if (fileChoice == JFileChooser.APPROVE_OPTION) {
            File templateFile = fc.getSelectedFile();
            if (!templateFile.getName().endsWith(".yml")) return;

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

        fc.setFileFilter(new FileNameExtensionFilter("", "csv","txt"));
        int choice = fc.showOpenDialog(view);
        if(choice == JFileChooser.APPROVE_OPTION) {
            File dataFile = fc.getSelectedFile();
            if (!dataFile.getName().endsWith(".yml")) return;

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
        StringSelection stringSelection = new StringSelection(output);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    // Clear input & output data
    private void handleClearData() {
        model.clearData();
        view.getInputTextArea().setText("");
        view.getOutputTextArea().setText("");
    }

    // Handle clicks on the README link
    private void handleReadmeClick() {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(README_URL));
            view.getTitleLabel().setText("<html><b>PreProcessIt</b> v0.0.5-beta,  " +
                                         "<a href='' style='color: purple; text-decoration: underline;'>README</a>, <i>made by @tbm00</i></html>");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Failed to open README link: " + e.getMessage());
        }
    }
}