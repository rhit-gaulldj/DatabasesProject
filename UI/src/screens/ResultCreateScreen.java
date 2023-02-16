package screens;

import components.NavHandler;
import components.SplitInput;
import components.TimeInput;
import databaseServices.AthleteService;
import databaseServices.RaceService;
import dbObj.Athlete;
import dbObj.Split;
import dbObj.Time;

import javax.swing.*;
import javax.xml.transform.Result;
import java.awt.*;
import java.util.ArrayList;

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
    private ArrayList<SplitInput> splitFields;
    private JPanel splitFieldContainer;

    public ResultCreateScreen(RaceService raceService, AthleteService athleteService,
                              NavHandler navHandler) {
        this.navHandler = navHandler;
        this.raceService = raceService;
        this.athleteService = athleteService;

        splitFields = new ArrayList<>();
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

        JPanel splitTitleContainer = new JPanel();
        splitTitleContainer.add(new JLabel("Splits:"));
        parent.add(splitTitleContainer);

        JPanel splitRow = new JPanel();
        splitRow.setLayout(new BoxLayout(splitRow, BoxLayout.Y_AXIS));
        splitFieldContainer = new JPanel();
        splitFieldContainer.setLayout(new BoxLayout(splitFieldContainer, BoxLayout.Y_AXIS));
        splitRow.add(splitFieldContainer);
        JPanel addSplitButtonContainer = new JPanel();
        JButton addSplitButton = new JButton("Add New Split");
        addSplitButton.addActionListener(e -> addSplitClicked());
        addSplitButtonContainer.add(addSplitButton);
        splitRow.add(addSplitButtonContainer);
        parent.add(splitRow);

        JPanel bottomRow = new JPanel();
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> submit());
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

    private void submit() {
        if (athleteField.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Must select a valid athlete");
            return;
        }
        Time totalTime = timeField.getTime();
        if (totalTime == null) {
            JOptionPane.showMessageDialog(null, "Must provide a valid time");
            return;
        }
        // Get all the splits
        ArrayList<Split> splits = new ArrayList<>();
        for (int i = 0; i < splitFields.size(); i++) {
            Split s = splitFields.get(i).getSplit();
            if (s == null || s.dist() == null || s.time() == null) {
                JOptionPane.showMessageDialog(null, "Must fill in all splits, or have invalid inputs");
                return;
            }
            splits.add(s);
        }

        raceService.addResult(raceId, ((Athlete) athleteField.getSelectedItem()).id(),
                timeField.getTime(), splits);
        goBack();
    }

    private void goBack() {
        ScreenOpenArgs args = new ScreenOpenArgs();
        args.add("id", meetId);
        navHandler.navigate(ScreenTypes.MeetView, args);
    }

    private void resetFields() {
        Athlete[] athletes = athleteService.getAthletesNotInRace(raceId);
        athleteField.setModel(new DefaultComboBoxModel<>(athletes));

        timeField.clear();

        splitFields.clear();
        splitFieldContainer.removeAll();
    }

    private void addSplitClicked() {
        addEmptySplitContainer();
    }
    private void deleteSplitClicked(Object obj) {
        SplitInput split = (SplitInput) obj;
        splitFieldContainer.remove(split);
        splitFields.remove(split);
        getPanel().repaint();
        getPanel().revalidate();
    }
    private void addEmptySplitContainer() {
        SplitInput newSi = new SplitInput();
        newSi.setDeleteAction(this::deleteSplitClicked);
        splitFields.add(newSi);
        splitFieldContainer.add(newSi);
        getPanel().repaint();
        getPanel().revalidate();
    }
}
