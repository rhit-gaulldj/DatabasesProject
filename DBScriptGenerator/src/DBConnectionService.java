package databaseServices;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionService {

    private static final String CONNECT_URL =
            "jdbc:sqlserver://${dbServer};databaseName=${dbName};user=${user};password={${pass}}";

    private Connection connection = null;

    private String serverName;
    private String dbName;

    public DBConnectionService(String serverName, String dbName) {
        this.serverName = serverName;
        this.dbName = dbName;
    }

    public void connect(String username, String password) {
        String url = CONNECT_URL.replace("${dbServer}", serverName)
                .replace("${dbName}", dbName)
                .replace("${user}", username)
                .replace("${pass}", password);
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
