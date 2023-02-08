package databaseServices;

import dbObj.Race;
import dbObj.Split;
import dbObj.Time;

import javax.swing.*;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

public class RaceService {

    private DBConnectionService dbService;

    public RaceService(DBConnectionService dbService) {
        this.dbService = dbService;
    }

    public int createRace(Race race) {
        try {
            CallableStatement stmt = dbService.getConnection()
                    .prepareCall("{? = call insert_race(?, ?, ?, ?, ?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setDouble(2, race.dist().dist());
            stmt.setString(3, race.dist().units());
            stmt.setInt(4, race.raceLevel().id());
            stmt.setInt(5, race.meetId());
            stmt.setString(6, race.gender().toString());
            stmt.execute();
            int status = stmt.getInt(1);
            return status;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void deleteRace(int raceId) {
        try {
            CallableStatement stmt = dbService.getConnection().prepareCall("{? = call delete_race(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, raceId);
            stmt.execute();
            int status = stmt.getInt(1);
            if (status != 0) {
                JOptionPane.showMessageDialog(null, "There was an error deleting the race");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addResult(int raceId, int athleteId, Time time, ArrayList<Split> splits) {
        try {
            CallableStatement stmt = dbService.getConnection()
                    .prepareCall("{? = call insert_result(?, ?, ?, ?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, raceId);
            stmt.setFloat(3, time.toSeconds());
            stmt.setInt(4, athleteId);
            stmt.registerOutParameter(5, Types.INTEGER);
            stmt.execute();
            int status = stmt.getInt(1);
            int resultId = stmt.getInt(5);
            if (status != 0) {
                JOptionPane.showMessageDialog(null, "Error inserting result");
                return;
            }

            // Now insert each split
            for (int i = 0; i < splits.size(); i++) {
                Split split = splits.get(i);
                stmt = dbService.getConnection()
                        .prepareCall("{? = call insert_split(?, ?, ?, ?, ?)}");
                stmt.registerOutParameter(1, Types.INTEGER);
                stmt.setInt(2, resultId);
                stmt.setInt(3, i);
                stmt.setFloat(4, split.time().toSeconds());
                stmt.setFloat(5, (float) split.dist().dist());
                stmt.setString(6, split.dist().units());
                stmt.execute();
                status = stmt.getInt(1);
                if (status != 0) {
                    JOptionPane.showMessageDialog(null, "Error inserting result");
                    return;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
