package DomainModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
// setter modificati per tener conto del dirty flag
public class Accommodation {
    private int id;
    private String name;
    private String address;
    private String place;
    private int disponibility; // per la disponibilità di prenotazioni
    private AccommodationType type;
    private float ratePrice;
    private LocalDateTime availableFrom;
    private LocalDateTime availableEnd;
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
    private int maxNumberOfPeople; // per il numero massimo di persone
    private Set<String> modifiedFields = new HashSet<>(); // utilizzo il concetto di dirty Flag

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
        if (!Objects.equals(this.name, name)) {
            modifiedFields.add("name");
        }
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (!Objects.equals(this.address, address)) {
            modifiedFields.add("address");
        }
        this.address = address;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        if (!Objects.equals(this.place, place)) {
            modifiedFields.add("place");
        }
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
        if (!Objects.equals(this.type, type)) {
            modifiedFields.add("type");
        }
        this.type = type;
    }

    public float getRatePrice() {
        return ratePrice;
    }

    public void setRatePrice(float ratePrice) {
        if (!Objects.equals(this.ratePrice, ratePrice)) {
            modifiedFields.add("ratePrice");
        }
        this.ratePrice = ratePrice;
    }

    public LocalDateTime getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(LocalDateTime availableFrom) {
        if (!Objects.equals(this.availableFrom, availableFrom)) {
            modifiedFields.add("availableFrom");
        }
        this.availableFrom = availableFrom;
    }

    public LocalDateTime getAvailableEnd() {
        return availableEnd;
    }

    public void setAvailableEnd(LocalDateTime availableEnd) {
        if (!Objects.equals(this.availableEnd, availableEnd)) {
            modifiedFields.add("availableEnd");
        }
        this.availableEnd = availableEnd;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (!Objects.equals(this.description, description)) {
            modifiedFields.add("description");
        }
        this.description = description;
    }

    public AccommodationRating getRating() {
        return rating;
    }

    public void setRating(AccommodationRating rating) {
        if (!Objects.equals(this.rating, rating)) {
            modifiedFields.add("rating");
        }
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
        if (!Objects.equals(this.freewifi, freewifi)) {
            modifiedFields.add("freewifi");
        }
        this.freewifi = freewifi;
    }

    public boolean isHaveSmokingArea() {
        return haveSmokingArea;
    }

    public void setHaveSmokingArea(boolean haveSmokingArea) {
        if (!Objects.equals(this.haveSmokingArea, haveSmokingArea)) {
            modifiedFields.add("haveSmokingArea");
        }
        this.haveSmokingArea = haveSmokingArea;
    }

    public boolean isHaveParking() {
        return haveParking;
    }

    public void setHaveParking(boolean haveParking) {
        if (!Objects.equals(this.haveParking, haveParking)) {
            modifiedFields.add("haveParking");
        }
        this.haveParking = haveParking;
    }

    public boolean isCoffeMachine() {
        return coffeMachine;
    }

    public void setCoffeMachine(boolean coffeMachine) {
        if (!Objects.equals(this.coffeMachine, coffeMachine)) {
            modifiedFields.add("coffeMachine");
        }
        this.coffeMachine = coffeMachine;
    }

    public boolean isRoomService() {
        return roomService;
    }

    public void setRoomService(boolean roomService) {
        if (!Objects.equals(this.roomService, roomService)) {
            modifiedFields.add("roomService");
        }
        this.roomService = roomService;
    }

    public boolean isCleaningService() {
        return cleaningService;
    }

    public void setCleaningService(boolean cleaningService) {
        if (!Objects.equals(this.cleaningService, cleaningService)) {
            modifiedFields.add("cleaningService");
        }
        this.cleaningService = cleaningService;
    }

    public boolean isHaveSpa() {
        return haveSpa;
    }

    public void setHaveSpa(boolean haveSpa) {
        if (!Objects.equals(this.haveSpa, haveSpa)) {
            modifiedFields.add("haveSpa");
        }
        this.haveSpa = haveSpa;
    }

    public boolean isGoodForKids() {
        return goodForKids;
    }

    public void setGoodForKids(boolean goodForKids) {
        if (!Objects.equals(this.goodForKids, goodForKids)) {
            modifiedFields.add("goodForKids");
        }
        this.goodForKids = goodForKids;
    }


    public void setNumberOfRoom(int numberOfRoom) {
        if (!Objects.equals(this.numberOfRoom, numberOfRoom)) {
            modifiedFields.add("numberOfRoom");
        }
        this.numberOfRoom = numberOfRoom;
    }

    public boolean isWelcomeAnimal() {
        return welcomeAnimal;
    }

    public void setWelcomeAnimal(boolean welcomeAnimal) {
        if (!Objects.equals(this.welcomeAnimal, welcomeAnimal)) {
            modifiedFields.add("welcomeAnimal");
        }
        this.welcomeAnimal = welcomeAnimal;
    }

    public void setDisponibility(int disponibility) {
        this.disponibility = disponibility;
    }

    public int getNumberOfRoom() {
        return numberOfRoom;
    }

    public int getMaxNumberOfPeople() {
        return maxNumberOfPeople;
    }

    public void setMaxNumberOfPeople(int maxNumberOfPeople) {
        if (!Objects.equals(this.maxNumberOfPeople, maxNumberOfPeople)) {
            modifiedFields.add("maxNumberOfPeople");
        }
        this.maxNumberOfPeople = maxNumberOfPeople;
    }

    public Set<String> getModifiedFields() {
        return modifiedFields;
    }

    public void setModifiedFields(Set<String> modifiedFields) {
        this.modifiedFields = modifiedFields;
    }

    // Aggiungi questo metodo controlla che sia stato modificato
    public boolean isFieldModified(String fieldName) {
        return modifiedFields.contains(fieldName);
    }

    public void clearModifiedFields() {
        modifiedFields.clear();
    }

    //FixMe aggiungerei un altro metodo per mostrare a schermo perché così son troppi attributi a schermo magari se ci sono più di un alloggio diventa ingestibile
    //FiXme lo terrei il toString per mostrare l'alloggio nel caso l'utente scelga di vedere tutte le informazioni
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return "Accommodation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", place='" + place + '\'' +
                ", disponibility=" + disponibility +
                ", type=" + type +
                ", ratePrice=" + ratePrice +
                ", availableFrom=" + availableFrom.format(formatter) +
                ", availableEnd=" + availableEnd.format(formatter) +
                ", description='" + description + '\'' +
                ", rating=" + rating +
                ", refundable=" + refundable +
                ", freewifi=" + freewifi +
                ", haveSmokingArea=" + haveSmokingArea +
                ", haveParking=" + haveParking +
                ", coffeMachine=" + coffeMachine +
                ", roomService=" + roomService +
                ", cleaningService=" + cleaningService +
                ", haveSpa=" + haveSpa +
                ", goodForKids=" + goodForKids +
                ", numberOfRoom=" + numberOfRoom +
                ", welcomeAnimal=" + welcomeAnimal +
                ", maxNumberOfPeople=" + maxNumberOfPeople +
                '}';
    }
}
