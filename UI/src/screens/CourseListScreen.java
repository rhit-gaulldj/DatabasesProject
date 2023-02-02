package screens;

import components.NavHandler;
import databaseServices.CourseService;
import databaseServices.DBObjectToFieldsHandler;
import databaseServices.UserService;
import dbObj.Athlete;
import dbObj.Course;

import javax.swing.*;

public class CourseListScreen extends ListScreen {

    private static final int PAGE_SIZE = 10;
    private NavHandler navHandler;
    private CourseService service;

    public CourseListScreen(NavHandler handler, UserService userService, CourseService courseService) {
        super(PAGE_SIZE, handler, userService, "Course", courseService);

        this.navHandler = handler;
        this.service = courseService;

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

        addEditHandler(this::edit);
        addDeleteHandler(this::delete);
    }

    @Override
    public void populatePanel() {
        super.populatePanel(new String[]{ "Name", "", "" });
    }

    private void edit(Object obj) {
        Course c = (Course) obj;
        ScreenOpenArgs args = new ScreenOpenArgs();
        args.add("course_id", c.id());
        navHandler.navigate(ScreenTypes.CourseModify, args);
    }
    private void delete(Object obj) {
        Course c = (Course) obj;

        int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " +
                c.name() + "?", "Warning", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            //athleteService.deleteAthlete(ath.id());
            updateAll();
        }
    }
}
