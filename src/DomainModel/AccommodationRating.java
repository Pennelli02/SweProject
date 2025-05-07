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

    public static String convert(int rating) {
        return switch (rating) {
            case 1 -> "OneStar";
            case 2 -> "TwoStar";
            case 3 -> "ThreeStar";
            case 4 -> "FourStar";
            case 5 -> "FiveStar";
            default -> "OneStar";
        };
    }

    public int getNumericValue() {
        return numericValue;
    }
}
