package databaseServices;

import dbObj.*;

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

    public DistancePair[] getCourseDistances(int courseId) {
        try {
            CallableStatement stmt = dbService.getConnection().prepareCall("{? = call get_dists_for_course(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, courseId);
            ResultSet rs = stmt.executeQuery();
            List<DistancePair> result = new ArrayList<>();
            while (rs.next()) {
                result.add(new DistancePair(rs.getDouble(1), rs.getString(2)));
            }
            DistancePair[] arr = new DistancePair[result.size()];
            result.toArray(arr);
            return arr;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public RaceLevel[] getRaceLevelsForCourse(int courseId) {
        try {
            CallableStatement stmt = dbService.getConnection()
                    .prepareCall("{? = call get_racelevels_for_course(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, courseId);
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

    public ResultSet getTopResultsPerCourse(int courseId, int numResults, boolean allowDupes,
                                            int raceLevelId, DistancePair distance) {
        try {
            CallableStatement stmt = dbService.getConnection()
                    .prepareCall("{? = call top_results_by_course(?, ?, ?, ?, ?, ?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, courseId);
            stmt.setInt(3, numResults);
            stmt.setBoolean(4, allowDupes);
            stmt.setInt(5, raceLevelId);
            stmt.setDouble(6, distance.dist());
            stmt.setString(7, distance.units());
            return stmt.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
