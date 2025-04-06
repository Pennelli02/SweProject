package DomainModel;

public enum AccommodationRating {
    OneStar(1),
    TwoStar(2),
    ThreeStar(3),
    FourStar(4),
    FiveStar(5);

    private final int numericValue;

    AccommodationRating(int numericValue) {
        this.numericValue = numericValue;
    }

    public int getNumericValue() {
        return numericValue;
    }
}
