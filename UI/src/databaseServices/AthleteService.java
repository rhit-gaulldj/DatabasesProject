package databaseServices;

import dbObj.Athlete;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class AthleteService {

    private DBConnectionService dbService;

    public AthleteService(DBConnectionService dbService) {
        this.dbService = dbService;
    }

    public List<Athlete> getAthletes(int page) {
        try {
            CallableStatement stmt = dbService.getConnection().prepareCall("{? = call get_athletes(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, page);
            ResultSet rs = stmt.executeQuery();
            ArrayList<Athlete> athletes = new ArrayList<>();
            while (rs.next()) {
                Athlete a = new Athlete(rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(5), rs.getInt(4));
                athletes.add(a);
            }
            return athletes;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
