package databaseServices;

import dbObj.Athlete;
import dbObj.Course;
import dbObj.Gender;

import javax.swing.*;
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
        }, "get_courses", "get_course_count", "get_all_courses");
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

    public void updateCourse(int id, Course newObj) {
        try {
            CallableStatement stmt = dbService.getConnection()
                    .prepareCall("{? = call update_course(?, ?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, id);
            stmt.setString(3, newObj.name());
            stmt.execute();
            int status = stmt.getInt(1);
            if (status != 0) {
                JOptionPane.showMessageDialog(null, "Error updating course");
            }

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

    public void deleteCourse(int id) {
        try {
            CallableStatement stmt = dbService.getConnection().prepareCall("{? = call delete_course(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, id);
            stmt.execute();
            int status = stmt.getInt(1);
            if (status != 0) {
                JOptionPane.showMessageDialog(null, "Error deleting course");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
