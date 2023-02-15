import java.io.*;
import java.util.*;

public class Main {

    // Note: This is by no means a good program, it is very poorly written but done to do the minimal
    // work possible to generate the scripts we need

    // Need to save course names so that we can use them for inserting into the race table
    static ArrayList<String> courseNames = new ArrayList<>();

    public static void main(String[] args) {
        // First arg is the file to open
        // Notes for when we do the total result data:
        // -- Course IDs will be autogenerated when they're added, but should be offset by 1 since the seed is at 1
        // -- Must split the name to add the first and last to athlete, and guarantee unique athletes
        // -- Time must be converted to seconds
        // -- Meet names must be saved along w/ years to create meet entries
        // -- Race levels must be generated as well here
        // -- Splits must be handled
        // -- Grade should be used w/ year to calculate grad year for the athlete
        // -- Distance can be in miles or have a unit on it which means kilometers
//        try {
//            createCourseStatement();
//            createOthersStatement();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        NewGenerator gen = new NewGenerator();
        gen.generate();
    }
    private static void createCourseStatement() throws IOException {
        File file = new File("PNXC Results_Course Data - Course IDs.csv");
        Writer fileWriter = new FileWriter("course_script.sql", false);
        ArrayList<String> lines = new ArrayList<>();
        lines.add("USE [TeamXCDB]");
        lines.add("GO");
        lines.add("");
        lines.add("INSERT INTO Course([name])");
        lines.add("VALUES");
        try {
            Scanner reader = new Scanner(file);
            reader.nextLine(); // Skip the first line
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                String name = courseLineToName(data);
                name = name.replaceAll("'", "''");
                String line = "\t('" + name + "')";
                if (reader.hasNextLine()) {
                    line = line + ",";
                }
                lines.add(line);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < lines.size(); i++) {
            fileWriter.write(lines.get(i) + "\n");
        }
        fileWriter.close();
        System.out.println("Done with courses");
    }
    private static String courseLineToName(String line) {
        String[] values = line.split(",");
        return values[2];
    }

    private static void createOthersStatement() {
        File file = new File("PNXC Results_Course Data - All Results.csv");

        // Step 1: Go line-by-line in the results CSV and extract the proper data
        ArrayList<String> raceLevels = new ArrayList<>();
        raceLevels.add("F/S");
        raceLevels.add("Freshman");
        raceLevels.add("JV");
        raceLevels.add("N/A");
        raceLevels.add("Open");
        raceLevels.add("Sophomore");
        raceLevels.add("Time Trial");
        raceLevels.add("Varsity");

        ArrayList<Athlete> athletes = new ArrayList<>();
        ArrayList<Meet> meets = new ArrayList<>();
        ArrayList<Race> races = new ArrayList<>();
        ArrayList<Result> results = new ArrayList<>();
        ArrayList<ResultSplit> splits = new ArrayList<>();
        try {
            Scanner reader = new Scanner(file);
            reader.nextLine(); // Skip the first line
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
                // Handle adding the athlete if they don't exist
                int athleteIndex = indexForAthlete(athletes, cols[0]) + 1;
                if (athleteIndex <= 0) {
                    athletes.add(createAthlete(cols));
                    athleteIndex = athletes.size();
                }
                // Add meet if it doesn't exist
                Meet meet = new Meet(cols[3], Integer.parseInt(cols[4]));
                if (!meets.contains(meet)) {
                    meets.add(meet);
                }
                // Add race if it doesn't exist
                int meetIndex = meets.indexOf(meet) + 1;
                int courseIndex = Integer.parseInt(cols[2]) + 1;
                int raceLevelIndex = raceLevels.indexOf(cols[9]) + 1;
                DistUnits units = DistUnits.MILES;
                if (cols[11].contains("K") || cols[11].contains("k")) {
                    units = DistUnits.KILOMETERS;
                    cols[11] = cols[11].substring(0, cols[11].length() - 1);
                }
                float distance = Float.parseFloat(cols[11]);
                Race race = new Race(distance, units, raceLevelIndex, meetIndex, courseIndex);
                if (!races.contains(race)) {
                    races.add(race);
                }
                // Add result (it should definitely not exist)
                int raceIndex = races.indexOf(race) + 1;
                float time = parseTime(cols[1]);
                Result result = new Result(time, athleteIndex, raceIndex);
                results.add(result);
                int resultIndex = results.size();

                // Hardest part is the splits
                // This is because some people run slow splits, so sometimes one guy's mile split
                // is within 10 seconds of someone else's 800 split. So we can't just use raw
                // time to decide what it is (ex. can't say "<4:30 = 800, >4:30 = mile")
                // Additionally, some 2-mile splits appear
                // But some people run 12:00 for 2 miles and some run 12:00 for 1 mile
                // We'll do some work with the average of the splits for a row to determine what to do
                // One pass to calculate the average
                float avgTime = 0;
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
                    ResultSplit split = new ResultSplit(resultIndex, i, thisTime, dist, DistUnits.MILES);
                    splits.add(split);
                }
            }
            reader.close();
            System.out.println();
            createOthersFile(athletes, meets, races, results, raceLevels, splits);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void createOthersFile(ArrayList<Athlete> athletes, ArrayList<Meet> meets,
                                         ArrayList<Race> races, ArrayList<Result> results,
                                         ArrayList<String> raceLevels, ArrayList<ResultSplit> splits) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<String> lines2 = new ArrayList<>();
        Writer fileWriter = new FileWriter("other_insert_script.sql", false);
        Writer fileWriter2 = new FileWriter("other_insert_script2.sql", false);
        lines.add("USE [TeamXCDB]");
        lines.add("GO");
        lines.add("");
        lines2.add("USE [TeamXCDB]");
        lines2.add("GO");
        lines2.add("");

