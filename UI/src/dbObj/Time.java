package dbObj;

public record Time(int minutes, int seconds, int secFraction){

    public double toSeconds() {
        double numDigits;
        if (secFraction == 0) {
            numDigits = 1;
        } else {
            numDigits = Math.log10(secFraction) + 1;
        }
        return minutes * 60 + seconds + secFraction / numDigits;
    }

}
