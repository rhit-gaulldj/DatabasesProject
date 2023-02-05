package screens;

import components.LinkButton;
import components.NavHandler;
import databaseServices.CourseService;
import dbObj.Course;
import dbObj.DistancePair;
import dbObj.RaceLevel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CourseViewScreen extends Screen {

    private NavHandler navHandler;
    private CourseService courseService;

    private JLabel nameLabel;
    private JCheckBox allowDuplicatesField;
    private JComboBox<RaceLevel> raceLevelField;
    private JSpinner numResultsField;
    private JComboBox<DistancePair> distanceField;

    public CourseViewScreen(CourseService courseService, NavHandler navHandler) {
        this.navHandler = navHandler;
        this.courseService = courseService;
    }

    @Override
    public void populatePanel() {
        super.createPanel(8, 1);
        JPanel parent = super.getPanel();

        LinkButton backButton = new LinkButton(new Color(5, 138, 255), "<< Back", 12);
        backButton.addActionListener(() -> navHandler.navigate(ScreenTypes.CourseList, new ScreenOpenArgs()));
        parent.add(backButton);

        nameLabel = new JLabel("");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        parent.add(nameLabel);

        allowDuplicatesField = new JCheckBox("Allow multiple results from the same athlete", false);
        parent.add(allowDuplicatesField);

        JPanel raceLevelPanel = new JPanel();
        raceLevelField = new JComboBox<>(courseService.getRaceLevels());
        raceLevelPanel.add(new JLabel("Race Level:"));
        raceLevelPanel.add(raceLevelField);
        parent.add(raceLevelPanel);

        JPanel numResultsPanel = new JPanel();
        numResultsField = new JSpinner(new SpinnerNumberModel(10, 5, 25, 5));
        numResultsField.setEditor(new JSpinner.NumberEditor(numResultsField, "#"));
        numResultsPanel.add(new JLabel("Number of Results:"));
        numResultsPanel.add(numResultsField);
        parent.add(numResultsPanel);

        JPanel distancePanel = new JPanel();
        distanceField = new JComboBox<>();
        distancePanel.add(new JLabel("Distance:"));
        distancePanel.add(distanceField);
        parent.add(distancePanel);

        JButton resultsButton = new JButton("Show Results");
        resultsButton.addActionListener(e -> showResults());
        parent.add(resultsButton);
    }

    @Override
    public void openScreen(ScreenOpenArgs args) {
        nameLabel.setText((String) args.get("name"));

        int id = (int) args.get("id");
        DefaultComboBoxModel<DistancePair> model =
                new DefaultComboBoxModel<>(courseService.getCourseDistances(id));
        distanceField.setModel(model);
    }

    private void showResults() {

    }
}
