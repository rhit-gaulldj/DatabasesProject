import databaseServices.*;
import screens.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;

public class Main {

    private JFrame frame;

    private DBConnectionService dbService;
    private UserService userService;
    private AthleteService athleteService;
    private CourseService courseService;
    private MeetService meetService;

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

        userService = new UserService(dbService);
        athleteService = new AthleteService(dbService);
        courseService = new CourseService(dbService);
        meetService = new MeetService(dbService);
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
        screenDict.put(ScreenTypes.AthletesList, new AthletesListScreen(athleteService,
                userService, this::switchScreens));
        screenDict.put(ScreenTypes.Test, new TestScreen(this::onLogout, userService));
        screenDict.put(ScreenTypes.AthleteModify, new AthleteModifyScreen(athleteService, this::switchScreens));
        screenDict.put(ScreenTypes.CourseList, new CourseListScreen(this::switchScreens, userService, courseService));
        screenDict.put(ScreenTypes.MeetList, new MeetListScreen(this::switchScreens, userService, meetService));

        // Create a panel to contain all the others
        JPanel masterPanel = new JPanel();
        for (Screen s : screenDict.values()) {
            s.populatePanel();
            JPanel panel = s.getPanel();
            panel.setVisible(false);
            masterPanel.add(panel);
        }
        frame.add(masterPanel);
        // Attempt to log in with the user's token
        boolean isLoggedInWithSession = false;
        String sessionId = null;
        File sessionFile = new File(UserService.getSessionIdPath());
        // TODO: Move to user service
        if (sessionFile.exists()) {
            try {
                Scanner reader = new Scanner(sessionFile);
                sessionId = reader.next();
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
            switchScreens(ScreenTypes.Login, new ScreenOpenArgs());
        }
        frame.pack();
    }

    private void switchScreens(ScreenTypes newScreenType, ScreenOpenArgs args) {
        JPanel formerlyActive = screenDict.get(this.activeScreen).getPanel();
        formerlyActive.setVisible(false);
        Screen newScreen = screenDict.get(newScreenType);
        JPanel newlyActive = newScreen.getPanel();
        newlyActive.setVisible(true);
        this.activeScreen = newScreenType;
        newScreen.openScreen(args);

        // TODO: Should be refactored into individual screens
        if(newScreenType == ScreenTypes.Test) {
        	
        	
        	try {
				CallableStatement stmt = dbService.getConnection().prepareCall("{call dbo.view_all_results}");
				
				ResultSet rs = stmt.executeQuery();
				JTable table = new JTable(buildTableModel(rs));
				newlyActive.add(table);
				
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            //Button to that shows popup
            JButton updateButton = new JButton("Modify Data");
            JPanel panel = new JPanel();
            newlyActive.add(updateButton);

            JTextField fieldMeetName = new JTextField();
            JTextField fieldMeetYear = new JTextField();
            Object[] message = {
                    "Meet Name:", fieldMeetName,
                    "Meet Year:", fieldMeetYear
            };

            updateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int confirmed = JOptionPane.showConfirmDialog(null, message, "Enter Values", JOptionPane.OK_CANCEL_OPTION);
                    if(confirmed == JOptionPane.OK_OPTION)
                        this.insertMeet(fieldMeetName.getText(),fieldMeetYear.getText());
                }
                private boolean insertMeet(String meetName, String meetYear) {
                    try {
                        int parsedMeetYear = Integer.parseInt(meetYear);
                        if (meetName != null && parsedMeetYear>0) {
                            CallableStatement cs = dbService.getConnection().prepareCall("{call insert_meet(?,?)}");
                            cs.setString(1, meetName);
                            cs.setInt(2, parsedMeetYear);
                            cs.execute();
//                            if(cs.getInt(1)==0) {
//                                JOptionPane.showMessageDialog((Component)null, "Successfully Inserted Meet!");
//                                return true;
//                            }
//                            JOptionPane.showMessageDialog((Component)null, "Stored Procedure execution failed!");
//                            return false;
                            JOptionPane.showMessageDialog((Component)null, "Successfully Inserted Meet!");
                            return true;
                        } else {
                            JOptionPane.showMessageDialog((Component)null, "Invalid Parameters!");
                            return false;
                        }
                    } catch(NumberFormatException e) {
                        JOptionPane.showMessageDialog((Component)null, "Meet Yeer must be an integer!");
                        throw new RuntimeException(e);
                    } catch (SQLException var8) {
                        JOptionPane.showMessageDialog((Component)null, var8);
                        throw new RuntimeException(var8);
                    }
                }
            });




        }
    }


    private void onLoginSuccess(String sessionId) {
        ((TestScreen) screenDict.get(ScreenTypes.Test)).setSessionId(sessionId);
        //switchScreens(ScreenTypes.Test);
        userService.setSessionId(sessionId);
        switchScreens(ScreenTypes.AthletesList, new ScreenOpenArgs());
    }
    private void onLogout() {
        JOptionPane.showMessageDialog(null, "You have logged out");
        switchScreens(ScreenTypes.Login, new ScreenOpenArgs());
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
