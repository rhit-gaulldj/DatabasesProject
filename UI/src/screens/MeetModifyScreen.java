package screens;

import components.NavHandler;
import databaseServices.CourseService;
import databaseServices.MeetService;
import dbObj.Course;
import dbObj.Meet;

import javax.swing.*;
import java.util.List;

public class MeetModifyScreen extends Screen {

    private MeetService service;
    private CourseService courseService;
    private int currentId = -1;
    private NavHandler navHandler;

    private JLabel titleLabel;
    private JTextField nameField;
    private JSpinner yearField;
    private JComboBox<Course> courseField;
    private JButton submitButton;

    public MeetModifyScreen(MeetService service, CourseService courseService,
                            NavHandler navHandler) {
        this.service = service;
        this.courseService = courseService;
        this.navHandler = navHandler;
    }

    @Override
    public void populatePanel() {
        super.createPanel(5, 2);
        JPanel form = getPanel();

        titleLabel = new JLabel("TITLE");
        form.add(titleLabel);
        form.add(new JLabel(""));

        nameField = new JTextField();
        form.add(new JLabel("Name: "));
        form.add(nameField);

        yearField = new JSpinner(new SpinnerNumberModel(2020, 1900, 3000, 1));
        yearField.setEditor(new JSpinner.NumberEditor(yearField, "#"));
        form.add(new JLabel("Year:"));
        form.add(yearField);

        List<Course> courseList = courseService.getAll().stream().map(x -> (Course) x).toList();
        Course[] courses = new Course[courseList.size()];
        courseList.toArray(courses);
        courseField = new JComboBox<>(courses);
        form.add(new JLabel("Course:"));
        form.add(courseField);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            navHandler.navigate(ScreenTypes.MeetList, new ScreenOpenArgs());
        });
        submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> submit());
        form.add(cancelButton);
        form.add(submitButton);
    }

    @Override
    public void openScreen(ScreenOpenArgs args) {
        if (args.has("meet_id")) {
            currentId = (int) args.get("meet_id");
            titleLabel.setText("Editing Meet");
            submitButton.setText("Update");
            populateFields();
        } else {
            currentId = -1;
            titleLabel.setText("Creating Meet");
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

        int year = (int) yearField.getValue();

        // When creating/updating meets, the name doesn't matter, only the ID
        Course selectedCourse = (Course) courseField.getSelectedItem();
        if (currentId < 0) {
            Meet m = new Meet(-1, name, year, selectedCourse.id(), "");
            service.insert(m);

            ScreenOpenArgs args = new ScreenOpenArgs();
            args.add("page", 0);
            navHandler.navigate(ScreenTypes.MeetList, args);
        } else {
            Meet m = new Meet(currentId, name, year, selectedCourse.id(), "");
            service.update(currentId, m);
            navHandler.navigate(ScreenTypes.MeetList, new ScreenOpenArgs());
        }
    }

    private void populateFields() {
        Meet m = service.getObj(currentId);
        nameField.setText(m.name());
        yearField.setValue(m.year());
        courseField.setSelectedItem(courseService.getCourse(m.courseId()));
    }
}
