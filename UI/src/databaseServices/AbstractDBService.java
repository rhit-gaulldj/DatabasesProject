package databaseServices;

import dbObj.Athlete;
import dbObj.Gender;
import dbObj.RaceLevel;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AbstractDBService {

    private DBConnectionService dbService;

    private DBObjectCreator consumer;

    private final String getProcName;
    private final String getCountProcName;
    private final String getAllProcName;

    public AbstractDBService(DBConnectionService dbService, DBObjectCreator consumer,
                             String getProcName, String getCountProcName, String getAllProcName) {
        this.dbService = dbService;
        this.consumer = consumer;

        this.getProcName = getProcName;
        this.getCountProcName = getCountProcName;
        this.getAllProcName = getAllProcName;
    }

    public List<Object> getObjects(int page, int pageSize) {
        try {
            CallableStatement stmt = dbService.getConnection()
                    .prepareCall("{? = call " + getProcName + "(?, ?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, page);
            stmt.setInt(3, pageSize);
            ResultSet rs = stmt.executeQuery();
            ArrayList<Object> objs = new ArrayList<>();
            while (rs.next()) {
                objs.add(consumer.createObj(rs));
            }
            return objs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Object> getAll() {
        try {
            CallableStatement stmt = dbService.getConnection()
                    .prepareCall("{? = call " + getAllProcName + "}");
            stmt.registerOutParameter(1, Types.INTEGER);
            ResultSet rs = stmt.executeQuery();
            ArrayList<Object> objs = new ArrayList<>();
            while (rs.next()) {
                objs.add(consumer.createObj(rs));
            }
            return objs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getObjectCount() {
        try {
            CallableStatement stmt = dbService.getConnection().prepareCall("{? = call " + getCountProcName + "}");
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

    public RaceLevel[] getRaceLevels() {
        try {
            CallableStatement stmt = dbService.getConnection().prepareCall("{? = call get_race_levels}");
            stmt.registerOutParameter(1, Types.INTEGER);
            ResultSet rs = stmt.executeQuery();
            List<RaceLevel> result = new ArrayList<>();
            while (rs.next()) {
                result.add(new RaceLevel(rs.getInt(1), rs.getString(2)));
            }
            RaceLevel[] arr = new RaceLevel[result.size()];
            result.toArray(arr);
            return arr;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
