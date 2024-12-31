package dev.tbm00.preprocessit.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;

/**
 * View is responsible for UI components
 */
public class View extends JFrame {
    // Top Panel (Control, top 10%)
    private JPanel headerPanel;                 // textPanel + Main&IO buttons
    private JPanel textPanel;                   // Title + Subtitle
    private JLabel titleLabel;                  // Label for title text
    private JLabel subLabel;                    // Label for instruction text
    private JPanel mainButtonPanel;             // Panel for main buttons
    private JPanel ioButtonPanel;               // Panel for IO buttons
    private JButton inputTemplatesButton;       // Input config templates from yml
    private JComboBox<String> templateSelector; // Dropdown of templates
    private JButton inputDataButton;            // Input data as CSV or TXT
    private JButton pasteDataButton;            // Paste input from clipboard
    private JButton processDataButton;          // Triggers processing
    private JButton copyOutputButton;           // Copy output to clipboard
    private JButton saveOutputButton;           // Save output to CSV or TXT
    private JButton clearButton;                // Clears input/output

    // Split Pane (lower 90%)
    private JSplitPane splitPane;               // SplitPanel for IO

    // Left Pane - Input
    private JTextArea inputTextArea;
    private LineNumber inputLineNumber;
    private JScrollPane inputScrollPane;

    // Right Pane - Output
    private JTextArea outputTextArea;
    private LineNumber outputLineNumber;
    private JScrollPane outputScrollPane;

    public View() {
        super("PreProcessIt");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Create top control panel (10% of frame height)
        initializeHeaderPanel();
        // Add Control Panel to JFrame
        add(headerPanel, BorderLayout.NORTH);

        // Create SplitPane for Input (left) and Output (right) (90% of frame height)
        initializeIOPane();
        // Add IO SplitPane to JFrame
        add(splitPane, BorderLayout.CENTER);
    }

    private void initializeHeaderPanel() {
        headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
        textPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Initialize title
        titleLabel = new JLabel();
        String titleHtml = "<html><b>PreProcessIt</b> v0.0.5-beta, <br/>" +
                           "<a href='' style=color: blue; text-decoration: underline;>README</a>, <i>made by @tbm00</i></html>";                 
        titleLabel.setText(titleHtml);
        //titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        titleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Initialize instrustions (subtitle)
        subLabel = new JLabel();
        String subHtml = "<html><div style='text-align: left;'>" +
                         "<b>0th:</b> Load Additional Config<br/>" +
                         "<b>1st:</b> Select Template<br/>" +
                         "<b>2nd:</b> Load/Paste Input Data <br/>" +
                         "<b>3rd:</b> Process Data <br/>" +
                         "<b>4th:</b> Save/Copy Output Data" +
                         "</div></html>";
        subLabel.setText(subHtml);
        //subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Add text labels to textPanel
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(25, 0)));
        textPanel.add(subLabel);

        // Initialize main buttons/panel
        mainButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        inputTemplatesButton = new JButton("Load Alternative Config");
        templateSelector = new JComboBox<>(new String[]{"*NO TEMPLATES LOADED*"});
        processDataButton = new JButton("Process Data");
        mainButtonPanel.add(inputTemplatesButton);
        mainButtonPanel.add(templateSelector);
        mainButtonPanel.add(processDataButton);
        //mainButtonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Initialize IO buttons/panel
        ioButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        inputDataButton = new JButton("Load Input");
        pasteDataButton = new JButton("Paste Input");
        clearButton = new JButton("*CLEAR*");
        copyOutputButton = new JButton("Copy Output");
        saveOutputButton = new JButton("Save Output");
        ioButtonPanel.add(inputDataButton);
        ioButtonPanel.add(pasteDataButton);
        ioButtonPanel.add(clearButton);
        ioButtonPanel.add(copyOutputButton);
        ioButtonPanel.add(saveOutputButton);
        //ioButtonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add main buttons, seperators, & textPanel to headerPanel
        headerPanel.add(textPanel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); // Span full width, height 1px
        separator.setForeground(Color.GRAY);
        separator.setBackground(Color.GRAY);
        headerPanel.add(separator);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(mainButtonPanel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(ioButtonPanel);
    }

    private void initializeIOPane() {
        // Left panel: input text area with line numbers
        inputTextArea = new JTextArea();
        inputLineNumber = new LineNumber(inputTextArea);
        inputScrollPane = new JScrollPane(inputTextArea);
        inputScrollPane.setRowHeaderView(inputLineNumber);

        // Right panel: output text area with line numbers
        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        outputLineNumber = new LineNumber(outputTextArea);
        outputScrollPane = new JScrollPane(outputTextArea);
        outputScrollPane.setRowHeaderView(outputLineNumber);

        splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            inputScrollPane,
            outputScrollPane
        );
        splitPane.setResizeWeight(0.5);
    }

    public JButton getInputTemplatesButton() {
        return inputTemplatesButton;
    }

    public JComboBox<String> getTemplateSelector() {
        return templateSelector;
    }

    public JButton getInputDataButton() {
        return inputDataButton;
    }

    public JButton getPasteDataButton() {
        return pasteDataButton;
    }

    public JButton getProcessDataButton() {
        return processDataButton;
    }

    public JButton getCopyOutputButton() {
        return copyOutputButton;
    }

    public JButton getSaveOutputButton() {
        return saveOutputButton;
    }

    public JButton getClearButton() {
        return clearButton;
    }

    public JTextArea getInputTextArea() {
        return inputTextArea;
    }

    public JTextArea getOutputTextArea() {
        return outputTextArea;
    }

    public JLabel getTitleLabel() {
        return titleLabel;
    }
}