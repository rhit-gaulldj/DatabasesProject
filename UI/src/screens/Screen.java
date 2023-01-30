package screens;

import javax.swing.*;
import java.awt.*;

public abstract class Screen {

    private JPanel panel;

    public Screen() {
        //populatePanel();
    }

    public abstract void populatePanel();

    public void createPanel(int rows, int cols) {
        GridLayout layout = new GridLayout(rows, cols);
        panel = new JPanel();
        panel.setLayout(layout);
    }

    public JPanel getPanel() {
        return panel;
    }
}
