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
                return new Meet(rs.getInt(1), rs.getString(2), rs.getInt(3));
            }
        }, "get_meets", "get_meet_count");

        this.dbService = dbService;
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
