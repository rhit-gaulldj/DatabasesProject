package components;

import util.SimpleAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LinkButton extends JComponent {

    private Color color;
    private String text;

    private Font font;

    private SimpleAction onClick;

    public LinkButton(Color color, String text, int fontSize) {
        this.color = color;
        this.text = text;

        this.font = new Font("Arial", Font.PLAIN, fontSize);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick.call();
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics metrics = getFontMetrics(font);
        return new Dimension(metrics.stringWidth(text) + 4, metrics.getHeight() + 5);
    }

    public void addActionListener(SimpleAction action) {
        this.onClick = action;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setFont(font);
        FontMetrics metrics = getFontMetrics(font);
        g.setColor(color);
        g.drawString(text, 0, metrics.getHeight());

        g.drawLine(-2, metrics.getHeight() + 3, metrics.stringWidth(text) + 2, metrics.getHeight() + 3);
    }
}
