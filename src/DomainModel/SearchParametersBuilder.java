package DomainModel;

import java.time.LocalDateTime;

public final class SearchParametersBuilder {
    private final String place;
    private LocalDateTime dateOfCheckIn;
    private LocalDateTime dateOfCheckOut;
    private Integer howMuchRooms=0;
    private Integer howMuchPeople=0;
    private AccommodationType category;
    private boolean allCategories;
    private Float maxPrice;
    private AccommodationRating minAccommodationRating;
    private AccommodationRating specificAccommodationRating;
    private boolean isRefundable;
    private boolean haveFreeWifi;
    private boolean canISmoke;
    private boolean haveParking;
    private boolean haveCoffeeMachine;
    private boolean haveRoomService;
    private boolean haveCleaningService;
    private boolean haveSpa;
    private boolean goodForKids;
    private boolean canHaveAnimal;

    public SearchParametersBuilder setDateOfCheckIn(LocalDateTime dateOfCheckIn) {
        this.dateOfCheckIn = dateOfCheckIn;
        return this;
    }

    public SearchParametersBuilder setDateOfCheckOut(LocalDateTime dateOfCheckOut) {
        this.dateOfCheckOut = dateOfCheckOut;
        return this;
    }

    public SearchParametersBuilder setHowMuchRooms(Integer howMuchRooms) {
        this.howMuchRooms = howMuchRooms;
        return this;
    }

    public SearchParametersBuilder setHowMuchPeople(Integer howMuchPeople) {
        this.howMuchPeople = howMuchPeople;
        return this;
    }

    public SearchParametersBuilder setCategory(AccommodationType category) {
        this.category = category;
        return this;
    }

    public SearchParametersBuilder setAllCategories(boolean allCategories) {
        this.allCategories = allCategories;
        return this;
    }

    public SearchParametersBuilder setMaxPrice(float maxPrice) {
        this.maxPrice = maxPrice;
        return this;
    }

    public SearchParametersBuilder setMinRatingStars(AccommodationRating minAccommodationRating) {
        this.minAccommodationRating = minAccommodationRating;
        return this;
    }

    public SearchParametersBuilder setSpecificRatingStars(AccommodationRating specificAccommodationRating) {
        this.specificAccommodationRating = specificAccommodationRating;
        return this;
    }

    public SearchParametersBuilder setRefundable(boolean refundable) {
        isRefundable = refundable;
        return this;
    }

    public SearchParametersBuilder setHaveFreeWifi(boolean haveFreeWifi) {
        this.haveFreeWifi = haveFreeWifi;
        return this;
    }

    public SearchParametersBuilder setCanISmoke(boolean canISmoke) {
        this.canISmoke = canISmoke;
        return this;
    }

    public SearchParametersBuilder setHaveParking(boolean haveParking) {
        this.haveParking = haveParking;
        return this;
    }

    public SearchParametersBuilder setHaveCoffeeMachine(boolean haveCoffeeMachine) {
        this.haveCoffeeMachine = haveCoffeeMachine;
        return this;
    }

    public SearchParametersBuilder setHaveRoomService(boolean haveRoomService) {
        this.haveRoomService = haveRoomService;
        return this;
    }

    public SearchParametersBuilder setHaveCleaningService(boolean haveCleaningService) {
        this.haveCleaningService = haveCleaningService;
        return this;
    }

    public SearchParametersBuilder setHaveSpa(boolean haveSpa) {
        this.haveSpa = haveSpa;
        return this;
    }

    public SearchParametersBuilder setGoodForKids(boolean goodForKids) {
        this.goodForKids = goodForKids;
        return this;
    }

    public SearchParametersBuilder setCanHaveAnimal(boolean canHaveAnimal) {
        this.canHaveAnimal = canHaveAnimal;
        return this;
    }



    private SearchParametersBuilder(String place) {
        this.place = place;
    }

    public static SearchParametersBuilder newBuilder(String place){
        return new SearchParametersBuilder(place);
    }

    public SearchParameters build() {

        // Validazione delle date
        validateDates();

        // Validazione degli altri parametri
        validatePeopleAndRooms();
        validatePriceRange();
        validateRating();

        return new SearchParameters(place,  dateOfCheckIn,  dateOfCheckOut,  howMuchRooms,  howMuchPeople, category,  allCategories,  maxPrice, minAccommodationRating, specificAccommodationRating,  isRefundable,  haveFreeWifi,  canISmoke,  haveParking,  haveCoffeeMachine,  haveRoomService,  haveCleaningService,  haveSpa,  goodForKids,  canHaveAnimal);
    }

    private void validateDates() {
        LocalDateTime now = LocalDateTime.now();

        if (dateOfCheckIn != null && dateOfCheckOut != null) {
            // Verifica che le date non siano nel passato
            if (dateOfCheckIn.isBefore(now)) {
                throw new IllegalArgumentException("Check-in date cannot be in the past.");
            }

            if (dateOfCheckOut.isBefore(now)) {
                throw new IllegalArgumentException("Check-out date cannot be in the past.");
            }

            // Verifica che la data di check-out sia dopo il check-in
            if (!dateOfCheckOut.isAfter(dateOfCheckIn)) {
                throw new IllegalArgumentException("Check-out date must be after check-in date.");
            }

        } else if (dateOfCheckIn != null || dateOfCheckOut != null) {
            // Se solo una delle due date è specificata
            throw new IllegalArgumentException("Both check-in and check-out dates must be provided or both must be null.");
        }
    }

    private void validatePeopleAndRooms() {
        if ( howMuchPeople!=0 && howMuchPeople < 1) {
            throw  new RuntimeException("️Numero di persone non valido: deve essere almeno 1.");
        }

        if ( howMuchRooms!=0 && howMuchRooms < 1) {
            throw  new RuntimeException("Numero di stanze non valido: deve essere almeno 1.");
        }
    }

    private void validatePriceRange() {
        if (maxPrice != null && maxPrice < 0) {
            throw new IllegalArgumentException("Maximum price cannot be negative.");
        }
    }

    private void validateRating() {
        if (minAccommodationRating != null && (minAccommodationRating.getNumericValue() < 1 || minAccommodationRating.getNumericValue() > 5)) {
            throw new IllegalArgumentException("Minimum rating must be between 1 and 5.");
        }

        if (specificAccommodationRating != null && (specificAccommodationRating.getNumericValue() < 1 || specificAccommodationRating.getNumericValue() > 5)) {
            throw new IllegalArgumentException("Specific rating must be between 1 and 5.");
        }

        // Verifica che non siano specificati sia rating minimo che specifico
        if (minAccommodationRating != null && specificAccommodationRating != null) {
            throw new IllegalArgumentException("Cannot specify both minimum and exact rating.");
        }
    }

}
