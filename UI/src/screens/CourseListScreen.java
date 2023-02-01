package screens;

import components.ComponentTable;
import components.LinkButton;
import components.NavBar;
import components.NavHandler;
import databaseServices.CourseService;
import databaseServices.UserService;
import dbObj.Athlete;
import dbObj.Course;
import dbObj.Gender;
import util.IntReturnAction;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CourseListScreen extends ListScreen {

    private CourseService courseService;
    private NavHandler handler;

    private static final int PAGE_SIZE = 10;

    public CourseListScreen(NavHandler handler, UserService userService, CourseService courseService) {
        super(PAGE_SIZE, handler, userService, "Course");

        this.courseService = courseService;
        this.handler = handler;

        // TODO: Add on add handler
        // TODO: Add get count handler
        addGetCountHandler(() -> courseService.getCourseCount());
    }

    @Override
    protected void updateTable(ComponentTable table, int page) {
        List<Course> courses = courseService.getCourses(page, PAGE_SIZE);
        ArrayList<JComponent[]> rows = new ArrayList<>();
        for (Course c : courses) {
            String name = c.name();
            LinkButton editButton = new LinkButton(new Color(5, 138, 255), "Edit", 12);
            LinkButton deleteButton = new LinkButton(new Color(193, 71, 71), "Delete", 12);
            editButton.addActionListener(() -> {
                //edit(a.id());
            });
            deleteButton.addActionListener(() -> {
                //delete(a.id());
            });
            JComponent[] row = new JComponent[]{
                    new JLabel(name),
                    editButton,
                    deleteButton
            };
            rows.add(row);
        }
        table.setCells(rows);
    }

    @Override
    public void populatePanel() {
        super.populatePanel(new String[]{ "Name", "", "" });
    }
}
