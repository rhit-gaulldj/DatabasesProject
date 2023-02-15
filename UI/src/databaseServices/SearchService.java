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
                result.add(new SearchResult(rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getInt(4), rs.getString(5), rs.getInt(6), rs.getString(7), rs.getString(8)));
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

}
