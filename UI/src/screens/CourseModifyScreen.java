package screens;

import components.NavHandler;
import databaseServices.CourseService;
import dbObj.Course;

import javax.swing.*;

public class CourseModifyScreen extends Screen {

    private CourseService courseService;
    private int currentCourseId = -1;
    private NavHandler navHandler;

    private JLabel titleLabel;
    private JTextField nameField;
    private JButton submitButton;

    public CourseModifyScreen(CourseService courseService, NavHandler navHandler) {
        this.courseService = courseService;
        this.navHandler = navHandler;
    }


    @Override
    public void populatePanel() {
        super.createPanel(3, 2);
        JPanel form = getPanel();

        titleLabel = new JLabel("TITLE");
        form.add(titleLabel);
        form.add(new JLabel(""));

        nameField = new JTextField();
        form.add(new JLabel("Name: "));
        form.add(nameField);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            navHandler.navigate(ScreenTypes.CourseList, new ScreenOpenArgs());
        });
        submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> submit());
        form.add(cancelButton);
        form.add(submitButton);
    }

    @Override
    public void openScreen(ScreenOpenArgs args) {
        if (args.has("course_id")) {
            currentCourseId = (int) args.get("course_id");
            titleLabel.setText("Editing Course");
            submitButton.setText("Update");
            populateFields();
        } else {
            currentCourseId = -1;
            titleLabel.setText("Creating Course");
            submitButton.setText("Create");
            nameField.setText("");
        }
    }

    private void submit() {
        String name = nameField.getText().trim();
        if (name.length() <= 0) {
            JOptionPane.showMessageDialog(null, "Name is required");
            return;
        }

        if (currentCourseId < 0) {
            Course c = new Course(-1, name);
            courseService.insertCourse(c);

            ScreenOpenArgs args = new ScreenOpenArgs();
            args.add("page", 0);
            navHandler.navigate(ScreenTypes.CourseList, args);
        } else {
            Course c = new Course(currentCourseId, name);
            courseService.updateCourse(currentCourseId, c);
            navHandler.navigate(ScreenTypes.CourseList, new ScreenOpenArgs());
        }
    }

    private void populateFields() {
        Course c = courseService.getCourse(currentCourseId);
        nameField.setText(c.name());
    }
}
