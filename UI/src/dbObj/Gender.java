package dbObj;

public enum Gender {
    MALE("M", "Male"),
    FEMALE("F", "Female"),
    OTHER("O", "Other");

    private String n;
    private String longN;
    Gender(String n, String longN) {
        this.n = n;
        this.longN = longN;
    }

    public static Gender fromString(String n) {
        for (Gender g : Gender.values()) {
            if (g.n.equals(n)) {
                return g;
            }
        }
        return null;
    }
    public static Gender fromLongString(String ln) {
        for (Gender g : Gender.values()) {
            if (g.longN.equals(ln)) {
                return g;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return n;
    }
    public String toLongString() {
        return longN;
    }
}
