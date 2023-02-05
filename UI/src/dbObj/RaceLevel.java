package dbObj;

public record RaceLevel(int id, String name) {
    @Override
    public String toString() {
        return name;
    }
}
