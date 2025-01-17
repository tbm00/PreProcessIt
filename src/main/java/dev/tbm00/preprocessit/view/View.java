package dev.tbm00.preprocessit.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;

/**
 * View is responsible for UI components
 */
public class View extends JFrame {
    /**
     * Header (top 10%)
     */
    private JPanel headerPanel;                                 // headerTop + headerBottom
        private JSplitPane headerTop;                           // textPanel + controlPanel
            private JPanel textPanel;                           // titleLabel + subLabel
                private JLabel titleLabel;                      // Label for title text
                private JLabel subLabel;                        // Label for instruction text
            private JPanel controlPanel;                        // buttonsContainer
                private JPanel buttonsContainer;                // Panel for main buttons
                    private JButton inputTemplatesButton;       // Input config templates from yml
                    private JComboBox<String> templateSelector; // Dropdown of templates
                    private JButton pasteDataButton;            // Paste input from clipboard
        private JPanel headerBottom;                            // Panel for IO buttons
            private JButton inputDataButton;                    // Input data as CSV or TXT
            private JButton processDataButton;                  // Triggers processing
            private JButton copyOutputButton;                   // Copy output to clipboard
            private JButton saveOutputButton;                   // Save output to CSV or TXT
            private JButton clearButton;                        // Clears input/output


    /**
     * IO (center 90%)
     */
    private JSplitPane ioSplitPane;                     // SplitPanel for IO
        private JScrollPane inputScrollPane;            // scroll panel (left)
            private JTextArea inputTextArea;            // input text screen (left)
            private LineNumber inputLineNumber;         // line number component (left)
        private JScrollPane outputScrollPane;           // scroll panel (right)
            private JTextArea outputTextArea;           // input text screen (right)
            private LineNumber outputLineNumber;        // line number component (right)


    public View() {
        super("PreProcessIt");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize and add the header panel
        initializeHeader();
        add(headerPanel, BorderLayout.NORTH);

        // Initialize and add the IO split pane
        // (inputArea:left, outputArea:right)
        initializeIOPane();
        add(ioSplitPane, BorderLayout.CENTER);
    }

    private void initializeHeader() {
        headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());

        initializeToolPanel();
        headerPanel.add(headerBottom, BorderLayout.SOUTH);

        initializeTextPanel();
        initializeControlPanel();
        headerTop = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                textPanel,
                controlPanel
        );
        headerTop.setResizeWeight(0.3);
        headerTop.setDividerSize(0);
        headerPanel.add(headerTop, BorderLayout.CENTER);
    }

    private void initializeTextPanel() {
        // Initialize textPanel on the left
        textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBorder(new EmptyBorder(5, 5, 5, 0));

        // Initialize title label with right alignment inside textPanel
        titleLabel = new JLabel();
        String titleHtml = "<html><b>PreProcessIt</b> v0.0.5-beta, <br/>" +
                "<a href='' style=color: blue; text-decoration: underline;>README</a>, <i>made by @tbm00</i></html>";
        titleLabel.setText(titleHtml);
        titleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        titleLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // Initialize subtitle label with right alignment inside textPanel
        subLabel = new JLabel();
        String subHtml = "<html><div style='text-align: left;'>" +
                "<b>0th:</b> Load Alternative Config<br/>" +
                "<b>1st:</b> Select Template<br/>" +
                "<b>2nd:</b> Load/Paste Input Data <br/>" +
                "<b>3rd:</b> Process Data <br/>" +
                "<b>4th:</b> Save/Copy Output Data" +
                "</div></html>";
        subLabel.setText(subHtml);
        subLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // Add labels to textPanel
        textPanel.add(titleLabel);
        textPanel.add(subLabel);
    }

    private void initializeControlPanel() {
        // Initialize main control buttons panel
        buttonsContainer = new JPanel();
        buttonsContainer.setLayout(new BoxLayout(buttonsContainer, BoxLayout.X_AXIS));

        inputTemplatesButton = new JButton("Load Alternative Config");
        templateSelector = new JComboBox<>(new String[]{"*NO TEMPLATES LOADED*"});
        Dimension preferredSize = templateSelector.getPreferredSize();
        templateSelector.setMaximumSize(preferredSize);
        processDataButton = new JButton("Process Data");

        buttonsContainer.add(inputTemplatesButton);
        buttonsContainer.add(templateSelector);
        buttonsContainer.add(processDataButton);

        buttonsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Create buttonContainer on the right with vertical BoxLayout
        controlPanel = new JPanel();
        //controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        //controlPanel.add(buttonsContainer);

        controlPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // Center horizontally
        gbc.gridy = 0; // Center vertically
        gbc.anchor = GridBagConstraints.CENTER; // Ensure centered alignment;

        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
        buttonContainer.add(buttonsContainer);

        controlPanel.add(buttonContainer, gbc);
    }

    private void initializeToolPanel() {
        // Initialize IO buttons panel with left-aligned contents
        headerBottom = new JPanel();
        headerBottom.setLayout(new GridLayout(1,5));

        inputDataButton = new JButton("Load Input");
        pasteDataButton = new JButton("Paste Input");
        clearButton = new JButton("*CLEAR*");
        copyOutputButton = new JButton("Copy Output");
        saveOutputButton = new JButton("Save Output");

        headerBottom.add(inputDataButton);
        headerBottom.add(pasteDataButton);
        headerBottom.add(clearButton);
        headerBottom.add(copyOutputButton);
        headerBottom.add(saveOutputButton);
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

        ioSplitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            inputScrollPane,
            outputScrollPane
        );
        ioSplitPane.setResizeWeight(0.5);
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