package screens;

import components.ComponentTable;
import components.LinkButton;
import components.NavHandler;
import databaseServices.AthleteService;
import databaseServices.DBObjectToFieldsHandler;
import databaseServices.MeetService;
import databaseServices.UserService;
import dbObj.Course;
import dbObj.Meet;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MeetListScreen extends ListScreen {

    private static final int PAGE_SIZE = 10;

    public MeetListScreen(NavHandler handler, UserService userService, MeetService meetService) {
        super(PAGE_SIZE, handler, userService, "Meet", new AthleteService(null));

        addOnAddHandler(() -> handler.navigate(ScreenTypes.MeetModify, new ScreenOpenArgs()));
        addGetFieldsHandler(new DBObjectToFieldsHandler() {
            @Override
            public String[] toFields(Object dbObj) {
                Meet m = (Meet) dbObj;
                return new String[] {
                        m.name(),
                        Integer.toString(m.year())
                };
            }
        });
    }

    @Override
    public void populatePanel() {
        super.populatePanel(new String[]{ "Name", "Year", "", "" });
    }
}
