package dev.tbm00.preprocessit.view;

import javax.swing.*;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.beans.*;

/**
 * A simplified line number component that attaches to a JTextArea
 * to display line numbers on the left side.
 */
public class LineNumber extends JPanel implements CaretListener, DocumentListener, PropertyChangeListener {
    private static final long serialVersionUID = 1L;

    private final JTextArea textArea;
    private int lineHeight;
    private int currentDigits;

    public LineNumber(JTextArea textArea) {
        this.textArea = textArea;
        setForeground(Color.GRAY);
        setBackground(Color.LIGHT_GRAY);
        textArea.getDocument().addDocumentListener(this);
        textArea.addCaretListener(this);
        textArea.addPropertyChangeListener(this);
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    }

    @Override
    public Dimension getPreferredSize() {
        int lines = getLineCount();
        int digits = Math.max(String.valueOf(lines).length(), 2);
        if (digits != currentDigits) {
            currentDigits = digits;
            FontMetrics fontMetrics = getFontMetrics(getFont());
            int width = digits * fontMetrics.charWidth('0');
            return new Dimension(width + 10, textArea.getHeight());
        }
        return new Dimension(super.getPreferredSize().width, textArea.getHeight());
    }

    protected int getLineCount() {
        return textArea.getLineCount();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        FontMetrics fontMetrics = textArea.getFontMetrics(textArea.getFont());
        lineHeight = fontMetrics.getHeight();
        int start = textArea.viewToModel(new Point(0,0));
        int end = textArea.viewToModel(new Point(0, textArea.getHeight()));
        
        // Convert to line numbers
        try {
            int startLine = textArea.getLineOfOffset(start);
            int endLine = textArea.getLineOfOffset(end);
            int y = -fontMetrics.getDescent() + fontMetrics.getLeading() +  (startLine + 1) * lineHeight;
            for (int i = startLine; i <= endLine; i++) {
                String lineNumber = String.valueOf(i + 1);
                int x = getWidth() - fontMetrics.stringWidth(lineNumber) - 2;
                g.drawString(lineNumber, x, y);
                y += lineHeight;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(textArea, "Error rendering line numbers: " + e.getMessage());
        }
    }

    // DocumentListener methods
    @Override
    public void insertUpdate(DocumentEvent e) {
        repaint();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        repaint();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        repaint();
    }

    // CaretListener
    @Override
    public void caretUpdate(CaretEvent e) {
        repaint();
    }

    // PropertyChangeListener
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("font".equals(evt.getPropertyName())) {
            SwingUtilities.invokeLater(this::revalidate);
        }
    }
} 