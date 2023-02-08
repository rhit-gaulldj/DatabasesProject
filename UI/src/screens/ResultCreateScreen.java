package screens;

import components.NavHandler;
import components.TimeInput;
import databaseServices.AthleteService;
import databaseServices.RaceService;
import dbObj.Athlete;

import javax.swing.*;
import javax.xml.transform.Result;
import java.awt.*;

public class ResultCreateScreen extends Screen {

    private NavHandler navHandler;

    private RaceService raceService;
    private AthleteService athleteService;

    private int meetId;
    private String meetName;
    private int meetYear;
    private int courseId;
    private int raceId;

    private JLabel titleLabel;
    private JComboBox<Athlete> athleteField;
    private TimeInput timeField;

    public ResultCreateScreen(RaceService raceService, AthleteService athleteService,
                              NavHandler navHandler) {
        this.navHandler = navHandler;
        this.raceService = raceService;
        this.athleteService = athleteService;
    }

    @Override
    public void populatePanel() {
        super.createPanel();
        JPanel parent = super.getPanel();
        super.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));

        JPanel titleContainer = new JPanel();
        titleLabel = new JLabel("TITLE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleContainer.add(titleLabel);
        parent.add(titleContainer);

        JPanel athleteRow = new JPanel();
        athleteField = new JComboBox<>();
        athleteRow.add(new JLabel("Athlete: "));
        athleteRow.add(athleteField);
        parent.add(athleteRow);

        JPanel timeRow = new JPanel();
        timeField = new TimeInput();
        timeRow.add(new JLabel("Time: "));
        timeRow.add(timeField);
        parent.add(timeRow);

        JPanel bottomRow = new JPanel();
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> goBack());
        bottomRow.add(cancelButton);
        bottomRow.add(submitButton);
        parent.add(bottomRow);
    }

    @Override
    public void openScreen(ScreenOpenArgs args) {
        meetId = (int) args.get("meet_id");
        meetName = args.get("meet_name").toString();
        meetYear =  (int) args.get("meet_year");
        courseId = (int) args.get("course_id");

        raceId = (int) args.get("race_id");
        String raceName = args.get("race_name").toString();
        titleLabel.setText("Add Result for " + raceName + " Race in " + meetName + " (" + meetYear + ")");

        resetFields();
    }

    private void goBack() {
        ScreenOpenArgs args = new ScreenOpenArgs();
        args.add("id", meetId);
        args.add("name", meetName);
        args.add("year", meetYear);
        args.add("course_id", courseId);
        navHandler.navigate(ScreenTypes.MeetView, args);
    }

    private void resetFields() {
        Athlete[] athletes = athleteService.getAthletesNotInRace(raceId);
        athleteField.setModel(new DefaultComboBoxModel<>(athletes));
    }
}
