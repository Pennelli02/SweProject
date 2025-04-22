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
}

