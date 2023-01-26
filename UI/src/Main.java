import databaseServices.*;
import screens.LoginScreen;
import screens.Screen;
import screens.ScreenTypes;
import screens.TestScreen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;

public class Main {

    private JFrame frame;

    private DBConnectionService dbService;
    private Connection connection;
    private UserService userService;

    private Properties properties;

    private HashMap<ScreenTypes, Screen> screenDict;
    private ScreenTypes activeScreen = ScreenTypes.Login;

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        properties = getProperties();

        initDb();
        initUi();
    }

    private void initDb() {
        dbService = new DBConnectionService(properties.getProperty("serverName"),
                properties.getProperty("databaseName"));
        System.out.println("Connecting to database... (Make sure you're on the VPN)");
        // TODO: Encrypt password
        dbService.connect(properties.getProperty("serverUsername"), properties.getProperty("serverPassword"));
        System.out.println("Connected successfully");
        connection = dbService.getConnection();

        userService = new UserService(dbService);
    }
    private void initUi() {
        this.frame = new JFrame("Cross Country App");

        // Block standard closing because we want to handle behavior our own way (to close DB connection)
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // https://stackoverflow.com/questions/9093448/how-to-capture-a-jframes-close-button-click-event
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                dbService.closeConnection();
                System.exit(0);
            }
        });

        frame.setVisible(true);

        screenDict = new HashMap<>();
        screenDict.put(ScreenTypes.Login, new LoginScreen(userService, this::onLoginSuccess));
        screenDict.put(ScreenTypes.Test, new TestScreen(this::onLogout, userService));

        // Create a panel to contain all the others
        JPanel masterPanel = new JPanel();
        for (Screen s : screenDict.values()) {
            JPanel panel = s.getPanel();
            panel.setVisible(false);
            masterPanel.add(panel);
        }
        frame.add(masterPanel);
        // Attempt to log in with the user's token
        boolean isLoggedInWithSession = false;
        String sessionId = null;
        File sessionFile = new File(UserService.getSessionIdPath());
        if (sessionFile.exists()) {
            try {
                Scanner reader = new Scanner(sessionFile);
                sessionId = reader.next();
                System.out.println(sessionId);
                CallableStatement stmt = dbService.getConnection()
                        .prepareCall("{? = call log_in_session(?, ?)}");
                stmt.registerOutParameter(1, Types.INTEGER);
                stmt.setString(2, sessionId);
                stmt.registerOutParameter(3, Types.BIT);
                stmt.execute();
                boolean sessionValid = stmt.getBoolean(3);
                if (sessionValid) {
                    isLoggedInWithSession = true;
                }
                reader.close();
            } catch (FileNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        if (isLoggedInWithSession) {
            onLoginSuccess(sessionId);
        } else {
            switchScreens(ScreenTypes.Login);
        }
    }

    private void switchScreens(ScreenTypes newScreen) {
        System.out.println(newScreen);
        JPanel formerlyActive = screenDict.get(this.activeScreen).getPanel();
        formerlyActive.setVisible(false);
        JPanel newlyActive = screenDict.get(newScreen).getPanel();
        newlyActive.setVisible(true);
        System.out.println(newlyActive);
        this.activeScreen = newScreen;
        frame.pack();
        if(newScreen == ScreenTypes.Test) {
        	
        	
        	try {
				CallableStatement stmt = dbService.getConnection().prepareCall("{call dbo.view_all_results}");
				
				ResultSet rs = stmt.executeQuery();
				JTable table = new JTable(buildTableModel(rs));
				newlyActive.add(table);
				
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    private void onLoginSuccess(String sessionId) {
        JOptionPane.showMessageDialog(null, "You're logged in!");
        ((TestScreen) screenDict.get(ScreenTypes.Test)).setSessionId(sessionId);
        switchScreens(ScreenTypes.Test);
    }
    private void onLogout() {
        JOptionPane.showMessageDialog(null, "You have logged out");
        // TODO: reset login message text
        switchScreens(ScreenTypes.Login);
    }

    private static Properties getProperties() {
        String binDir = System.getProperty("user.dir");
        FileInputStream input = null;
        Properties properties = new Properties();
        try {
            input = new FileInputStream(binDir + "\\app.properties");
            properties.load(input);
            return properties;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    public static DefaultTableModel buildTableModel(ResultSet rs)
            throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // data of the table
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);

    }
}
