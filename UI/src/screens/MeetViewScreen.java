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
    private String meetName;
    private int meetYear;
    private int courseId;
    private Race currentRace;

    private NavHandler navHandler;

    private JLabel titleLabel;
    private JLabel courseNameLabel;
    private JComboBox<Race> raceField;
    private JButton newResultButton;
    private JButton deleteRaceButton;
    private JButton addNewRaceButton;

    private ComponentTable table;
    private JScrollPane tableScrollPane;
    private static final int SCROLL_PANE_HEIGHT = 450; // Simply hardcoded

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
        addNewRaceButton = new JButton("Add New Race");
        addNewRaceButton.addActionListener(e -> {
            ScreenOpenArgs args = new ScreenOpenArgs();
            args.add("meet_id", meetId);
            args.add("meet_name", meetName);
            args.add("meet_year", meetYear);
            args.add("course_id", courseId);
            navHandler.navigate(ScreenTypes.RaceCreate, args);
        });
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
        newResultButton = new JButton("Add Result");
        deleteRaceButton = new JButton("Delete Race"); // TODO must reset fields after deleting
        modifyRaceButtonPanel.add(newResultButton);
        modifyRaceButtonPanel.add(deleteRaceButton);
        parent.add(modifyRaceButtonPanel);

        table = new ComponentTable(new String[]{"Number", "Name", "Time", "Grade", "Splits", ""});
        tableScrollPane = new JScrollPane();
        tableScrollPane.setViewportView(table);
        parent.add(tableScrollPane);
    }

    @Override
    public void openScreen(ScreenOpenArgs args) {
        meetId = (int) args.get("id");
        meetName = args.get("name").toString();
        meetYear =  (int) args.get("year");
        courseId = (int) args.get("course_id");

        titleLabel.setText(meetName + " (" + meetYear + ")");
        Course course = courseService.getCourse(courseId);
        courseNameLabel.setText("Course: " + course.name());

        RaceLevel[] availableLevels = meetService.getUnusedLevelForMeet(meetId);
        // If length is 0, then cannot create any new races
        addNewRaceButton.setEnabled(availableLevels.length > 0);

        resetFields();
        updateTable();
    }

    private void updateTable() {
        ArrayList<JComponent[]> cells = new ArrayList<>();
        if (currentRace != null) {
            RaceResult[] results = meetService.getResultsForRace(currentRace.id());
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
        }
        table.setCells(cells);

        newResultButton.setEnabled(currentRace != null);
        deleteRaceButton.setEnabled(currentRace != null);

        getPanel().repaint();
        getPanel().revalidate();

        tableScrollPane.setPreferredSize(new Dimension((int) table.getPreferredSize().getWidth() + 20,
                SCROLL_PANE_HEIGHT));
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
