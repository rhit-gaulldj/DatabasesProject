package databaseServices;

import dbObj.Athlete;
import dbObj.Course;
import dbObj.Gender;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class CourseService extends AbstractDBService {

    public CourseService(DBConnectionService dbService) {
        super(dbService, new DBObjectCreator() {
            @Override
            public Object createObj(ResultSet rs) throws SQLException {
                return new Course(rs.getInt(1), rs.getString(2));
            }
        }, "get_courses", "get_course_count");
    }

}
