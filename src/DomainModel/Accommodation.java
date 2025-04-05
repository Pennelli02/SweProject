package DomainModel;

import java.util.Date;

public class Accommodation {
    private int id;
    private String name;
    private String address;
    private String place;
    private int disponibility;
    private AccommodationType type;
    private float ratePrice;
    private Date availableFrom;
    private Date availableEnd;
    private String description;
    private AccommodationRating rating;
    private boolean refundable;
    private boolean freewifi;
    private boolean haveSmokingArea;
    private boolean haveParking;
    private boolean coffeMachine;
    private boolean roomService;
    private boolean cleaningService;
    private boolean haveSpa;
    private boolean goodForKids;
    private int numberOfRoom;
    private boolean welcomeAnimal;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Integer getDisponibility() {
        return disponibility;
    }

    public void setDisponibility(Integer disponibility) {
        this.disponibility = disponibility;
    }

    public AccommodationType getType() {
        return type;
    }

    public void setType(AccommodationType type) {
        this.type = type;
    }

    public float getRatePrice() {
        return ratePrice;
    }

    public void setRatePrice(float ratePrice) {
        this.ratePrice = ratePrice;
    }

    public Date getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(Date availableFrom) {
        this.availableFrom = availableFrom;
    }

    public Date getAvailableEnd() {
        return availableEnd;
    }

    public void setAvailableEnd(Date availableEnd) {
        this.availableEnd = availableEnd;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AccommodationRating getRating() {
        return rating;
    }

    public void setRating(AccommodationRating rating) {
        this.rating = rating;
    }

    public boolean isRefundable() {
        return refundable;
    }

    public void setRefundable(boolean refundable) {
        this.refundable = refundable;
    }

    public boolean isFreewifi() {
        return freewifi;
    }

    public void setFreewifi(boolean freewifi) {
        this.freewifi = freewifi;
    }

    public boolean isHaveSmokingArea() {
        return haveSmokingArea;
    }

    public void setHaveSmokingArea(boolean haveSmokingArea) {
        this.haveSmokingArea = haveSmokingArea;
    }

    public boolean isHaveParking() {
        return haveParking;
    }

    public void setHaveParking(boolean haveParking) {
        this.haveParking = haveParking;
    }

    public boolean isCoffeMachine() {
        return coffeMachine;
    }

    public void setCoffeMachine(boolean coffeMachine) {
        this.coffeMachine = coffeMachine;
    }

    public boolean isRoomService() {
        return roomService;
    }

    public void setRoomService(boolean roomService) {
        this.roomService = roomService;
    }

    public boolean isCleaningService() {
        return cleaningService;
    }

    public void setCleaningService(boolean cleaningService) {
        this.cleaningService = cleaningService;
    }

    public boolean isHaveSpa() {
        return haveSpa;
    }

    public void setHaveSpa(boolean haveSpa) {
        this.haveSpa = haveSpa;
    }

    public boolean isGoodForKids() {
        return goodForKids;
    }

    public void setGoodForKids(boolean goodForKids) {
        this.goodForKids = goodForKids;
    }

    public int isNumberOfRoom() {
        return numberOfRoom;
    }

    public void setNumberOfRoom(int numberOfRoom) {
        this.numberOfRoom = numberOfRoom;
    }

    public boolean isWelcomeAnimal() {
        return welcomeAnimal;
    }

    public void setWelcomeAnimal(boolean welcomeAnimal) {
        this.welcomeAnimal = welcomeAnimal;
    }




}
