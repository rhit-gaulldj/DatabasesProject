package screens;

import components.NavHandler;
import databaseServices.AthleteService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AthleteModifyScreen extends Screen {

    private AthleteService athleteService;
    private NavHandler navHandler;

    private JLabel titleLabel;
    private JButton submitButton;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JSpinner gradYrField;
    private JComboBox<String> genderField;

    private final String[] GENDERS = { "Male", "Female", "Other" };

    public AthleteModifyScreen(AthleteService athleteService, NavHandler navHandler) {
        this.athleteService = athleteService;
        this.navHandler = navHandler;
    }

    @Override
    public void populatePanel() {
        super.createPanel(2, 1);
        JPanel parent = super.getPanel();

        titleLabel = new JLabel("TITLE");
        parent.add(titleLabel);

        JPanel form = new JPanel();
        form.setLayout(new GridLayout(5, 2));
        parent.add(form);

        firstNameField = new JTextField();
        form.add(new JLabel("First Name:"));
        form.add(firstNameField);

        lastNameField = new JTextField();
        form.add(new JLabel("Last Name:"));
        form.add(lastNameField);

        gradYrField = new JSpinner(new SpinnerNumberModel(2020, 1900, 3000, 1));
        gradYrField.setEditor(new JSpinner.NumberEditor(gradYrField, "#"));
        form.add(new JLabel("Graduation Year:"));
        form.add(gradYrField);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            navHandler.navigate(ScreenTypes.AthletesList, new ScreenOpenArgs());
        });
        submitButton = new JButton("Submit");
        form.add(cancelButton);
        form.add(submitButton);
    }

    @Override
    public void openScreen(ScreenOpenArgs args) {
        // Args should have a field for the athlete ID, if it doesn't exist then we are creating an athlete
    }
}
