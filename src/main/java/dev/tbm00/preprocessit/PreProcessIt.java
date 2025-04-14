package dev.tbm00.preprocessit;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import dev.tbm00.preprocessit.controller.Controller;
import dev.tbm00.preprocessit.model.Model;
import dev.tbm00.preprocessit.view.View;

public class PreProcessIt {

    public static void main(String[] args) {
        // Set system LookAndFeel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            StaticUtil.log("Error setting look and feel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            Model model = new Model();
            View view = new View();
            Controller controller = new Controller(model, view);
            
            // Show the view
            view.setVisible(true);
        });
    }
}