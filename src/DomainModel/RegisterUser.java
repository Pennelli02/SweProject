package DomainModel;

import DAO.ReviewDAO;

import java.util.ArrayList;

public class RegisterUser {
    private int id;
    private String username;
    private String password;
    private String email;
    private int fidelityPoints;
    private String name;
    private String surname;
    private Location favouriteLocation;
    private ArrayList<Booking> myBookings;
    private ArrayList<Accommodation> myPreferences;

    public RegisterUser(int id, String username, String password, String email, int fidelityPoints, String name, Location favouriteLocation, String surname, ArrayList<Booking> myBookings, ArrayList<Accommodation> myPreferences) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fidelityPoints = fidelityPoints;
        this.name = name;
        this.favouriteLocation = favouriteLocation;
        this.surname = surname;
        this.myBookings = myBookings;
        this.myPreferences = myPreferences;
    }

    public RegisterUser(int id, String username, String password, String email, int fidelityPoints, String name, String surname, Location location) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fidelityPoints = fidelityPoints;
        this.name = name;
        this.surname = surname;
        this.favouriteLocation = location;
        myBookings = new ArrayList<>();
        myPreferences = new ArrayList<>();
    }
    // lo uso come possibile gestione per quando l'utente inserisce l'email giusta, ma la password sbagliata id<0
    public RegisterUser(int errorID, String email) {
        this.id = errorID; this.email = email;
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

    public Location getFavouriteLocations() {
        return favouriteLocation;
    }

    public void setFavouriteLocations(Location favouriteLocation) {
        this.favouriteLocation = favouriteLocation;
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

    public void addBooking(Booking booking) {
        myBookings.add(booking);
    }

    public void removeBooking(Booking booking) {
        myBookings.remove(booking);
    }

    public void addPreference(Accommodation accommodation) {
        myPreferences.add(accommodation);
    }

    public void removePreference(Accommodation accommodation) {
        myPreferences.remove(accommodation);
    }

    public void showMyPersonalInfo(){
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Email: " + email);
        System.out.println("Fidelity Points: " + fidelityPoints);
        System.out.println("Name: " + name);
        System.out.println("Surname: " + surname);
        System.out.println("Favourite Location: " + favouriteLocation);
//        System.out.println("My Bookings: " + myBookings);
//        System.out.println("My Preferences: " + myPreferences); dipende se vogliamo fare una cosa a parte così si può gestire il remove e le recensioni
    }

    //FIXME (richiesta lore) magari da espandere in modo che in base alo stato faccia uscire un messaggio? l'ho già aggiunto io dimmi se va bene. Risposta Lore: va benissimo
    public void showMyBookings(){
        System.out.println("ALL BOOKINGS");
        for (Booking myBooking : myBookings) {
            if (myBooking.getState() == State.Accommodation_Cancelled) {
                sendMessage(myBooking.getPrice(), myBooking.getAccommodation().getName());
            }
            System.out.println(myBooking);
        }
    }

    private void sendMessage(float price, String name) {
        String subject = "Siamo spiacenti per la cancellazione";
        String message = "Ciao " + username + ",\n\n" +
                "Purtroppo il tuo alloggio \"" + name + "\" è stato cancellato dalla struttura.\n" +
                "Come gesto di scuse, le abbiamo rimborsato l'intero importo della prenotazione (" + price + "€),\n" +
                "senza togliere i suoi punti fedeltà.\n\n" +
                "Grazie per la comprensione e ci auguriamo di riaverla presto tra i nostri viaggiatori.\n\n" +
                "Il Team";

        // Qui puoi inviare il messaggio (es. via mail o notifica)
        System.out.println("Oggetto: " + subject);
        System.out.println("Messaggio:\n" + message);
    }

    public void showMyPreferences(){
        System.out.println("ALL FAVOURITE ACCOMMODATIONS");
        for (Accommodation myPreference : myPreferences) {
            System.out.println(myPreference.toString());
        }
    }

    public void showMyReviews(ArrayList<Review> myReviews){
        System.out.println("ALL MY REVIEWS");
        for (Review myReview : myReviews) {
            System.out.println(myReview.toStringUser());
        }
    }
}
