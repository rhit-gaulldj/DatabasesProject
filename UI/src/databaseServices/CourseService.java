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

    private DBConnectionService dbService;

    public CourseService(DBConnectionService dbService) {
        super(dbService, new DBObjectCreator() {
            @Override
            public Object createObj(ResultSet rs) throws SQLException {
                return new Course(rs.getInt(1), rs.getString(2));
            }
        }, "get_courses", "get_course_count");
        this.dbService = dbService;
    }

    public void insertCourse(Course newCourse) {
        try {
            CallableStatement stmt = dbService.getConnection()
                    .prepareCall("{? = call insert_course(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, newCourse.name());
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // TODO: Move this to the abstract class
    public Course getCourse(int id) {
        try {
            CallableStatement stmt = dbService.getConnection()
                    .prepareCall("{? = call get_course(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Course(rs.getInt(1), rs.getString(2));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
