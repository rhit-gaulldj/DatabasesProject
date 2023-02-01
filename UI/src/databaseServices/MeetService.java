package databaseServices;

import dbObj.Course;
import dbObj.Meet;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class MeetService {

    private DBConnectionService dbService;

    public MeetService(DBConnectionService dbService) {
        this.dbService = dbService;
    }

    // TODO: Add get page, get indiv, update, delete
    public List<Meet> getMeets(int page, int pageSize) {
        try {
            CallableStatement stmt = dbService.getConnection().prepareCall("{? = call get_meets(?, ?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, page);
            stmt.setInt(3, pageSize);
            ResultSet rs = stmt.executeQuery();
            ArrayList<Meet> meets = new ArrayList<>();
            while (rs.next()) {
                Meet m = new Meet(rs.getInt(1), rs.getString(2), rs.getInt(3));
                meets.add(m);
            }
            return meets;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getMeetCount() {
        try {
            CallableStatement stmt = dbService.getConnection().prepareCall("{? = call get_meet_count}");
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

}
