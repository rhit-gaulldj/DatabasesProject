package screens;

import components.ComponentTable;
import components.LinkButton;
import components.NavHandler;
import databaseServices.AthleteService;
import dbObj.Athlete;
import dbObj.Course;
import dbObj.DistancePair;
import dbObj.RaceLevel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AthleteViewScreen extends Screen {

    private NavHandler navHandler;
    private AthleteService athleteService;

    private int athleteId;

    private JLabel nameLabel;
    private JLabel prediction;
    private JSpinner distanceField;
    private JComboBox<String> unitsField;

    private static final int NUM_FIELDS = 13;

    private ComponentTable table;
    private JScrollPane tableScrollPane;
    private int scrollPaneHeight;

    public AthleteViewScreen(AthleteService athleteService, NavHandler navHandler) {
        this.navHandler = navHandler;
        this.athleteService = athleteService;
    }

    @Override
    public void populatePanel() {
        super.createPanel();
        JPanel parent = super.getPanel();
        super.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));

        LinkButton backButton = new LinkButton(new Color(5, 138, 255), "<< Back", 12);
        backButton.addActionListener(() -> navHandler.navigate(ScreenTypes.AthletesList, new ScreenOpenArgs()));
        parent.add(backButton);

        JPanel nameContainer = new JPanel();
        nameLabel = new JLabel("");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameContainer.add(nameLabel);
        parent.add(nameContainer);

        JPanel distancePanel = new JPanel();
        distanceField = new JSpinner(new SpinnerNumberModel(3, 1, 25, 1));
        distanceField.setEditor(new JSpinner.NumberEditor(distanceField, "#"));
        distancePanel.add(new JLabel("Distance:"));
        distancePanel.add(distanceField);
        parent.add(distancePanel);

        JPanel unitsPanel = new JPanel();
        unitsField = new JComboBox(new String[]{"mi", "km"});
        unitsPanel.add(new JLabel("Units:"));
        unitsPanel.add(unitsField);
        parent.add(unitsPanel);


        prediction = new JLabel("---");
        prediction.setAlignmentX(Component.CENTER_ALIGNMENT);
        prediction.setHorizontalAlignment(JLabel.CENTER);
        parent.add(prediction);

        JPanel predictButtonContainer = new JPanel();
        JButton predictButton = new JButton("Show Prediction");
        predictButton.addActionListener(e -> showPredictedTime());
        predictButtonContainer.add(predictButton);
        parent.add(predictButtonContainer);

        table = new ComponentTable(new String[] { "Number","ID", "First Name", "Last Name", "Grad Year", "Gender", "Meet Name", "Year","Course Name","Distance","Distance Unit","Best Time","Per Mile","Splits" });
        tableScrollPane = new JScrollPane();
        tableScrollPane.setViewportView(table);
        parent.add(tableScrollPane);
    }

    @Override
    public void openScreen(ScreenOpenArgs args) {
        this.athleteId = (int) args.get("id");

        Athlete ath = athleteService.getAthlete(athleteId);
        nameLabel.setText(ath.firstName() + " " + ath.lastName());

        resetFields();

        // Populate with initial results
        showResults();

        scrollPaneHeight = (int) table.getPreferredSize().getHeight() + 15;
        updateScrollPane();
    }

    private void showResults() {
        /*
        DistancePair dist = (DistancePair) distanceField.getSelectedItem();
        RaceLevel rl = (RaceLevel) raceLevelField.getSelectedItem();
        */
        ResultSet rs = athleteService.getPbs(athleteId);

        ArrayList<JComponent[]> rows = new ArrayList<>();
        try {
            int count = 1;
            while (rs.next()) {
                JComponent[] row = new JComponent[NUM_FIELDS + 1];
                for (int i = 1; i < NUM_FIELDS + 1; i++) {
                    row[i] = new JLabel(rs.getString(i));
                }
                row[0] = new JLabel(Integer.toString(count++));
                rows.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        table.setCells(rows);

        updateScrollPane();

        getPanel().repaint();
        getPanel().revalidate();
    }

    private void showPredictedTime() {
        JPanel parent = super.getPanel();
        String predictedTime = athleteService.getPredictedTime(athleteId,(int)distanceField.getValue(),(String) unitsField.getSelectedItem());
        prediction.setText(predictedTime);
        prediction.revalidate();
        updateScrollPane();
        getPanel().repaint();
        getPanel().revalidate();
    }

    private void updateScrollPane() {
        tableScrollPane.setPreferredSize(new Dimension((int) table.getPreferredSize().getWidth() + 20,
                scrollPaneHeight));
    }

    private void resetFields() {
        distanceField.setValue(3);
        unitsField.setSelectedItem("mi");
        prediction.setText("---");
    }

}

