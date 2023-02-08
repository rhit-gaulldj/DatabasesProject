package dbObj;

public record Athlete(int id, String firstName, String lastName, Gender gender, int gradYear) {
    @Override
    public String toString() {
        return lastName + ", " + firstName;
    }
}