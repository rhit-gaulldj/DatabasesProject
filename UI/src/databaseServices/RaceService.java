package databaseServices;

import dbObj.Race;

import javax.swing.*;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

public class RaceService {

    private DBConnectionService dbService;

    public RaceService(DBConnectionService dbService) {
        this.dbService = dbService;
    }

    public void createRace(Race race) {
        try {
            CallableStatement stmt = dbService.getConnection()
                    .prepareCall("{? = call insert_race(?, ?, ?, ?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setDouble(2, race.dist().dist());
            stmt.setString(3, race.dist().units());
            stmt.setInt(4, race.raceLevel().id());
            stmt.setInt(5, race.meetId());
            stmt.execute();
            int status = stmt.getInt(1);
            if (status != 0) {
                JOptionPane.showMessageDialog(null, "Error inserting race");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
