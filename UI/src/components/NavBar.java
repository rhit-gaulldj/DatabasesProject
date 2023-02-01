package components;

import databaseServices.UserService;
import screens.ScreenOpenArgs;
import screens.ScreenTypes;

import javax.swing.*;
import java.awt.*;

public class NavBar {

    private JPanel panel;
    private NavHandler handler;
    private UserService userService;

    public NavBar(NavHandler handler, UserService userService) {
        this.handler = handler;
        this.userService = userService;
        panel = new JPanel();
        addButtons();
    }

    public JPanel getPanel() {
        return panel;
    }

    private void addButtons() {
        // Add all navigation buttons as links
        final int FONT_SIZE = 15;
        final Color COLOR = new Color(66, 135, 245);
        final int BULLET_SIZE = 10;

        LinkButton athleteButton = new LinkButton(COLOR, "Athletes", FONT_SIZE);
        ScreenOpenArgs alistOpenArgs = new ScreenOpenArgs();
        alistOpenArgs.add("page", 0);
        athleteButton.addActionListener(() -> handler.navigate(ScreenTypes.AthletesList, alistOpenArgs));
        panel.add(athleteButton);
        panel.add(new Bullet(Color.black, BULLET_SIZE));

        LinkButton logoutButton = new LinkButton(new Color(193, 71, 71), "Logout", FONT_SIZE);
        logoutButton.addActionListener(() -> logout());
        panel.add(logoutButton);

        // TODO: Add new buttons here...
    }

    private void logout() {
        int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to log out?",
                "Confirm Action", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            userService.logOut();
            handler.navigate(ScreenTypes.Login, new ScreenOpenArgs());
        }
    }

}
