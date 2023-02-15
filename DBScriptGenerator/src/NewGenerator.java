import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

// This is the generator we actually use; old one is kept in Main
public class NewGenerator {

    // Keep a list of course names
    private ArrayList<String> courseNames = new ArrayList<>();

    private DBConnectionService dbService;

    public void generate() {
        Properties properties = getProperties();
        dbService = new DBConnectionService(properties.getProperty("serverName"),
                properties.getProperty("databaseName"));
        System.out.println("Connecting to database... (Make sure you're on the VPN)");
        dbService.connect(properties.getProperty("serverUsername"), properties.getProperty("serverPassword"));
        System.out.println("Connected successfully");

        genCourseNames();
        try {
            genRest();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Done");
    }

    private void genCourseNames() {
        File courseFile = new File("PNXC Results_Course Data - Course IDs.csv");
        Scanner reader = null;
        try {
            reader = new Scanner(courseFile);
            reader.nextLine(); // Skip the first line
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                String[] values = data.split(",");
                String name = values[2];
                name = name.replaceAll("'", "''");
                courseNames.add(name);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void genRest() throws FileNotFoundException {
        File mainFile = new File("PNXC Results_Course Data - All Results.csv");
        // Read each line and create a call to update
        Scanner reader = new Scanner(mainFile);
        reader.nextLine(); // Skip the first line
        int counter = 0;
        while (reader.hasNextLine()) {
            String data = reader.nextLine();
            String[] cols = data.split("(?!\\B\"[^\"]*),(?![^\"]*\"\\B)");
            for (int i = 0; i < cols.length; i++) {
                if (cols[i].length() == 0) {
                    continue;
                }
                if (cols[i].charAt(0) == '"' || cols[i].charAt(0) == '[') {
                    // Remove first and last character and reassign
                    cols[i] = cols[i].substring(1, cols[i].length() - 1);
                }
                cols[i] = cols[i].replaceAll("[\u200C]", "");
                cols[i] = cols[i].trim();
                cols[i] = cols[i].replaceAll("'", "''");
            }
            // Extract fields from the source
            String[] nameSplit = cols[0].split(", ");
            String fname = nameSplit[1];
            String lname = nameSplit[0];
            int grade = Integer.parseInt(cols[10]);
            String meetName = cols[3];
            int meetYear = Integer.parseInt(cols[4]);
            // Get the name of the course
            int courseIndex = Integer.parseInt(cols[2]);
            String courseName = courseNames.get(courseIndex);
            String raceLevelName = cols[9];
            DistUnits units = DistUnits.MILES;
            if (cols[11].contains("K") || cols[11].contains("k")) {
                units = DistUnits.KILOMETERS;
                cols[11] = cols[11].substring(0, cols[11].length() - 1);
            }
            String distanceUnit = units.getAbbreviation();
            float distance = Float.parseFloat(cols[11]);
            float time = parseTime(cols[1]);
            ArrayList<ResultSplit> splits = getSplits(cols);
            // Now we can add the result
            addFull(fname, lname, grade, meetName, meetYear, courseName, raceLevelName, distance, distanceUnit,
                    time, splits);

            counter++;
            if (counter % 500 == 0) {
                System.out.println("Completed " + counter + " rows");
            }
        }
        reader.close();
    }

    public void addFull(String fname, String lname, int grade, String meetName, int meetYear,
                         String courseName, String raceLevelName, float distance, String distanceUnit,
                         float time, ArrayList<ResultSplit> splits) {
        try {
            // 23 fields
            CallableStatement stmt = dbService.getConnection()
                    .prepareCall("{? = call insert_full_line(?, ?, ?, ?, ?, " +
                                                            "?, ?, ?, ?, ?, " +
                                                            "?, ?, ?, ?, ?, " +
                                                            "?, ?, ?, ?, ?, " +
                                                            "?, ?, ?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, fname);
            stmt.setString(3, lname);
            stmt.setString(4, "M");
            stmt.setFloat(5, time);
            stmt.setString(6, courseName);
            stmt.setString(7, meetName);
            stmt.setInt(8, meetYear);
            stmt.setString(9, raceLevelName);
            stmt.setInt(10, grade);
            stmt.setFloat(11, distance);
            stmt.setString(12, distanceUnit);
            // Splits are all optional
            int index = 13;
            for (int i = 0; i < splits.size(); i++) {
                ResultSplit s = splits.get(i);
                stmt.setFloat(index++, s.time);
                stmt.setFloat(index++, s.distance);
                stmt.setString(index++, s.unit.getAbbreviation());
            }
            // Set rest to null
            for (int i = splits.size(); i < 4; i++) {
                stmt.setNull(index++, Types.FLOAT);
                stmt.setNull(index++, Types.FLOAT);
                stmt.setNull(index++, Types.VARCHAR);
            }

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<ResultSplit> getSplits(String[] cols) {
        // Hardest part is the splits
        // This is because some people run slow splits, so sometimes one guy's mile split
        // is within 10 seconds of someone else's 800 split. So we can't just use raw
        // time to decide what it is (ex. can't say "<4:30 = 800, >4:30 = mile")
        // Additionally, some 2-mile splits appear
        // But some people run 12:00 for 2 miles and some run 12:00 for 1 mile
        // We'll do some work with the average of the splits for a row to determine what to do
        // One pass to calculate the average
        float avgTime = 0;
        ArrayList<ResultSplit> splits = new ArrayList<>();
        ArrayList<Float> splitTimes = new ArrayList<>();
        ArrayList<Float> providedDists = new ArrayList<>();
        // Iterate through the 4 columns in which splits can appear
        for (int i = 5; i < 9; i++) {
            if (cols[i].length() > 0 && !cols[i].equals("?") && !cols[i].equals("N/A")) {
                if (cols[i].contains("(")) {
                    float d = Float.parseFloat(
                            cols[i].substring(cols[i].indexOf('(') + 1, cols[i].indexOf(')')));
                    providedDists.add(d);
                    cols[i] = cols[i].substring(0, cols[i].indexOf('(') - 1).trim();
                } else {
                    providedDists.add(-1f);
                }
                if (cols[i].charAt(0) == '[') {
                    cols[i] = cols[i].substring(1, cols[i].length() - 1);
                }
                float t = parseTime(cols[i]);
                avgTime += t;
                splitTimes.add(t);
            }
        }
        avgTime /= splitTimes.size();
        // Second pass to create the splits
        for (int i = 0; i < splitTimes.size(); i++) {
            // If 1.5x average or higher, then say it's a 2 mile
            // If 0.75x average or lower, then say it's a half mile
            // Otherwise, a mile split
            float thisTime = splitTimes.get(i);
            float dist = 1;
            if (providedDists.get(i) > 0) {
                dist = providedDists.get(i);
            } else {
                if (thisTime <= 0.75f * avgTime) {
                    dist = 0.5f;
                } else if (thisTime >= 1.5f * avgTime) {
                    dist = 2;
                }
            }
            ResultSplit split = new ResultSplit(-1, i, thisTime, dist, DistUnits.MILES);
            splits.add(split);
        }
        return splits;
    }

    private static float parseTime(String timeString) {
        // Split on colons
        String[] segments = timeString.split(":");
        if (segments.length > 2) {
            // Erroneously interpreted some sort of 24:XX-esque time as days:hours:minutes
            // We're going to re-assign segments to properly handle this
            String[] newSegments = new String[2];
            newSegments[0] = segments[0];
            newSegments[1] = segments[1];
            segments = newSegments;
        }
        // Parse each individual part, multiplying each successive segment by 60
        // (should never have anything higher than hours)
        int mult = 1;
        float time = 0;
        for (int i = segments.length - 1; i >= 0; i--) {
            float parsed = Float.parseFloat(segments[i]);
            time += parsed * mult;
            mult *= 60;
        }
        return time;
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
}
