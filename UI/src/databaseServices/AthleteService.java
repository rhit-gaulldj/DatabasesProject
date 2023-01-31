package databaseServices;

import dbObj.Athlete;
import dbObj.Gender;

import javax.swing.*;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AthleteService {

    private DBConnectionService dbService;

    public AthleteService(DBConnectionService dbService) {
        this.dbService = dbService;
    }

    public List<Athlete> getAthletes(int page, int pageSize) {
        try {
            CallableStatement stmt = dbService.getConnection().prepareCall("{? = call get_athletes(?, ?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, page);
            stmt.setInt(3, pageSize);
            ResultSet rs = stmt.executeQuery();
            ArrayList<Athlete> athletes = new ArrayList<>();
            while (rs.next()) {
                Athlete a = new Athlete(rs.getInt(1), rs.getString(2), rs.getString(3),
                        Gender.fromString(rs.getString(5)), rs.getInt(4));
                athletes.add(a);
            }
            return athletes;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getAthleteCount() {
        try {
            CallableStatement stmt = dbService.getConnection().prepareCall("{? = call get_athlete_count}");
            stmt.registerOutParameter(1, Types.INTEGER);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
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

}
