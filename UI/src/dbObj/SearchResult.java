package dbObj;

public record SearchResult(int id, String name, int type) {
    public static final int ATHLETE = 0;
    public static final int COURSE = 1;
    public static final int MEET = 2;
}
