package screens;

import components.LinkButton;
import components.NavHandler;
import databaseServices.MeetService;

import javax.swing.*;
import java.awt.*;

public class RaceCreateScreen extends Screen {

    private MeetService meetService;
    private NavHandler navHandler;

    private int meetId;
    private String meetName;
    private int meetYear;
    private int courseId;

    public RaceCreateScreen(MeetService meetService, NavHandler navHandler) {
        this.meetService = meetService;
        this.navHandler = navHandler;
    }

    @Override
    public void populatePanel() {
        super.createPanel();
        JPanel parent = super.getPanel();
        super.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));

        LinkButton backButton = new LinkButton(new Color(5, 138, 255), "<< Back", 12);
        backButton.addActionListener(() -> {
            ScreenOpenArgs args = new ScreenOpenArgs();
            args.add("id", meetId);
            args.add("name", meetName);
            args.add("year", meetYear);
            args.add("course_id", courseId);
            navHandler.navigate(ScreenTypes.MeetView, args);
        });
        parent.add(backButton);
    }

    @Override
    public void openScreen(ScreenOpenArgs args) {
        meetId = (int) args.get("meet_id");
        meetName = args.get("meet_name").toString();
        meetYear =  (int) args.get("meet_year");
        courseId = (int) args.get("course_id");


    }
}
