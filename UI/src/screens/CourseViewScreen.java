package screens;

import components.ComponentTable;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CourseViewScreen extends Screen {

    private NavHandler navHandler;
    private CourseService courseService;

    private int courseId;

    private JLabel nameLabel;
    private JCheckBox allowDuplicatesField;
    private JComboBox<RaceLevel> raceLevelField;
    private JSpinner numResultsField;
    private JComboBox<DistancePair> distanceField;

    private static final int NUM_FIELDS = 5;

    private ComponentTable table;

    public CourseViewScreen(CourseService courseService, NavHandler navHandler) {
        this.navHandler = navHandler;
        this.courseService = courseService;
    }

    @Override
    public void populatePanel() {
        super.createPanel();
        JPanel parent = super.getPanel();
        super.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));

        LinkButton backButton = new LinkButton(new Color(5, 138, 255), "<< Back", 12);
        backButton.addActionListener(() -> navHandler.navigate(ScreenTypes.CourseList, new ScreenOpenArgs()));
        parent.add(backButton);

        nameLabel = new JLabel("");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        parent.add(nameLabel);

        JPanel duplicateBoxPanel = new JPanel();
        allowDuplicatesField = new JCheckBox("", false);
        duplicateBoxPanel.add(allowDuplicatesField);
        duplicateBoxPanel.add(new JLabel("Allow multiple results from the same athlete"));
        parent.add(duplicateBoxPanel);

        JPanel raceLevelPanel = new JPanel();
        raceLevelField = new JComboBox<>();
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

        table = new ComponentTable(new String[] { "Athlete", "Time", "Meet", "Year", "Grade" });
        parent.add(table);
    }

    @Override
    public void openScreen(ScreenOpenArgs args) {
        nameLabel.setText((String) args.get("name"));

        this.courseId = (int) args.get("id");

        resetFields();
        // Populate with initial results
        showResults();
    }

    private void showResults() {
        DistancePair dist = (DistancePair) distanceField.getSelectedItem();
        RaceLevel rl = (RaceLevel) raceLevelField.getSelectedItem();
        ResultSet rs = courseService.getTopResultsPerCourse(courseId, (int) numResultsField.getValue(),
                allowDuplicatesField.isSelected(), rl.id(), dist);

        ArrayList<JComponent[]> rows = new ArrayList<>();
        try {
            while (rs.next()) {
                JComponent[] row = new JComponent[NUM_FIELDS];
                for (int i = 0; i < NUM_FIELDS; i++) {
                    row[i] = new JLabel(rs.getString(i + 1));
                }
                rows.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        table.setCells(rows);

        getPanel().repaint();
        getPanel().revalidate();
    }

    private void resetFields() {
        RaceLevel[] levels = courseService.getRaceLevelsForCourse(courseId);
        raceLevelField.setModel(new DefaultComboBoxModel<>(levels));
        // If varsity or F/S option, select those. Otherwise, just let it be the first level
        boolean hasVarsity = false;
        for (int i = 0; i < levels.length; i++) {
            if (levels[i].name().equals("Varsity")) {
                hasVarsity = true;
                raceLevelField.setSelectedIndex(i);
                break;
            }
        }
        if (!hasVarsity) {
            for (int i = 0; i < levels.length; i++) {
                if (levels[i].name().equals("F/S")) {
                    raceLevelField.setSelectedIndex(i);
                    break;
                }
            }
        }

        DistancePair[] distances = courseService.getCourseDistances(courseId);
        DefaultComboBoxModel<DistancePair> model =
                new DefaultComboBoxModel<>(distances);
        distanceField.setModel(model);
        // Prefer to have 3mi option
        for (int i = 0; i < distances.length; i++) {
            if (distances[i].dist() == 3 && distances[i].units().equals("mi")) {
                distanceField.setSelectedIndex(i);
            }
        }

        allowDuplicatesField.setSelected(false);
        numResultsField.setValue(10);
    }
}
