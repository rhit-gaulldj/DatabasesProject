package screens;

import components.LinkButton;
import components.NavHandler;
import databaseServices.MeetService;
import databaseServices.RaceService;
import dbObj.DistancePair;
import dbObj.Race;
import dbObj.RaceLevel;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RaceCreateScreen extends Screen {

    private MeetService meetService;
    private RaceService raceService;
    private NavHandler navHandler;

    private int meetId;
    private String meetName;
    private int meetYear;
    private int courseId;

    private JLabel titleLabel;
    private JTextField distanceField;
    private JComboBox<String> distanceUnitField;
    private JComboBox<RaceLevel> raceLevelField;

    private static final String[] UNITS = new String[]{ "mi", "km", "m" };

    public RaceCreateScreen(MeetService meetService, RaceService raceService, NavHandler navHandler) {
        this.meetService = meetService;
        this.raceService = raceService;
        this.navHandler = navHandler;
    }

    @Override
    public void populatePanel() {
        super.createPanel();
        JPanel parent = super.getPanel();
        super.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));

        LinkButton backButton = new LinkButton(new Color(5, 138, 255), "<< Back", 12);
        backButton.addActionListener(this::goBack);
        parent.add(backButton);

        titleLabel = new JLabel("TITLE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        parent.add(titleLabel);

        JPanel distanceRow = new JPanel();
        distanceField = new JTextField(10);
        // Document filter forces distance field to only allow numbers
        ((AbstractDocument)distanceField.getDocument()).setDocumentFilter(new DocumentFilter(){
            final Pattern regEx = Pattern.compile("^\\d*[.]?\\d*$");
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                Matcher matcher = regEx.matcher(text);
                if(!matcher.matches()){
                    return;
                }
                super.replace(fb, offset, length, text, attrs);
            }
        });
        distanceUnitField = new JComboBox<>(UNITS);
        distanceRow.add(new JLabel("Distance: "));
        distanceRow.add(distanceField);
        distanceRow.add(distanceUnitField);
        parent.add(distanceRow);

        JPanel levelRow = new JPanel();
        raceLevelField = new JComboBox<>();
        levelRow.add(new JLabel("Level: "));
        levelRow.add(raceLevelField);
        parent.add(levelRow);

        // Throw into a container so that it's centered
        JPanel submitPanel = new JPanel();
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> submit());
        submitPanel.add(submitButton);
        parent.add(submitPanel);
    }

    @Override
    public void openScreen(ScreenOpenArgs args) {
        meetId = (int) args.get("meet_id");
        meetName = args.get("meet_name").toString();
        meetYear =  (int) args.get("meet_year");
        courseId = (int) args.get("course_id");

        titleLabel.setText("Create New Race for " + meetName + " (" + meetYear + ")");

        RaceLevel[] levels = meetService.getUnusedLevelForMeet(meetId);
        DefaultComboBoxModel<RaceLevel> model = new DefaultComboBoxModel<>(levels);
        raceLevelField.setModel(model);

        resetFields();
    }

    private void submit() {
        String distText = distanceField.getText();
        if (distText.length() <= 0) {
            JOptionPane.showMessageDialog(null, "Race distance is required.");
            return;
        }
        float distance = Float.parseFloat(distText);
        if (distance <= 0) {
            JOptionPane.showMessageDialog(null, "Race distance must be greater than 0.");
            return;
        }

        String distanceUnit = distanceUnitField.getSelectedItem().toString();
        RaceLevel level = (RaceLevel) raceLevelField.getSelectedItem();
        Race newRace = new Race(-1, new DistancePair(distance, distanceUnit), level, meetId);
        raceService.createRace(newRace);

        goBack();
    }

    private void goBack() {
        ScreenOpenArgs args = new ScreenOpenArgs();
        args.add("id", meetId);
        args.add("name", meetName);
        args.add("year", meetYear);
        args.add("course_id", courseId);
        navHandler.navigate(ScreenTypes.MeetView, args);
    }

    private void resetFields() {
        distanceField.setText("");
        distanceUnitField.setSelectedIndex(0);
    }

}
