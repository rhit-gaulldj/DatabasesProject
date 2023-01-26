package screens;

import databaseServices.UserService;
import util.SimpleAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends Screen {

    private JTextField emailBox;
    private JTextField passwordBox;

    private JLabel msgLabel;

    private SimpleAction onLogin;

    private UserService userService;

    public LoginScreen(UserService userService, SimpleAction onLogin) {
        super();
        this.userService = userService;
        this.onLogin = onLogin;
    }


    @Override
    public void populatePanel() {
        // Parent panel has 2 rows and 1 column (we'll have 2 panels that go inside of it)
        super.createPanel(2, 1);
        JPanel parent = super.getPanel();
        // Create our panels
        JPanel top = new JPanel(); // 3 rows (user, pass, buttons), 2 cols (label, field entry)
        top.setLayout(new GridLayout(3, 2));
        JPanel bottom = new JPanel(); // 1 row (error message), 1 col
        bottom.setLayout(new GridLayout(1, 1));
        parent.add(top);
        parent.add(bottom);

        // Populate the top panel
        this.emailBox = new JTextField();
        this.passwordBox = new JPasswordField();
        top.add(new JLabel("Email: "));
        top.add(this.emailBox);
        top.add(new JLabel("Password: "));
        top.add(this.passwordBox);
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> tryLogin());
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> tryRegister());
        top.add(loginButton);
        top.add(registerButton);

        // Populate bottom panel
        msgLabel = new JLabel("You do not have an active session. Please sign in.");
        msgLabel.setForeground(new Color(47, 124, 255));
        bottom.add(msgLabel);
    }

    private void tryLogin() {
        String email = emailBox.getText();
        String password = passwordBox.getText();
        boolean success = userService.login(email, password);
        if (success) {
            this.onLogin.call();
        } else {
            showErrorMessage("There was an error logging you in.");
        }
    }

    private void tryRegister() {
        String email = emailBox.getText();
        String password = passwordBox.getText();
        boolean success = userService.register(email, password);
        if (success) {
            showSuccessMessage("You have successfully registered, and can now log in to your account.");
        } else {
            showErrorMessage("There was an error registering a new account.");
        }
    }

    private void showSuccessMessage(String msg) {
        msgLabel.setText(msg);
        msgLabel.setForeground(Color.GREEN);
    }
    private void showErrorMessage(String msg) {
        msgLabel.setText(msg);
        msgLabel.setForeground(Color.RED);
    }
}
