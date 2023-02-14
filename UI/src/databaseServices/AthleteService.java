package databaseServices;

import dbObj.*;

import javax.swing.*;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class AthleteService extends AbstractDBService {

    private DBConnectionService dbService;

    public AthleteService(DBConnectionService dbService) {
        super(dbService, (rs) -> {
            return new Athlete(rs.getInt(1), rs.getString(2), rs.getString(3),
                    Gender.fromString(rs.getString(5)), rs.getInt(4));
        }, "get_athletes", "get_athlete_count", "TODO");
        this.dbService = dbService;
    }

    public void insertAthlete(Athlete newAthlete) {
        try {
            CallableStatement stmt = dbService.getConnection()
                    .prepareCall("{? = call insert_athlete(?, ?, ?, ?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, newAthlete.firstName());
            stmt.setString(3, newAthlete.lastName());
            stmt.setInt(4, newAthlete.gradYear());
            stmt.setString(5, newAthlete.gender().toString());
            stmt.execute();
            int status = stmt.getInt(1);
            if (status == 1) {
                JOptionPane.showMessageDialog(null, "All fields must be entered");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Athlete getAthlete(int id) {
        try {
            CallableStatement stmt = dbService.getConnection()
                    .prepareCall("{? = call get_athlete(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Athlete(rs.getInt(1), rs.getString(2), rs.getString(3),
                        Gender.fromString(rs.getString(5)), rs.getInt(4));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateAthlete(int athleteId, Athlete ath) {
        try {
            CallableStatement stmt = dbService.getConnection().prepareCall("{? = call update_athlete(?, ?, ?, ?, ?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, athleteId);
            stmt.setString(3, ath.firstName());
            stmt.setString(4, ath.lastName());
            stmt.setInt(5, ath.gradYear());
            stmt.setString(6, ath.gender().toString());
            stmt.execute();
            int status = stmt.getInt(1);
            if (status != 0) {
                JOptionPane.showMessageDialog(null, "Error updating athlete");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAthlete(int athleteId) {
        try {
            CallableStatement stmt = dbService.getConnection().prepareCall("{? = call delete_athlete(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, athleteId);
            stmt.execute();
            int status = stmt.getInt(1);
            if (status != 0) {
                JOptionPane.showMessageDialog(null, "Error deleting athlete");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Athlete[] getAthletesNotInRace(int raceId) {
        try {
            CallableStatement stmt = dbService.getConnection()
                    .prepareCall("{? = call get_athletes_not_in_race(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, raceId);
            ResultSet rs = stmt.executeQuery();
            List<Athlete> result = new ArrayList<>();
            while (rs.next()) {
                result.add(new Athlete(rs.getInt(1), rs.getString(2), rs.getString(3),
                        Gender.fromString(rs.getString(5)), rs.getInt(4)));
            }
            Athlete[] arr = new Athlete[result.size()];
            result.toArray(arr);
            return arr;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public ResultSet getPbs(int athleteID) {
        try {
            CallableStatement stmt = dbService.getConnection()
                    .prepareCall("{? = call get_athlete_pbs(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, athleteID);
            return stmt.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPredictedTime(int athleteID,float distance, String unit) {
        try {
            CallableStatement stmt = dbService.getConnection()
                    .prepareCall("{? = call predict_athlete_times(?,?,?,?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, athleteID);
            stmt.setFloat(3, distance);
            stmt.setString(4, unit);
            stmt.registerOutParameter(5, Types.VARCHAR);
            try{
            stmt.execute();
            }
            catch(SQLException e){
                return "Athlete has no data";
            }
            return "Predicted "+distance+unit+" Time: ["+stmt.getString(5)+"]";

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Integer> getRosterYears() {
        try {
            CallableStatement stmt = dbService.getConnection().prepareCall("{? = call get_roster_years()}");
            stmt.registerOutParameter(1, Types.INTEGER);
            ResultSet rs = stmt.executeQuery();
            ArrayList<Integer> results = new ArrayList<>();
            while (rs.next()) {
                results.add(rs.getInt(1));
            }
            return results;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ArrayList<Athlete> getRoster(int year, Gender gender) {
        try {
            CallableStatement stmt = dbService.getConnection().prepareCall("{? = call get_roster(?, ?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, year);
            stmt.setString(3, gender.toString());
            ResultSet rs = stmt.executeQuery();
            ArrayList<Athlete> athletes = new ArrayList<>();
            while (rs.next()) {
                athletes.add(new Athlete(rs.getInt(1), rs.getString(2), rs.getString(3),
                        gender, rs.getInt(4)));
            }
            return athletes;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
