package screens;

import components.NavHandler;
import databaseServices.CourseService;
import databaseServices.DBObjectToFieldsHandler;
import databaseServices.MeetService;
import databaseServices.UserService;
import dbObj.Course;
import dbObj.Meet;

import javax.swing.*;

public class MeetListScreen extends ListScreen {

    private static final int PAGE_SIZE = 10;

    private NavHandler navHandler;
    private MeetService service;

    public MeetListScreen(NavHandler handler, UserService userService, MeetService meetService) {
        super(PAGE_SIZE, handler, userService, "Meet", meetService);

        this.service = meetService;
        this.navHandler = handler;

        addOnAddHandler(() -> handler.navigate(ScreenTypes.MeetModify, new ScreenOpenArgs()));
        addGetFieldsHandler(new DBObjectToFieldsHandler() {
            @Override
            public String[] toFields(Object dbObj) {
                Meet m = (Meet) dbObj;
                return new String[] {
                        m.name(),
                        Integer.toString(m.year()),
                        m.courseName()
                };
            }
        });

        addEditHandler(this::edit);
        addDeleteHandler(this::delete);
        setOnFirstClickEvent(this::meetClicked);
    }

    @Override
    public void populatePanel() {
        super.populatePanel(new String[]{ "Name", "Year", "Course", "", "" });
    }

    private void edit(Object obj) {
        Meet m = (Meet) obj;
        ScreenOpenArgs args = new ScreenOpenArgs();
        args.add("meet_id", m.id());
        navHandler.navigate(ScreenTypes.MeetModify, args);
    }
    private void delete(Object obj) {
        Meet m = (Meet) obj;

        int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " +
                m.name() + "? THIS WILL DELETE ALL ASSOCIATED RACES AND RESULTS!", "Warning", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            service.delete(m.id());
            updateAll();
        }
    }

    private void meetClicked(Object obj) {
        Meet m = (Meet) obj;
        ScreenOpenArgs args = new ScreenOpenArgs();
        args.add("name", m.name());
        args.add("year", m.year());
        args.add("course_id", m.courseId());
        args.add("id", m.id());
        navHandler.navigate(ScreenTypes.MeetView, args);
    }
}
