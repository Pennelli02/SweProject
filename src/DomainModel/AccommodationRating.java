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
        switch (rating){
            case 1:{
                return "OneStar";
            }
            case 2:{
                return "TwoStar";
            }
            case 3:{
                return "ThreeStar";
            }
            case 4:{
                return "FourStar";
            }
            case 5:{
                return "FiveStar";
            }
            default:
                return "OneStar";
        }
    }

    public int getNumericValue() {
        return numericValue;
    }
}
