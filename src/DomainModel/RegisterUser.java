package DomainModel;

import java.util.ArrayList;

public class RegisterUser {
    private int id;
    private String username;
    private String password;
    private String email;
    private int fidelityPoints;
    private String name;
    private String surname;
    private Locations favouriteLocations;
    private ArrayList<Booking> myBookings;
    private ArrayList<Accommodation> myPreferences;

    public RegisterUser(int id, String username, String password, String email, int fidelityPoints, String name, Locations favouriteLocations, String surname, ArrayList<Booking> myBookings, ArrayList<Accommodation> myPreferences) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fidelityPoints = fidelityPoints;
        this.name = name;
        this.favouriteLocations = favouriteLocations;
        this.surname = surname;
        this.myBookings = myBookings;
        this.myPreferences = myPreferences;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getFidelityPoints() {
        return fidelityPoints;
    }

    public void setFidelityPoints(int fidelityPoints) {
        this.fidelityPoints = fidelityPoints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Locations getFavouriteLocations() {
        return favouriteLocations;
    }

    public void setFavouriteLocations(Locations favouriteLocations) {
        this.favouriteLocations = favouriteLocations;
    }

    public ArrayList<Booking> getMyBookings() {
        return myBookings;
    }

    public void setMyBookings(ArrayList<Booking> myBookings) {
        this.myBookings = myBookings;
    }

    public ArrayList<Accommodation> getMyPreferences() {
        return myPreferences;
    }

    public void setMyPreferences(ArrayList<Accommodation> myPreferences) {
        this.myPreferences = myPreferences;
    }

    public void addBooking(Booking booking) {}

    public void removeBooking(Booking booking) {}

    public void addPreference(Accommodation accommodation) {}

    public void removePreference(Accommodation accommodation) {}
}
