package screens;

import components.LinkButton;
import components.NavHandler;
import databaseServices.CourseService;
import databaseServices.MeetService;
import dbObj.Course;
import dbObj.Race;
import dbObj.RaceLevel;

import javax.swing.*;
import java.awt.*;

public class MeetViewScreen extends Screen {

    private MeetService meetService;
    private CourseService courseService;

    private int meetId;

    private NavHandler navHandler;

    private JLabel titleLabel;
    private JLabel courseNameLabel;
    private JComboBox<Race> raceField;

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
    }

    @Override
    public void openScreen(ScreenOpenArgs args) {
        meetId = (int) args.get("id");

        titleLabel.setText(args.get("name").toString() + " (" + args.get("year").toString() + ")");
        Course course = courseService.getCourse((int) args.get("course_id"));
        courseNameLabel.setText("Course: " + course.name());

        resetFields();
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
    }
}
