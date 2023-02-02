package databaseServices;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DBObjectCreator {
    Object createObj(ResultSet rs) throws SQLException;
}
