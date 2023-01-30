package screens;

import components.DataRow;
import components.NavBar;
import components.NavHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.Vector;

public class AthletesListScreen extends Screen {

    private final NavHandler navHandler;

    public AthletesListScreen(NavHandler navHandler) {
        super();
        this.navHandler = navHandler;

        String[] colNames = new String[]{
            "Last Name", "First Name", "Graduation Year"
        };
        Object[][] data = new Object[][]{
                { "Smith", "testname", 2025 }
        };
    }

    @Override
    public void populatePanel() {
        super.createPanel(4, 1);
        JPanel parent = super.getPanel();
        NavBar navBar = new NavBar(navHandler);
        parent.add(navBar.getPanel());

        JPanel buttonRowPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addAthleteButton = new JButton("Add New Athlete");
        buttonRowPanel.add(addAthleteButton);
        parent.add(buttonRowPanel);

        JPanel table = new JPanel();
        DataRow row = new DataRow();
        row.setRow(new String[] { "LName", "FName", "2020", "M" });
        table.add(row.getPanel());
        parent.add(table);

        // TODO: ... Do stuff here...
    }


}