        lines.add("INSERT INTO Athlete([first_name], [last_name], [grad_year], [gender])");
        lines.add("VALUES");
        for (int i = 0; i < athletes.size(); i++) {
            String line = "\t('" + athletes.get(i).firstName + "', '" + athletes.get(i).lastName +
                    "', " + athletes.get(i).gradYear + ", 'M')";
            if (i + 1 < athletes.size()) {
                line += ",";
            }
            lines.add(line);
        }
        lines.add("");

        lines.add("INSERT INTO Meet([name], [year])");
        lines.add("VALUES");
        for (int i = 0; i < meets.size(); i++) {
            String line = "\t('" + meets.get(i).name + "', " + meets.get(i).year + ")";
            if (i + 1 < meets.size()) {
                line += ",";
            }
            lines.add(line);
        }
        lines.add("");

        lines.add("INSERT INTO RaceLevel([name])");
        lines.add("VALUES");
        for (int i = 0; i < raceLevels.size(); i++) {
            String line = "\t('" + raceLevels.get(i) + "')";
            if (i + 1 < raceLevels.size()) {
                line += ",";
            }
            lines.add(line);
        }
        lines.add("");

        lines.add("INSERT INTO Race([distance], [distance_unit], [race_level_id], [meet_id], [course_id])");
        lines.add("VALUES");
        for (int i = 0; i < races.size(); i++) {
            String line = "\t(" + races.get(i).distance + ", '" + races.get(i).units.getAbbreviation() +
                    "', " + races.get(i).levelIndex + ", " + races.get(i).meetIndex + ", " +
                    races.get(i).courseIndex + ")";
            if (i + 1 < races.size()) {
                line += ",";
            }
            lines.add(line);
        }
        lines.add("");

        // Need to insert into results multiple times as there are over 1000 rows
        for (int j = 0; j < Math.ceil(results.size() / 1000.0); j++) {
            lines2.add("INSERT INTO Result([time], [athlete_id], [race_id])");
            lines2.add("VALUES");
            for (int i = j * 1000; i < (j+1) * 1000 && i < results.size(); i++) {
                String line = "\t(" + results.get(i).time + ", " + results.get(i).athleteId + ", " +
                        results.get(i).raceId + ")";
                if (i+1 < (j+1) * 1000 && i+1 < results.size()) {
                    line += ",";
                }
                lines2.add(line);
            }
            lines2.add("");
        }

