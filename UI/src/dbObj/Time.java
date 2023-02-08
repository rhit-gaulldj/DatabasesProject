package dbObj;

public record Time(int minutes, int seconds, int secFraction){

    public float toSeconds() {
        float numDigits;
        if (secFraction == 0) {
            numDigits = 1;
        } else {
            numDigits = (float) Math.log10(secFraction) + 1;
        }
        return minutes * 60 + seconds + secFraction / numDigits;
    }

}
