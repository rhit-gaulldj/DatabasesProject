package screens;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends Screen {

    private JTextField usernameBox;
    private JTextField passwordBox;

    private JLabel errorLabel;

    @Override
    public void populatePanel() {
        // Parent panel has 2 rows and 1 column (we'll have 2 panels that go inside of it)
        super.createPanel(2, 1);
        JPanel parent = super.getPanel();
        // Create our panels
        JPanel top = new JPanel(); // 2 rows (user, pass), 2 cols (label, field entry)
        top.setLayout(new GridLayout(2, 2));
        JPanel bottom = new JPanel(); // 2 rows (error message, login button), 1 col
        bottom.setLayout(new GridLayout(2, 1));
        parent.add(top);
        parent.add(bottom);

        // Populate the top panel
        this.usernameBox = new JTextField();
        this.passwordBox = new JPasswordField();
        top.add(new JLabel("Username: "));
        top.add(this.usernameBox);
        top.add(new JLabel("Password: "));
        top.add(this.passwordBox);

        // Populate bottom panel
        JButton loginButton = new JButton("Login");
        this.errorLabel = new JLabel("");
        this.errorLabel.setForeground(Color.RED);
        bottom.add(errorLabel);
        bottom.add(loginButton);
    }
}
