package components;

import javax.swing.*;

public class DataRow {

    private JPanel panel;

    public DataRow() {
        this.panel = new JPanel();
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setRow(String[] data) {
        clearData();
        for (int i = 0; i < data.length; i++) {
            panel.add(new JLabel(data[i]));
        }
    }
    public void clearData() {
        panel.removeAll();
    }

}
