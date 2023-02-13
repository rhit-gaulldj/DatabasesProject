package components;

import util.SimpleAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;

public class ResultHighlightLabel extends JComponent {

    private String text;
    private String targetText;

    private Font font;

    private SimpleAction onClick;

    public ResultHighlightLabel(String text, String targetText) {
        this.text = text;
        this.targetText = targetText;

        this.font = new Font("Arial", Font.BOLD, 12);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) {
                    onClick.call();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();

        // Iterate over chars in source text, highlighting when necessary
        int x = 0;
        for (int i = 0; i < text.length(); i++) {
            String check;
            if (i + targetText.length() < text.length()) {
                check = text.substring(i, i + targetText.length());
            } else {
                check = text.substring(i);
            }
            if (check.toLowerCase(Locale.ROOT).equals(targetText.toLowerCase())) {
                // Need to highlight the text here
                // Draw the background box
                g.setColor(Color.blue);
                int width = metrics.stringWidth(check);
                g.fillRect(x, 0, width, metrics.getHeight() + 5);
                // Draw the text on top
                g.setColor(Color.white);
                g.drawString(check, x, metrics.getHeight());
                // Skip over these characters now
                i += check.length() - 1;
                x += width;

            } else {
                // Print one character at a time
                g.setColor(Color.black);
                String c = Character.toString(text.charAt(i));
                g.drawString(c, x, metrics.getHeight());
                x += metrics.stringWidth(c);
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics metrics = getFontMetrics(font);
        return new Dimension(metrics.stringWidth(text) + 4, metrics.getHeight() + 5);
    }

    public void addActionListener(SimpleAction action) {
        onClick = action;
    }
}
