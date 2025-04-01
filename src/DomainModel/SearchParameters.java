package DomainModel;

import java.util.Date;

public class SearchParameters {
    private String place;
    private Date dateOfCheckIn;
    private Date dateOfCheckOut;
    private int howMuchRooms;
    private int howMuchPeople;
    private AccommodationType category;
    private boolean allCategories;
    private float maxPrice;
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

    public SearchParameters(String place, Date dateOfCheckIn, Date dateOfCheckOut, int howMuchRooms, int howMuchPeople, AccommodationType category, boolean allCategories, float maxPrice, AccommodationRating minAccommodationRating, AccommodationRating specificAccommodationRating, boolean isRefundable, boolean haveFreeWifi, boolean canISmoke, boolean haveParking, boolean haveCoffeeMachine, boolean haveRoomService, boolean haveCleaningService, boolean haveSpa, boolean goodForKids, boolean canHaveAnimal) {
        this.place = place;
        this.dateOfCheckIn = dateOfCheckIn;
        this.dateOfCheckOut = dateOfCheckOut;
        this.howMuchRooms = howMuchRooms;
        this.howMuchPeople = howMuchPeople;
        this.category = category;
        this.allCategories = allCategories;
        this.maxPrice = maxPrice;
        this.minAccommodationRating = minAccommodationRating;
        this.specificAccommodationRating = specificAccommodationRating;
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


    public String getPlace() {
        return place;
    }

    public Date getDateOfCheckIn() {
        return dateOfCheckIn;
    }

    public Date getDateOfCheckOut() {
        return dateOfCheckOut;
    }

    public int getHowMuchRooms() {
        return howMuchRooms;
    }

    public int getHowMuchPeople() {
        return howMuchPeople;
    }

    public AccommodationType getCategory() {
        return category;
    }

    public boolean isAllCategories() {
        return allCategories;
    }

    public float getMaxPrice() {
        return maxPrice;
    }

    public AccommodationRating getMinAccommodationRating() {
        return minAccommodationRating;
    }

    public AccommodationRating getSpecificAccommodationRating() {
        return specificAccommodationRating;
    }

    public boolean isRefundable() {
        return isRefundable;
    }

    public boolean isHaveFreeWifi() {
        return haveFreeWifi;
    }

    public boolean isCanISmoke() {
        return canISmoke;
    }

    public boolean isHaveParking() {
        return haveParking;
    }

    public boolean isHaveCoffeeMachine() {
        return haveCoffeeMachine;
    }

    public boolean isHaveRoomService() {
        return haveRoomService;
    }

    public boolean isHaveCleaningService() {
        return haveCleaningService;
    }

    public boolean isHaveSpa() {
        return haveSpa;
    }

    public boolean isGoodForKids() {
        return goodForKids;
    }

    public boolean isCanHaveAnimal() {
        return canHaveAnimal;
    }
}
