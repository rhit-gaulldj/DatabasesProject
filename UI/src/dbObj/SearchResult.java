package dbObj;

public record SearchResult(String athleteName, String time, String meetName, int meetYear,
                           String courseName, int grade, String splits, String distance) {
}
