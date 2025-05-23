package DomainModel;

import java.util.ArrayList;

public class RegisteredUser {
    private int id;
    private String username;
    private String password;
    private String email;
    private int fidelityPoints;
    private String name;
    private String surname;
    private Location favouriteLocation;
    private ArrayList<Accommodation> myPreferences;

    public RegisteredUser(int id, String username, String password, String email, int fidelityPoints, String name, Location favouriteLocation, String surname, ArrayList<Accommodation> myPreferences) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fidelityPoints = fidelityPoints;
        this.name = name;
        this.favouriteLocation = favouriteLocation;
        this.surname = surname;
        this.myPreferences = myPreferences;
    }

    public RegisteredUser(int id, String username, String password, String email, int fidelityPoints, String name, String surname, Location location) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fidelityPoints = fidelityPoints;
        this.name = name;
        this.surname = surname;
        this.favouriteLocation = location;
        myPreferences = new ArrayList<>();
    }
    // lo uso come possibile gestione per quando l'utente inserisce l'email giusta, ma la password sbagliata id<0
    public RegisteredUser(int errorID, String email) {
        this.id = errorID; this.email = email;
    }
    //solo per testing
    public RegisteredUser() {}
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

    public Location getFavouriteLocations() {
        return favouriteLocation;
    }

    public void setFavouriteLocations(Location favouriteLocation) {
        this.favouriteLocation = favouriteLocation;
    }


    public ArrayList<Accommodation> getMyPreferences() {
        return myPreferences;
    }

    public void setMyPreferences(ArrayList<Accommodation> myPreferences) {
        this.myPreferences = myPreferences;
    }


    public void addPreference(Accommodation accommodation) {
        myPreferences.add(accommodation);
    }

    public void removePreference(Accommodation accommodation) {
        myPreferences.remove(accommodation);
    }

    public void showMyPersonalInfo(){
        System.out.print("Username: " + username);
        System.out.print(", Password: " + password);
        System.out.print(", Email: " + email);
        System.out.print(", Fidelity Points: " + fidelityPoints);
        System.out.print(", Name: " + name);
        System.out.print(", Surname: " + surname);
        System.out.print(", Favourite Location: " + favouriteLocation + "\n");
//        System.out.println("My Bookings: " + myBookings);
//        System.out.println("My Preferences: " + myPreferences); dipende se vogliamo fare una cosa a parte così si può gestire il remove e le recensioni
    }


    public void showMyPreferences(){
        System.out.println("ALL FAVOURITE ACCOMMODATIONS");
        for (int i=0;i<myPreferences.size();i++) {
            System.out.println((i+1)+") "+myPreferences.get(i).toString());
        }
    }

    public void showMyReviews(ArrayList<Review> myReviews){
        System.out.println("ALL MY REVIEWS");
        for (int i=0;i<myReviews.size();i++) {
            System.out.println((i+1)+") "+myReviews.get(i).toStringUser());
        }
    }

    public void showMyPersonalInfoAdmin() {
        System.out.print("ID: " + id);
        System.out.print(", Username: " + username);
        System.out.print(", Password: " + password);
        System.out.print(", Email: " + email);
        System.out.print(", Fidelity Points: " + fidelityPoints);
        System.out.print(", Name: " + name);
        System.out.print(", Surname: " + surname);
        System.out.print(", Favourite Location: " + favouriteLocation + "\n");
    }
}