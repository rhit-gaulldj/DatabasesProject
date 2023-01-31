package screens;

import javax.swing.*;
import java.awt.*;

public abstract class Screen {

    private JPanel panel;

    public Screen() {
        //populatePanel();
    }

    public abstract void populatePanel();
    public abstract void openScreen(ScreenOpenArgs args);

    public void createPanel(int rows, int cols) {
        GridLayout layout = new GridLayout(rows, cols);
        panel = new JPanel();
        panel.setLayout(layout);
    }

    public void createPanel() {
        panel = new JPanel();
    }

    public void setLayout(LayoutManager lm) {
        panel.setLayout(lm);
    }

    public JPanel getPanel() {
        return panel;
    }
}
