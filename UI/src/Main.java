import databaseServices.*;
import screens.LoginScreen;
import screens.Screen;
import screens.ScreenTypes;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

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
                System.out.println("Closed");
                System.exit(0);
            }
        });

        frame.setVisible(true);

        screenDict = new HashMap<>();
        screenDict.put(ScreenTypes.Login, new LoginScreen(userService));

        for (Screen s : screenDict.values()) {
            JPanel panel = s.getPanel();
            panel.setVisible(false);
            frame.add(panel);
        }
        switchScreens(ScreenTypes.Login);
    }

    private void switchScreens(ScreenTypes newScreen) {
        JPanel formerlyActive = screenDict.get(this.activeScreen).getPanel();
        formerlyActive.setVisible(false);
        JPanel newlyActive = screenDict.get(newScreen).getPanel();
        newlyActive.setVisible(true);
        this.activeScreen = newScreen;
        frame.pack();
    }

    private static Properties getProperties() {
        String binDir = System.getProperty("user.dir");
        System.out.println(binDir);
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
}
