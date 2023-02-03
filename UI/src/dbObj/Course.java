package dbObj;

public record Course(int id, String name) {

    @Override
    public String toString() {
        return name;
    }
}