        // Same deal with the splits, we have over 1000 so have to do multiple inserts
        for (int j = 0; j < Math.ceil(splits.size() / 1000.0); j++) {
            lines2.add("INSERT INTO ResultSplit([result_id], [index], [time], [distance], [distance_unit])");
            lines2.add("VALUES");
            for (int i = j * 1000; i < (j+1) * 1000 && i < splits.size(); i++) {
                String line = "\t(" + splits.get(i).resultId + ", " + splits.get(i).index + ", " +
                        splits.get(i).time + ", " + splits.get(i).distance + ", '" +
                        splits.get(i).unit.getAbbreviation() + "')";
                if (i + 1 < (j+1) * 1000 && i + 1 < splits.size()) {
                    line += ",";
                }
                lines2.add(line);
            }
            lines2.add("");
        }

        for (int i = 0; i < lines.size(); i++) {
            fileWriter.write(lines.get(i) + "\n");
        }
        fileWriter.close();
        for (int i = 0; i < lines2.size(); i++) {
            fileWriter2.write(lines2.get(i) + "\n");
        }
        fileWriter2.close();
        System.out.println("Done with all others");
    }

    private static int indexForAthlete(ArrayList<Athlete> src, String name) {
        String[] split = name.split(", ");
        String fname = split[1];
        String lname = split[0];
        for (int i = 0; i < src.size(); i++) {
            if (src.get(i).firstName.equals(fname) && src.get(i).lastName.equals(lname)) {
                return i;
            }
        }
        return -1;
    }

    private static Athlete createAthlete(String[] cols) {
        Athlete a = new Athlete();
        String[] splitName = cols[0].split(", ");
        a.firstName = splitName[1];
        a.lastName = splitName[0];
        // Calculate grad year from the grade and year of meet
        int grade = Integer.parseInt(cols[10]);
        int year = Integer.parseInt(cols[4]);
        a.gradYear =  year + 1 + (12 - grade);
        return a;
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
}

// We're just doing public-access stuff for simplicity
class Athlete {
    public String firstName;
    public String lastName;
    public int gradYear;
    // Assume gender is male since source data is for an all-male team

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Athlete athlete = (Athlete) o;
        return gradYear == athlete.gradYear &&
                firstName.equals(athlete.firstName) &&
                lastName.equals(athlete.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, gradYear);
    }
}
class Meet {
    public String name;
    public int year;

    public Meet(String name, int year) {
        this.name = name;
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meet meet = (Meet) o;
        return year == meet.year && name.equals(meet.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, year);
    }
}
class Race {
    public float distance;
    public DistUnits units;
    public int levelIndex;
    public int meetIndex;
    public int courseIndex;

    public Race(float distance, DistUnits units, int levelIndex, int meetIndex, int courseIndex) {
        this.distance = distance;
        this.units = units;
        this.levelIndex = levelIndex;
        this.meetIndex = meetIndex;
        this.courseIndex = courseIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Race race = (Race) o;
        return levelIndex == race.levelIndex &&
                meetIndex == race.meetIndex &&
                courseIndex == race.courseIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(levelIndex, meetIndex, courseIndex);
    }
}
class Result {
    public float time; // In seconds
    public int athleteId;
    public int raceId;

    public Result(float time, int athleteId, int raceId) {
        this.time = time;
        this.athleteId = athleteId;
        this.raceId = raceId;
    }
}
class ResultSplit {
    public int resultId;
    public int index;
    public float time;
    public float distance;
    public DistUnits unit;

    public ResultSplit(int resultId, int index, float time, float distance, DistUnits unit) {
        this.resultId = resultId;
        this.index = index;
        this.time = time;
        this.distance = distance;
        this.unit = unit;
    }
}

enum DistUnits {
    MILES("mi"),
    KILOMETERS("km"),
    METERS("m");

    String abb;
    DistUnits(String abb) {
        this.abb = abb;
    }

    public String getAbbreviation() {
        return abb;
    }
}