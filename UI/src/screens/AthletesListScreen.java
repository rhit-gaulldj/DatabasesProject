package screens;

import components.ComponentTable;
import components.NavBar;
import components.NavHandler;
import databaseServices.AthleteService;
import dbObj.Athlete;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AthletesListScreen extends Screen {

    private NavHandler navHandler;
    private AthleteService athleteService;

    // TODO: Add paging buttons
    // TODO: Add detecting if there's another page (have na output param for total length)
    private int page = 0;
    private static final int PAGE_SIZE = 10;

    private ComponentTable table;

    public AthletesListScreen(AthleteService athleteService, NavHandler navHandler) {
        super();
        this.navHandler = navHandler;
        this.athleteService = athleteService;
    }

    @Override
    public void populatePanel() {
        super.createPanel();
        JPanel parent = super.getPanel();
        super.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));
        NavBar navBar = new NavBar(navHandler);
        parent.add(navBar.getPanel());

        JPanel buttonRowPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addAthleteButton = new JButton("Add New Athlete");
        buttonRowPanel.add(addAthleteButton);
        parent.add(buttonRowPanel);

        table = new ComponentTable(new String[] { "Last Name", "First Name", "Grad Year", "Gender" });
        parent.add(table);
    }

    @Override
    public void openScreen() {
        updateTable();
    }

    public void updateTable() {
        // TODO: Add paging
        List<Athlete> athletes = athleteService.getAthletes(page, PAGE_SIZE);
        ArrayList<JComponent[]> rows = new ArrayList<>();
        for (Athlete a : athletes) {
            String fname = a.firstName();
            String lname = a.lastName();
            int gradYr = a.gradYear();
            String gender = a.gender();
            JComponent[] row = new JComponent[]{
                new JLabel(fname),
                new JLabel(lname),
                new JLabel(Integer.toString(gradYr)),
                new JLabel(gender)
            };
            rows.add(row);
        }
        table.setCells(rows);
    }

}
