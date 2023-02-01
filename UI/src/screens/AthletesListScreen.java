package screens;

import components.ComponentTable;
import components.LinkButton;
import components.NavBar;
import components.NavHandler;
import databaseServices.AthleteService;
import databaseServices.UserService;
import dbObj.Athlete;
import dbObj.Gender;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AthletesListScreen extends Screen {

    private NavHandler navHandler;
    private AthleteService athleteService;
    private UserService userService;

    private int page = 0;
    private static final int PAGE_SIZE = 10;
    private int athleteCount = 0;

    private ComponentTable table;
    private JButton nextButton;
    private JButton prevButton;

    public AthletesListScreen(AthleteService athleteService, UserService userService, NavHandler navHandler) {
        super();
        this.navHandler = navHandler;
        this.athleteService = athleteService;
        this.userService = userService;
    }

    @Override
    public void populatePanel() {
        super.createPanel();
        JPanel parent = super.getPanel();
        super.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));
        NavBar navBar = new NavBar(navHandler, userService);
        parent.add(navBar.getPanel());

        JPanel buttonRowPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addAthleteButton = new JButton("Add New Athlete");
        addAthleteButton.addActionListener(e -> {
            navHandler.navigate(ScreenTypes.AthleteModify, new ScreenOpenArgs());
        });
        buttonRowPanel.add(addAthleteButton);
        parent.add(buttonRowPanel);

        table = new ComponentTable(new String[] { "Last Name", "First Name", "Grad Year", "Gender", "", "" });
        parent.add(table);

        JPanel pageButtonPanel = new JPanel();
        nextButton = new JButton(">>");
        prevButton = new JButton("<<");
        nextButton.addActionListener(e -> nextPage());
        prevButton.addActionListener(e -> prevPage());
        pageButtonPanel.add(prevButton);
        pageButtonPanel.add(nextButton);
        parent.add(pageButtonPanel);
    }

    @Override
    public void openScreen(ScreenOpenArgs args) {
        if (args.has("page")) {
            page = (int) args.get("page");
        }
        updateAll();
    }

    private void updateAll() {
        athleteCount = athleteService.getAthleteCount();
        updateTable();
    }
    public void updateTable() {
        List<Athlete> athletes = athleteService.getAthletes(page, PAGE_SIZE);
        ArrayList<JComponent[]> rows = new ArrayList<>();
        for (Athlete a : athletes) {
            String fname = a.firstName();
            String lname = a.lastName();
            int gradYr = a.gradYear();
            Gender gender = a.gender();
            LinkButton editButton = new LinkButton(new Color(5, 138, 255), "Edit", 12);
            LinkButton deleteButton = new LinkButton(new Color(193, 71, 71), "Delete", 12);
            editButton.addActionListener(() -> {
                edit(a.id());
            });
            deleteButton.addActionListener(() -> {
                delete(a.id());
            });
            JComponent[] row = new JComponent[]{
                new JLabel(fname),
                new JLabel(lname),
                new JLabel(Integer.toString(gradYr)),
                new JLabel(gender.toString()),
                editButton,
                deleteButton
            };
            rows.add(row);
        }
        table.setCells(rows);

        prevButton.setEnabled(page > 0);
        int maxPage = (athleteCount - 1) / PAGE_SIZE;
        nextButton.setEnabled(page < maxPage);

        getPanel().repaint();
        getPanel().revalidate();
    }

    private void nextPage() {
        page++;
        updateTable();
    }
    private void prevPage() {
        page--;
        updateTable();
    }

    private void edit(int athleteId) {
        ScreenOpenArgs args = new ScreenOpenArgs();
        args.add("athlete_id", athleteId);
        navHandler.navigate(ScreenTypes.AthleteModify, args);
    }
    private void delete(int athleteId) {
        Athlete ath = athleteService.getAthlete(athleteId);

        int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " +
                ath.firstName() + " " + ath.lastName() + "?", "Warning", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            athleteService.deleteAthlete(athleteId);
            updateAll();
        }
    }

}
