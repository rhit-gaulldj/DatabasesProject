package databaseServices;

import dbObj.Course;
import dbObj.Meet;

import javax.swing.*;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class MeetService extends AbstractDBService {

    private DBConnectionService dbService;

    public MeetService(DBConnectionService dbService) {
        super(dbService, new DBObjectCreator() {
            @Override
            public Object createObj(ResultSet rs) throws SQLException {
                return new Meet(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4));
            }
        }, "get_meets", "get_meet_count");

        this.dbService = dbService;
    }

    public void insert(Meet obj) {
        try {
            CallableStatement stmt = dbService.getConnection()
                    .prepareCall("{? = call insert_meet(?, ?, ?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, obj.name());
            stmt.setInt(3, obj.year());
            stmt.setInt(4, obj.courseId());
            stmt.execute();
            int status = stmt.getInt(1);
            if (status != 0) {
                JOptionPane.showMessageDialog(null, "Error deleting meet");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(int id, Meet newObj) {
        try {
            CallableStatement stmt = dbService.getConnection()
                    .prepareCall("{? = call update_meet(?, ?, ?, ?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, id);
            stmt.setString(3, newObj.name());
            stmt.setInt(4, newObj.year());
            stmt.setInt(5, newObj.courseId());
            stmt.execute();
            int status = stmt.getInt(1);
            if (status != 0) {
                JOptionPane.showMessageDialog(null, "Error updating meet");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Meet getObj(int id) {
        try {
            CallableStatement stmt = dbService.getConnection()
                    .prepareCall("{? = call get_meet(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Meet(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // TODO: Move delete to the abstract class
    public void delete(int id) {
        try {
            CallableStatement stmt = dbService.getConnection().prepareCall("{? = call delete_meet(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, id);
            stmt.execute();
            int status = stmt.getInt(1);
            if (status != 0) {
                JOptionPane.showMessageDialog(null, "Error deleting meet");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
