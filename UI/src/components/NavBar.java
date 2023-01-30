package components;

import screens.ScreenTypes;
import util.SimpleAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

public class NavBar {

    private JPanel panel;
    private NavHandler handler;

    public NavBar(NavHandler handler) {
        this.handler = handler;
        panel = new JPanel();
        addButtons();
    }

    public JPanel getPanel() {
        return panel;
    }

    private void addButtons() {
        // Add all navigation buttons as links
        final int FONT_SIZE = 20;
        final Color COLOR = new Color(66, 135, 245);
        final int BULLET_SIZE = 10;
        LinkButton athleteButton = new LinkButton(COLOR, "Athletes", BULLET_SIZE);
        athleteButton.addActionListener(() -> handler.navigate(ScreenTypes.AthletesList));
        panel.add(athleteButton);
        panel.add(new Bullet(COLOR, BULLET_SIZE));
    }

}
