package components;

import javax.swing.*;
import java.awt.*;

public class Bullet extends JComponent {

    private Color color;
    private int size;

    public Bullet(Color color, int size) {
        this.color = color;
        this.size = size;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(size, size);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(color);
        g.fillOval(0, 0, size, size);
    }
}
