package dbObj;

public record DistancePair(double dist, String units) {
    @Override
    public String toString() {
        return "" + dist + units;
    }
}
