package databaseServices;

import dbObj.SearchResult;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class SearchService {

    private DBConnectionService dbService;

    public SearchService(DBConnectionService dbService) {
        this.dbService = dbService;
    }

    public List<SearchResult> search(String query) {
        try {
            CallableStatement stmt = dbService.getConnection().prepareCall("{? = call perform_search(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, query);
            ResultSet rs = stmt.executeQuery();
            List<SearchResult> result = new ArrayList<>();
            while (rs.next()) {
                int intType;
                String strType = rs.getString(2);
                switch (strType) {
                    case "Athlete":
                        intType = SearchResult.ATHLETE;
                        break;
                    case "Course":
                        intType = SearchResult.COURSE;
                        break;
                    case "Meet":
                        intType = SearchResult.MEET;
                        break;
                    default:
                        intType = -1;
                        break;
                }
                result.add(new SearchResult(rs.getInt(1), rs.getString(3), intType));
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

}
