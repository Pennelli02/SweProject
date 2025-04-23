package DomainModel;

public enum AccommodationType {
    Hotel,
    BnB,
    Apartment;

    public static AccommodationType fromString(String value) {
        return switch (value.toLowerCase()) {
            case "hotel" -> Hotel;
            case "apartment" -> Apartment;
            case "b&b", "bnb" -> BnB;
            default -> throw new IllegalArgumentException("Tipo di alloggio sconosciuto: " + value);
        };
    }

    @Override
    public String toString() {
        if (this == Hotel) {
            return "Hotel";
        }else if (this == Apartment) {
            return "Apartment";
        }else if (this == BnB) {
            return "B&B";
        }
        return null;
    }
}

