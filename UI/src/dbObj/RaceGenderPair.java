package dbObj;

public record RaceGenderPair(RaceLevel level, Gender gender) {
    @Override
    public String toString() {
        return gender.toLongString() + " " + level.toString();
    }
}
