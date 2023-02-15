package screens;

import components.*;
import databaseServices.AthleteService;
import databaseServices.UserService;
import dbObj.Athlete;
import dbObj.Gender;
import dbObj.SearchResult;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RosterScreen extends Screen {

    private AthleteService athleteService;
    private UserService userService;

    private NavHandler navHandler;

    private static final int SCROLL_PANE_HEIGHT = 450; // Simply hardcoded
    private JComboBox<Integer> yearField;
    private JComboBox<Gender> genderField;
    private ComponentTable table;
    private JScrollPane tablePane;

    public RosterScreen(AthleteService athleteService, UserService userService, NavHandler navHandler) {
        this.athleteService = athleteService;
        this.userService = userService;

        this.navHandler = navHandler;
    }

    @Override
    public void populatePanel() {
        super.createPanel();
        JPanel parent = super.getPanel();
        super.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));

        NavBar navBar = new NavBar(navHandler, userService);
        parent.add(navBar.getPanel());

        JPanel titleRow = new JPanel();
        JLabel titleLabel = new JLabel("Roster");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleRow.add(titleLabel);
        parent.add(titleRow);

        JPanel yearRow = new JPanel();
        yearField = new JComboBox<>();
        yearField.addActionListener(e -> updateTable());
        yearRow.add(new JLabel("Year:"));
        yearRow.add(yearField);
        parent.add(yearRow);

        JPanel genderRow = new JPanel();
        genderField = new JComboBox<>(new Gender[]{Gender.MALE, Gender.FEMALE, Gender.OTHER});
        genderField.addActionListener(e -> updateTable());
        genderRow.add(new JLabel("Gender:"));
        genderRow.add(genderField);
        parent.add(genderRow);

        tablePane = new JScrollPane();
        table = new ComponentTable(new String[]{"Name", "Grade"});
        tablePane.setViewportView(table);
        parent.add(tablePane);
    }

    @Override
    public void openScreen(ScreenOpenArgs args) {
        ArrayList<Integer> years = athleteService.getRosterYears();
        Integer[] yearArray = new Integer[years.size()];
        years.toArray(yearArray);
        yearField.setModel(new DefaultComboBoxModel<>(yearArray));
        yearField.setSelectedIndex(yearArray.length - 1);

        updateTable();
    }

    private void updateTable() {
        ArrayList<JComponent[]> cells = new ArrayList<>();

        if (yearField.getSelectedItem() != null && genderField.getSelectedItem() != null) {
            int year = (int) yearField.getSelectedItem();
            List<Athlete> results = athleteService.getRoster(year,
                    (Gender) genderField.getSelectedItem());
            for (Athlete ath : results) {
                LinkButton nameButton = new LinkButton(new Color(5, 138, 255),
                        ath.firstName() + " " + ath.lastName(), 12);
                nameButton.addActionListener(() -> {
                    ScreenOpenArgs args = new ScreenOpenArgs();
                    args.add("id", ath.id());
                    navHandler.navigate(ScreenTypes.AthleteView, args);
                });
                cells.add(new JComponent[]{
                        nameButton,
                        new JLabel(Integer.toString(year - ath.gradYear() + 13))
                });
            }
            table.setCells(cells);
        }

        getPanel().repaint();
        getPanel().revalidate();

        tablePane.setPreferredSize(new Dimension((int) table.getPreferredSize().getWidth() + 20,
                SCROLL_PANE_HEIGHT));
    }
}
