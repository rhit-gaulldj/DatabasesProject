package dbObj;

public record Race(int id, DistancePair dist, RaceLevel raceLevel, int meetId, Gender gender) {

    @Override
    public String toString() {
        return gender.toLongString() + " " + raceLevel.name() + " (" + dist.toString() + ")";
    }
}
