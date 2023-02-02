package databaseServices;

import dbObj.Course;
import dbObj.Meet;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class MeetService extends AbstractDBService {

    public MeetService(DBConnectionService dbService) {
        super(dbService, new DBObjectCreator() {
            @Override
            public Object createObj(ResultSet rs) throws SQLException {
                return new Meet(rs.getInt(1), rs.getString(2), rs.getInt(3));
            }
        }, "get_meets", "get_meet_count");
    }

}
