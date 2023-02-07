package screens;

import components.LinkButton;
import components.NavHandler;
import databaseServices.CourseService;
import databaseServices.MeetService;
import dbObj.Course;

import javax.swing.*;
import java.awt.*;

public class MeetViewScreen extends Screen {

    private MeetService meetService;
    private CourseService courseService;

    private int meetId;

    private NavHandler navHandler;

    private JLabel titleLabel;
    private JLabel courseNameLabel;

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
    }

    @Override
    public void openScreen(ScreenOpenArgs args) {
        meetId = (int) args.get("id");

        titleLabel.setText(args.get("name").toString() + " (" + args.get("year").toString() + ")");
        Course course = courseService.getCourse((int) args.get("course_id"));
        courseNameLabel.setText("Course: " + course.name());
    }
}
