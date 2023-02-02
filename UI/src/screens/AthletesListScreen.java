package screens;

import components.NavHandler;
import databaseServices.AthleteService;
import databaseServices.DBObjectToFieldsHandler;
import databaseServices.UserService;
import dbObj.Athlete;

import javax.swing.*;

public class AthletesListScreen extends ListScreen {

    private NavHandler navHandler;
    private AthleteService athleteService;
    private static final int PAGE_SIZE = 10;

    public AthletesListScreen(AthleteService athleteService, UserService userService, NavHandler navHandler) {
        super(PAGE_SIZE, navHandler, userService, "Athlete", athleteService);
        this.navHandler = navHandler;
        this.athleteService = athleteService;

        addOnAddHandler(() -> navHandler.navigate(ScreenTypes.AthleteModify, new ScreenOpenArgs()));
        addGetFieldsHandler(new DBObjectToFieldsHandler() {
            @Override
            public String[] toFields(Object dbObj) {
                Athlete a = (Athlete) dbObj;
                return new String[] {
                        a.lastName(),
                        a.firstName(),
                        Integer.toString(a.gradYear()),
                        a.gender().toLongString()
                };
            }
        });
    }

    @Override
    public void populatePanel() {
        super.populatePanel(new String[]{ "Last Name", "First Name", "Grad Year", "Gender", "", "" });
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
