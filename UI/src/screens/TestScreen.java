package screens;

import databaseServices.UserService;
import util.SimpleAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestScreen extends Screen {

    private SimpleAction onLogout;
    private UserService userService;
    private String sessionId;

    public TestScreen(SimpleAction onLogout, UserService userService) {
        super();
        this.onLogout = onLogout;
        this.userService = userService;
    }

    @Override
    public void populatePanel() {
        super.createPanel(1, 1);
        JPanel panel = getPanel();
        JButton logoutButton = new JButton("Log Out");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userService.logOut(sessionId);
                onLogout.call();
            }
        });
        panel.add(logoutButton);
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
