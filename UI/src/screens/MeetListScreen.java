package screens;

import components.ComponentTable;
import components.LinkButton;
import components.NavHandler;
import databaseServices.MeetService;
import databaseServices.UserService;
import dbObj.Course;
import dbObj.Meet;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MeetListScreen extends ListScreen {

    private MeetService meetService;

    private static final int PAGE_SIZE = 10;

    public MeetListScreen(NavHandler handler, UserService userService, MeetService meetService) {
        super(PAGE_SIZE, handler, userService, "Meet");

        this.meetService = meetService;

        addOnAddHandler(() -> handler.navigate(ScreenTypes.MeetModify, new ScreenOpenArgs()));
        addGetCountHandler(() -> meetService.getMeetCount());
    }

    @Override
    protected void updateTable(ComponentTable table, int page) {
        List<Meet> meets = meetService.getMeets(page, PAGE_SIZE);
        ArrayList<JComponent[]> rows = new ArrayList<>();
        for (Meet m : meets) {
            String name = m.name();
            int year = m.year();
            LinkButton editButton = new LinkButton(new Color(5, 138, 255), "Edit", 12);
            LinkButton deleteButton = new LinkButton(new Color(193, 71, 71), "Delete", 12);
            editButton.addActionListener(() -> {
                // TODO: Write edit & delete
                //edit(a.id());
            });
            deleteButton.addActionListener(() -> {
                //delete(a.id());
            });
            JComponent[] row = new JComponent[]{
                    new JLabel(name),
                    new JLabel(Integer.toString(year)),
                    editButton,
                    deleteButton
            };
            rows.add(row);
        }
        table.setCells(rows);
    }

    @Override
    public void populatePanel() {
        super.populatePanel(new String[]{ "Name", "Year", "", "" });
    }
}
