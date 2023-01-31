package components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class ComponentTable extends JPanel {

    private String[] headers;
    private int rows;
    private int cols;
    private JPanel tablePanel;

    public ComponentTable(String[] headers) {
        this.headers = headers;
        rows = 0;
        cols = headers.length;

        tablePanel = new JPanel(new GridBagLayout());
        add(tablePanel);

        addHeaders();
    }

    @Override
    public Dimension getPreferredSize() {
        return tablePanel.getPreferredSize();
    }

    public void setCells(ArrayList<JComponent[]> cells) {
        this.clear();

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < cells.size(); i++) {
            constraints.gridy = i + 1;
            for (int j = 0; j < cells.get(i).length; j++) {
                constraints.gridx = j;
                JComponent comp = cells.get(i)[j];
                // Use wrapper JPanel to avoid modifying source component borders
                JPanel p = new JPanel();
                p.add(comp);

                Border blackline = BorderFactory.createLineBorder(Color.black);
                Border margin = new EmptyBorder(5, 5, 5, 5);
                p.setBorder(new CompoundBorder(blackline, margin));
                tablePanel.add(p, constraints);
            }
        }
    }
    public void clear() {
        tablePanel.removeAll();
        rows = 0;
        addHeaders();
    }

    private void addHeaders() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 0;
        for (int i = 0; i < headers.length; i++) {
            constraints.gridx = i;
            JLabel label = new JLabel(headers[i]);

            Border blackline = BorderFactory.createLineBorder(Color.black);
            Border margin = new EmptyBorder(5, 5, 5, 5);
            label.setBorder(new CompoundBorder(blackline, margin));

            tablePanel.add(label, constraints);
        }
    }

}
