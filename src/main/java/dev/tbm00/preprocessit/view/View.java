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
    private JPanel headerPanel;					// holds: headerTop + toolPanel
    private JSplitPane headerTop;                   // holds: textPanel + controlPanel
    private JPanel textPanel;                           // holds: titleLabel + subLabel
    private JLabel titleLabel;                              // Label for title text
    private JLabel subLabel;                                // Label for instruction text
    private JPanel controlPanel;                        // holds: buttonsContainer
    private JPanel buttonsContainer;                        // holds: control buttons
    private JButton inputComponentsButton;       		        // Button for inputting config as YML
    private JComboBox<String> componentSelector; 		        // Dropdown for selecting config component
    private JButton processDataButton;          		        // Button for triggering data process
    private JPanel toolPanel;                       // holds: tool buttons
    private JButton inputDataButton;                    // Button for inputting data as CSV or TXT
    private JButton pasteDataButton;                    // Button for pasting data from clipboard
    private JButton copyOutputButton;                   // Button for copying data to clipboard
    private JButton saveOutputButton;                   // Button for outputting data as CSV or TXT
    private JButton clearButton;                        // Button for clearing input & output


    /**
     * IO (center 90%)
     */
    private JSplitPane ioSplitPane;				// SplitPanel for IO
    private JScrollPane inputScrollPane;            // scroll panel (left)
    private JTextArea inputTextArea;            		// input text screen (left)
    private LineNumber inputLineNumber;         		// line number component (left)
    private JScrollPane outputScrollPane;           // scroll panel (right)
    private JTextArea outputTextArea;           		// input text screen (right)
    private LineNumber outputLineNumber;        		// line number component (right)


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
        initializeIOPane();
        add(ioSplitPane, BorderLayout.CENTER);
    }

    private void initializeHeader() {
        headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());

        initializeTextPanel();
        initializeControlPanel();
        headerTop = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                textPanel,
                controlPanel
        );
        headerTop.setResizeWeight(0.5);
        headerTop.setDividerSize(0);
        headerPanel.add(headerTop, BorderLayout.CENTER);

        initializeToolPanel();
        headerPanel.add(toolPanel, BorderLayout.SOUTH);
    }

    private void initializeTextPanel() {
        // Initialize textPanel on the left
        textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
        textPanel.setBorder(new EmptyBorder(5, 5, 5, 0));

        // Initialize title label with right alignment inside textPanel
        titleLabel = new JLabel();
        String titleHtml = "<html><b>PreProcessIt</b> v0.1.8.1-beta, <br/>" +
                "<a href='' style=color: blue; text-decoration: underline;>README</a>, <i>made by @tbm00</i></html>";
        titleLabel.setText(titleHtml);
        titleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        titleLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // Initialize subtitle label with right alignment inside textPanel
        subLabel = new JLabel();
        String subHtml = "<html><div style='text-align: left;'>" +
                "<b>0th:</b> Load Alternative Config<br/>" +
                "<b>1st:</b> Select Component<br/>" +
                "<b>2nd:</b> Load/Paste Input Data <br/>" +
                "<b>3rd:</b> Process Data <br/>" +
                "<b>4th:</b> Save/Copy Output Data" +
                "</div></html>";
        subLabel.setMaximumSize(new Dimension(200, 200));
        subLabel.setText(subHtml);
        subLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Add labels to textPanel
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        textPanel.add(subLabel);
        textPanel.add(Box.createRigidArea(new Dimension(15, 0)));
    }

    private void initializeControlPanel() {
        // Initialize main control buttons panel
        buttonsContainer = new JPanel();
        buttonsContainer.setLayout(new BoxLayout(buttonsContainer, BoxLayout.X_AXIS));

        inputComponentsButton = new JButton("Load Alternative Config");
        componentSelector = new JComboBox<>(new String[]{"*NO TEMPLATES LOADED*"});
        Dimension preferredSize = componentSelector.getPreferredSize();
        componentSelector.setMaximumSize(preferredSize);
        processDataButton = new JButton("Process Data");

        buttonsContainer.add(inputComponentsButton);
        buttonsContainer.add(componentSelector);
        buttonsContainer.add(processDataButton);

        buttonsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Create buttonContainer on the right with vertical BoxLayout
        controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.add(buttonsContainer, BorderLayout.WEST);
    }

    private void initializeToolPanel() {
        // Initialize IO buttons panel with left-aligned contents
        toolPanel = new JPanel();
        toolPanel.setLayout(new GridLayout(0,5));

        inputDataButton = new JButton("Load Input");
        pasteDataButton = new JButton("Paste Input");
        clearButton = new JButton("*CLEAR*");
        copyOutputButton = new JButton("Copy Output");
        saveOutputButton = new JButton("Save Output");

        toolPanel.add(inputDataButton);
        toolPanel.add(pasteDataButton);
        toolPanel.add(clearButton);
        toolPanel.add(copyOutputButton);
        toolPanel.add(saveOutputButton);
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

    public JButton getInputComponentsButton() {
        return inputComponentsButton;
    }

    public JComboBox<String> getComponentSelector() {
        return componentSelector;
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