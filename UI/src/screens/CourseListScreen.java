package screens;

import components.ComponentTable;
import components.LinkButton;
import components.NavBar;
import components.NavHandler;
import databaseServices.AthleteService;
import databaseServices.CourseService;
import databaseServices.DBObjectToFieldsHandler;
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

    private static final int PAGE_SIZE = 10;

    public CourseListScreen(NavHandler handler, UserService userService, CourseService courseService) {
        super(PAGE_SIZE, handler, userService, "Course", courseService);

        addOnAddHandler(() -> handler.navigate(ScreenTypes.CourseModify, new ScreenOpenArgs()));
        addGetFieldsHandler(new DBObjectToFieldsHandler() {
            @Override
            public String[] toFields(Object dbObj) {
                Course c = (Course) dbObj;
                return new String[] {
                        c.name()
                };
            }
        });
    }

    @Override
    public void populatePanel() {
        super.populatePanel(new String[]{ "Name", "", "" });
    }
}
