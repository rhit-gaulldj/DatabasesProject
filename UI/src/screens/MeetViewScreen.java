package screens;

import components.ComponentTable;
import components.LinkButton;
import components.NavHandler;
import databaseServices.CourseService;
import databaseServices.MeetService;
import dbObj.Course;
import dbObj.Race;
import dbObj.RaceLevel;
import dbObj.RaceResult;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MeetViewScreen extends Screen {

    private MeetService meetService;
    private CourseService courseService;

    private int meetId;
    private Race currentRace;

    private NavHandler navHandler;

    private JLabel titleLabel;
    private JLabel courseNameLabel;
    private JComboBox<Race> raceField;

    private ComponentTable table;

    public MeetViewScreen(MeetService meetService, CourseService courseService, NavHandler navHandler) {
        this.meetService = meetService;
        this.courseService = courseService;

        this.navHandler = navHandler;
    }

    @Override
    public void populatePanel() {
        super.createPanel();
        JPanel parent = super.getPanel();
        super.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));

        JPanel topPanel = new JPanel();
        LinkButton backButton = new LinkButton(new Color(5, 138, 255), "<< Back", 12);
        backButton.addActionListener(() -> navHandler.navigate(ScreenTypes.MeetList, new ScreenOpenArgs()));
        JButton addNewRaceButton = new JButton("Add New Race");
        addNewRaceButton.addActionListener(e -> System.out.println("Add new race"));
        topPanel.add(backButton);
        topPanel.add(addNewRaceButton);
        parent.add(topPanel);

        titleLabel = new JLabel("TITLE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        parent.add(titleLabel);

        courseNameLabel = new JLabel("COURSE NAME");
        courseNameLabel.setFont(new Font("Arial", Font.BOLD, 15));
        parent.add(courseNameLabel);

        JPanel raceLevelPanel = new JPanel();
        raceField = new JComboBox<>();
        raceLevelPanel.add(new JLabel("Race:"));
        raceLevelPanel.add(raceField);
        parent.add(raceLevelPanel);

        JPanel modifyRaceButtonPanel = new JPanel();
        JButton addResultButton = new JButton("Add Result");
        JButton deleteRaceButton = new JButton("Delete Race"); // TODO must reset fields after deleting
        modifyRaceButtonPanel.add(addResultButton);
        modifyRaceButtonPanel.add(deleteRaceButton);
        parent.add(modifyRaceButtonPanel);

        table = new ComponentTable(new String[]{"Number", "Name", "Time", "Grade", "Splits", ""});
        parent.add(table);
    }

    @Override
    public void openScreen(ScreenOpenArgs args) {
        meetId = (int) args.get("id");

        titleLabel.setText(args.get("name").toString() + " (" + args.get("year").toString() + ")");
        Course course = courseService.getCourse((int) args.get("course_id"));
        courseNameLabel.setText("Course: " + course.name());

        resetFields();
        updateTable();
    }

    private void updateTable() {
        RaceResult[] results = meetService.getResultsForRace(currentRace.id());
        ArrayList<JComponent[]> cells = new ArrayList<>();
        for (int i = 0; i < results.length; i++) {
            JComponent[] row = new JComponent[6];
            row[0] = new JLabel(Integer.toString(i + 1));
            row[1] = new JLabel(results[i].athleteName());
            row[2] = new JLabel(results[i].timeString());
            row[3] = new JLabel(Integer.toString(results[i].grade()));
            row[4] = new JLabel(results[i].splitString());

            LinkButton delButton = new LinkButton(new Color(193, 71, 71), "Delete", 12);
            delButton.addActionListener(() -> {
                // TODO: Handle
            });
            row[5] = delButton;

            cells.add(row);
        }
        table.setCells(cells);

        getPanel().repaint();
        getPanel().revalidate();
    }

    private void resetFields() {
        Race[] races = meetService.getRacesForMeet(meetId);
        raceField.setModel(new DefaultComboBoxModel<>(races));
        // If varsity or F/S option, select those. Otherwise, just let it be the first level
        boolean hasVarsity = false;
        for (int i = 0; i < races.length; i++) {
            if (races[i].raceLevel().name().equals("Varsity")) {
                hasVarsity = true;
                raceField.setSelectedIndex(i);
                break;
            }
        }
        if (!hasVarsity) {
            for (int i = 0; i < races.length; i++) {
                if (races[i].raceLevel().name().equals("F/S")) {
                    raceField.setSelectedIndex(i);
                    break;
                }
            }
        }

        // Reset the current race right now, since it must be initialized when page loads
        currentRace = (Race) raceField.getSelectedItem();

        raceField.addActionListener(e -> {
            currentRace = (Race) raceField.getSelectedItem();
            updateTable();
        });
    }
}
