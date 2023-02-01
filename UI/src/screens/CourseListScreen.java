package screens;

import components.ComponentTable;
import components.NavBar;
import components.NavHandler;
import databaseServices.CourseService;
import databaseServices.UserService;
import util.IntReturnAction;

import javax.swing.*;

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
        addGetCountHandler(() -> 100);
    }

    @Override
    protected void updateTable(ComponentTable table, int page) {

    }

    @Override
    public void populatePanel() {
        super.populatePanel(new String[]{ "Name", "", "" });
    }
}
