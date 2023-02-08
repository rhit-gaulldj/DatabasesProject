package dbObj;

public record Race(int id, DistancePair dist, RaceLevel raceLevel, int meetId) {

    @Override
    public String toString() {
        return raceLevel.name() + " (" + dist.toString() + ")";
    }
}
