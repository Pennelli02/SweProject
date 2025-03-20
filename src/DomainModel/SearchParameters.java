package DomainModel;

import java.util.Date;

public class SearchParameters {
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

    public SearchParameters(String place, Date dateOfCheckIn, Date dateOfCheckOut, int howMuchRooms, int howMuchPeople, Types category, boolean allCategories, float maxPrice, RatingStars minRatingStars, RatingStars specificRatingStars, boolean isRefundable, boolean haveFreeWifi, boolean canISmoke, boolean haveParking, boolean haveCoffeeMachine, boolean haveRoomService, boolean haveCleaningService, boolean haveSpa, boolean goodForKids, boolean canHaveAnimal) {
        this.place = place;
        this.dateOfCheckIn = dateOfCheckIn;
        this.dateOfCheckOut = dateOfCheckOut;
        this.howMuchRooms = howMuchRooms;
        this.howMuchPeople = howMuchPeople;
        this.category = category;
        this.allCategories = allCategories;
        this.maxPrice = maxPrice;
        this.minRatingStars = minRatingStars;
        this.specificRatingStars = specificRatingStars;
        this.isRefundable = isRefundable;
        this.haveFreeWifi = haveFreeWifi;
        this.canISmoke = canISmoke;
        this.haveParking = haveParking;
        this.haveCoffeeMachine = haveCoffeeMachine;
        this.haveRoomService = haveRoomService;
        this.haveCleaningService = haveCleaningService;
        this.haveSpa = haveSpa;
        this.goodForKids = goodForKids;
        this.canHaveAnimal = canHaveAnimal;
    }

    private boolean canHaveAnimal;


}
