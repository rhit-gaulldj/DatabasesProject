package screens;

import components.NavBar;
import components.NavHandler;

import javax.swing.*;

public class AthletesListScreen extends Screen {

    private final NavHandler navHandler;

    public AthletesListScreen(NavHandler navHandler) {
        super();
        this.navHandler = navHandler;
    }

    @Override
    public void populatePanel() {
        super.createPanel(1, 1);
        JPanel parent = super.getPanel();
        NavBar navBar = new NavBar(navHandler);
        parent.add(navBar.getPanel());

        // TODO: ... Do stuff here...
    }
}
