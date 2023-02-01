package screens;

import components.NavBar;
import components.NavHandler;
import databaseServices.CourseService;
import databaseServices.UserService;

import javax.swing.*;

public class CourseListScreen extends Screen {

    private UserService userService;
    private CourseService courseService;
    private NavHandler handler;

    // TODO: Refactor into list screen class
    private int page;

    public CourseListScreen(UserService userService, CourseService courseService, NavHandler handler) {
        this.userService = userService;
        this.courseService = courseService;
        this.handler = handler;
    }

    @Override
    public void populatePanel() {
        super.createPanel();
        JPanel parent = super.getPanel();
        super.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));

        parent.add(new NavBar(handler, userService).getPanel());
    }

    @Override
    public void openScreen(ScreenOpenArgs args) {

    }
}
