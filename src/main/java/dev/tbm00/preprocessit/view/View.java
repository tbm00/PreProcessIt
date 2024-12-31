package dev.tbm00.preprocessit.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;

/**
 * View is responsible for UI components
 */
public class View extends JFrame {
    // Top Panel (Control, top 10%)
    private JPanel controlPanel;                // Title + buttons
    private JLabel titleLabel;                  // Label for title text
    private JLabel subLabel;                    // Label for instruction text
    private JPanel lowerButtonPanel;            // Panel for lower buttons
    private JButton inputTemplatesButton;       // Input config templates from yml
    private JComboBox<String> templateSelector; // Dropdown of templates
    private JButton inputDataButton;            // Input data as CSV or TXT
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
        initializeControlPanel();
        // Add Control Panel to JFrame
        add(controlPanel, BorderLayout.NORTH);

        // Create SplitPane for Input (left) and Output (right) (90% of frame height)
        initializeIOPane();
        // Add IO SplitPane to JFrame
        add(splitPane, BorderLayout.CENTER);
    }

    private void initializeControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

        // Initialize title
        titleLabel = new JLabel();
        String titleHtml = "<html><div style='text-align: center;'>" +
                           "<b>PreProcessIt</b> v0.0.5-beta, " +
                           "<a href=''>README</a>, <i>made by @tbm00</i>" +
                           "</div></html>";
        titleLabel.setText(titleHtml);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Initialize instrustions (subtitle)
        subLabel = new JLabel();
        String subHtml = "<html>" +
                         "<b>1st:</b> Load Config Template(s) <br/>" +
                         "<b>2nd:</b> Select Template <br/>" +
                         "<b>3rd:</b> Load/Paste Input Data <br/>" +
                         "<b>4th:</b> Process Data <br/>" +
                         "<b>5th:</b> Save/Copy Output Data" +
                         "</html>";
        subLabel.setText(subHtml);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Initialize lower buttons
        lowerButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        inputTemplatesButton = new JButton("Load Template Config");
        templateSelector = new JComboBox<>(new String[]{"*NO TEMPLATES LOADED*"});
        inputDataButton = new JButton("Load Input Data");
        processDataButton = new JButton("Process Data");
        copyOutputButton = new JButton("Copy Output");
        saveOutputButton = new JButton("Save Output Data");
        clearButton = new JButton("Clear");

        lowerButtonPanel.add(inputTemplatesButton);
        lowerButtonPanel.add(templateSelector);
        lowerButtonPanel.add(inputDataButton);
        lowerButtonPanel.add(processDataButton);
        lowerButtonPanel.add(copyOutputButton);
        lowerButtonPanel.add(saveOutputButton);
        lowerButtonPanel.add(clearButton);

        lowerButtonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add buttons, subtitle, and title to controlPanel
        controlPanel.add(titleLabel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        controlPanel.add(subLabel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Add JSeparator
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); // Span full width, height 1px
        // Optionally customize the separator's color
        separator.setForeground(Color.GRAY);
        separator.setBackground(Color.GRAY);
        controlPanel.add(separator);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    
        controlPanel.add(lowerButtonPanel);
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