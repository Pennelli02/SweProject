package DomainModel;

import java.time.LocalDateTime;
import java.util.Date;

public class Booking {
    private int bookingID;
    private RegisterUser customer;
    private Accommodation accommodation;
    private float price;
    private int numPeople;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private State state;

    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public RegisterUser getCustomer() {
        return customer;
    }

    public void setCustomer(RegisterUser customer) {
        this.customer = customer;
    }

    public Accommodation getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getNumPeople() {
        return numPeople;
    }

    public void setNumPeople(int numPeople) {
        this.numPeople = numPeople;
    }

    public LocalDateTime getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDateTime checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDateTime getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDateTime checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }


    public Booking(int bookingID, RegisterUser customer, Accommodation accommodation, float price, int numPeople, LocalDateTime checkInDate, LocalDateTime checkOutDate, State state) {
        this.bookingID = bookingID;
        this.customer = customer;
        this.accommodation = accommodation;
        this.price = price;
        this.numPeople = numPeople;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.state = state;
    }

    public Booking() {}

    @Override
    public String toString() {
        return "bookingID=" + bookingID +
                ", customer=" + customer.getUsername() +
                ", accommodation=" + accommodation.getName() +
                ", price=" + price +
                ", numPeople=" + numPeople +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", state=" + state;
    }
}
