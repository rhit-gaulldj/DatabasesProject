package screens;

import components.NavHandler;
import databaseServices.AthleteService;
import dbObj.Athlete;
import dbObj.Gender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AthleteModifyScreen extends Screen {

    private AthleteService athleteService;
    private int currentAthleteId = -1;
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

        genderField = new JComboBox<>(GENDERS);
        form.add(new JLabel("Gender:"));
        form.add(genderField);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            navHandler.navigate(ScreenTypes.AthletesList, new ScreenOpenArgs());
        });
        submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            submit();
        });
        form.add(cancelButton);
        form.add(submitButton);
    }

    @Override
    public void openScreen(ScreenOpenArgs args) {
        // Args should have a field for the athlete ID, if it doesn't exist then we are creating an athlete
        if (args.has("athlete_id")) {
            currentAthleteId = (int) args.get("athlete_id");
        } else {
            currentAthleteId = -1;
        }
    }

    private void submit() {
        if (currentAthleteId < 0) {
            // Not modifying an athlete, so we should create them
            String fname = firstNameField.getText();
            String lname = lastNameField.getText();

            if (fname.length() <= 0 || lname.length() <= 0) {
                JOptionPane.showMessageDialog(null, "First and last name are required");
                return;
            }

            int gradYear = (int) gradYrField.getValue();
            Gender gender = Gender.fromLongString(genderField.getSelectedItem().toString());
            Athlete a = new Athlete(-1, fname, lname, gender, gradYear);
            athleteService.insertAthlete(a);
            ScreenOpenArgs args = new ScreenOpenArgs();
            args.add("page", 0);
            navHandler.navigate(ScreenTypes.AthletesList, args);
        } else {
            // Update an existing athlete
        }
    }
}
