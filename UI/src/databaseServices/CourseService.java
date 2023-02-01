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

public class CourseService {

    private DBConnectionService dbService;

    public CourseService(DBConnectionService dbService) {
        this.dbService = dbService;
    }

    // TODO: Add get page, get indiv, update, delete
    public List<Course> getCourses(int page, int pageSize) {
        try {
            CallableStatement stmt = dbService.getConnection().prepareCall("{? = call get_courses(?, ?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, page);
            stmt.setInt(3, pageSize);
            ResultSet rs = stmt.executeQuery();
            ArrayList<Course> courses = new ArrayList<>();
            while (rs.next()) {
                Course c = new Course(rs.getInt(1), rs.getString(2));
                courses.add(c);
            }
            return courses;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getCourseCount() {
        try {
            CallableStatement stmt = dbService.getConnection().prepareCall("{? = call get_course_count}");
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
