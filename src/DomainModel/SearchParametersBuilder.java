package DomainModel;

import DAO.DatabaseConnection;

import java.time.LocalDateTime;
import java.util.Date;

public final class SearchParametersBuilder {
    private String place;
    private Date dateOfCheckIn;
    private Date dateOfCheckOut;
    private int howMuchRooms;
    private int howMuchPeople;
    private Types category;
    private boolean allCategories;
    private float maxPrice;
    private RatingStars minRatingStars;
    private RatingStars specificRatingStars;
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

    public SearchParametersBuilder setDateOfCheckIn(Date dateOfCheckIn) {
        this.dateOfCheckIn = dateOfCheckIn;
        return this;
    }

    public SearchParametersBuilder setDateOfCheckOut(Date dateOfCheckOut) {
        this.dateOfCheckOut = dateOfCheckOut;
        return this;
    }

    public SearchParametersBuilder setHowMuchRooms(int howMuchRooms) {
        this.howMuchRooms = howMuchRooms;
        return this;
    }

    public SearchParametersBuilder setHowMuchPeople(int howMuchPeople) {
        this.howMuchPeople = howMuchPeople;
        return this;
    }

    public SearchParametersBuilder setCategory(Types category) {
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

    public SearchParametersBuilder setMinRatingStars(RatingStars minRatingStars) {
        this.minRatingStars = minRatingStars;
        return this;
    }

    public SearchParametersBuilder setSpecificRatingStars(RatingStars specificRatingStars) {
        this.specificRatingStars = specificRatingStars;
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

        Date now = new Date();

        if (dateOfCheckIn != null && dateOfCheckOut != null) {
            if (dateOfCheckIn.before(now) || dateOfCheckOut.before(now) || dateOfCheckOut.before(dateOfCheckIn)) {
                throw new IllegalArgumentException("Invalid check-in or check-out dates.");
            }
        }
        return new SearchParameters(place,  dateOfCheckIn,  dateOfCheckOut,  howMuchRooms,  howMuchPeople, category,  allCategories,  maxPrice,  minRatingStars,  specificRatingStars,  isRefundable,  haveFreeWifi,  canISmoke,  haveParking,  haveCoffeeMachine,  haveRoomService,  haveCleaningService,  haveSpa,  goodForKids,  canHaveAnimal);
    }


}
