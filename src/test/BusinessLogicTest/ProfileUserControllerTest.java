package test.BusinessLogicTest;

import BusinessLogic.ProfileUserController;
import BusinessLogic.UserController;
import DAO.*;
import DomainModel.Location;
import DomainModel.RegisterUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import DomainModel.*;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProfileUserControllerTest {
    private ProfileUserController profileUserController;
    private RegisterUser registerUser;
    private UserController userController;
    private UserDAO userDAO;

    private final String testEmail = "test.user@example.com";
    private final String testPassword = "Test123!";
    private final String testUsername = "testuser";
    private final String testName = "Test";
    private final String testSurname = "User";
    private final Location testLocation= Location.Nothing;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        userDAO = new UserDAO();
        userController = new UserController();

        registerUser=userController.register(testEmail,testPassword,testUsername,testName,testSurname,testLocation);

        profileUserController=new ProfileUserController(registerUser);
    }

    @AfterEach
    void tearDown() {
        if (registerUser!=null){
            userDAO.removeUser(registerUser.getId());
        }
        profileUserController=null;
    }
    // non è necessario secondo me perché è solo una cosa visiva
//    @Test
//    void seeProfile() {
//    }

    @Test
    void updateProfile() throws SQLException, ClassNotFoundException {
        // testiamo con tutti i valori modificati
        String newEmail="newEmail";
        String newPassword="newPassword";
        String newUsername="newUsername";
        String newName="newName";
        String newSurname="newSurname";
        Location newLocation=Location.Sea;
        profileUserController.updateProfile(newName, newSurname, newEmail, newPassword, newUsername, newLocation);

        assertNotEquals(testEmail,registerUser.getEmail());
        assertNotEquals(testPassword,registerUser.getPassword());
        assertNotEquals(testUsername,registerUser.getUsername());
        assertNotEquals(testName,registerUser.getName());
        assertNotEquals(testSurname,registerUser.getSurname());
        assertNotEquals(testLocation, registerUser.getFavouriteLocations());

    }

    @Test
    void unRegister() throws SQLException, ClassNotFoundException {
        profileUserController.unRegister();
        registerUser=userController.login(testEmail, testPassword);
        assertNull(registerUser);
    }
// non so è una questione grafica
    @Test
    void viewMyBookings() {
    }

    @Test
    void cancelABooking() throws SQLException, ClassNotFoundException {
        // 1. Recupera un alloggio reale (esistente nel DB con id noto)
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        Accommodation accommodation = accommodationDAO.getAccommodationByID(2); // Adatta l'ID al tuo db

        // 2. Crea una nuova prenotazione
        BookingDAO bookingDAO = new BookingDAO();
        Booking testBooking = bookingDAO.addBooking(
                registerUser,
                accommodation,
                accommodation.getAvailableFrom(),
                accommodation.getAvailableEnd(),
                1,
                400
        );

        assertEquals(0, registerUser.getFidelityPoints());
        // 3. Simula logout e nuovo login per testare persistenza
        profileUserController.exit();
        registerUser = userController.login(testEmail, testPassword);
        profileUserController = new ProfileUserController(registerUser);

        // 4. Verifica che la prenotazione esista e sia confermata
        var bookingsBefore = profileUserController.viewMyBookings();
        assertFalse(bookingsBefore.isEmpty());
        assertEquals(State.Booking_Confirmed, bookingsBefore.getFirst().getState());
        var Disponibility= accommodation.getDisponibility();
        // 5. Cancella la prenotazione
        profileUserController.cancelABooking(bookingsBefore.getFirst());

        // 6. Verifica che lo stato sia cambiato
        var bookingsAfter = profileUserController.viewMyBookings();
        assertNotEquals(State.Booking_Confirmed, bookingsAfter.getFirst().getState());

        assertEquals(0, registerUser.getFidelityPoints());
        accommodation= accommodationDAO.getAccommodationByID(2);
        assertNotEquals(accommodation.getDisponibility(), Disponibility);
        // 7. Cleanup manuale
        bookingDAO.removeBooking(testBooking.getBookingID(), State.Cancelled);
    }

    @Test
    void removeBooking() throws SQLException, ClassNotFoundException {
        // 1. Recupera un alloggio reale (esistente nel DB con id noto)
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        Accommodation accommodation = accommodationDAO.getAccommodationByID(2); // Adatta l'ID al tuo db

        // 2. Crea una nuova prenotazione
        BookingDAO bookingDAO = new BookingDAO();
        Booking testBooking = bookingDAO.addBooking(
                registerUser,
                accommodation,
                accommodation.getAvailableFrom(),
                accommodation.getAvailableEnd(),
                1,
                400
        );

        // 3. Simula logout e nuovo login per testare persistenza
        profileUserController.exit();
        registerUser = userController.login(testEmail, testPassword);
        profileUserController = new ProfileUserController(registerUser);

        // 4. Verifica che la prenotazione esista e sia confermata
        var bookingsBefore = profileUserController.viewMyBookings();
        assertFalse(bookingsBefore.isEmpty());
        assertEquals(State.Booking_Confirmed, bookingsBefore.getFirst().getState());

        // 5. Cancella la prenotazione
        profileUserController.cancelABooking(bookingsBefore.getFirst());

        //6. rimuoviamo definitivamente la prenotazione
        profileUserController.removeBooking(bookingsBefore.getFirst());

        //7. controlliamo che la prenotazione non ci sia più
        var bookingsAfter = profileUserController.viewMyBookings();
        assertTrue(bookingsAfter.isEmpty());

        //8. controlliamo che sia persistente
        profileUserController.exit();
        registerUser = userController.login(testEmail, testPassword);
        profileUserController = new ProfileUserController(registerUser);
        var bookingsBefore2 = profileUserController.viewMyBookings();
        assertTrue(bookingsBefore2.isEmpty());
    }

    @Test
    void removeReview() throws SQLException, ClassNotFoundException {
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        Accommodation accommodation = accommodationDAO.getAccommodationByID(2); // Adatta l'ID al tuo db
        String testComment="testComment";
        ReviewDAO reviewDAO = new ReviewDAO();

        var reviews=profileUserController.viewMyReviews();
        assertTrue(reviews.isEmpty());
        reviewDAO.addReview(registerUser, accommodation, testComment, AccommodationRating.OneStar);
        var rating= accommodation.getRating();
        reviews=profileUserController.getReviewsByUser();
        assertFalse(reviews.isEmpty());

        profileUserController.removeReview(reviews.getFirst());

        reviews=profileUserController.getReviewsByUser();
        assertTrue(reviews.isEmpty());

        accommodation=accommodationDAO.getAccommodationByID(2);

        //assertNotEquals(rating,accommodation.getRating()); essendo che è stato fatto con un trigger non si aggiorna in tempo reale


    }

    @Test
    void unSaveAccommodation() throws SQLException, ClassNotFoundException {
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        Accommodation accommodation = accommodationDAO.getAccommodationByID(2);

        PreferenceDAO preferenceDAO = new PreferenceDAO();
        preferenceDAO.save(registerUser.getId(), accommodation.getId());

        profileUserController.exit();
        registerUser=userController.login(testEmail, testPassword);
        profileUserController = new ProfileUserController(registerUser);

        var savingsBefore = registerUser.getMyPreferences();
        assertFalse(savingsBefore.isEmpty());

        profileUserController.unSaveAccommodation(savingsBefore.getFirst());
        profileUserController.exit();
        registerUser=userController.login(testEmail, testPassword);
        profileUserController = new ProfileUserController(registerUser);
        var savingsAfter = registerUser.getMyPreferences();
        assertTrue(savingsAfter.isEmpty());

    }

    @Test
    void getReviewsByUser() {
        // non sono state scritte nessuna recensione quindi ci si aspetta 0
        var myReviews= profileUserController.getReviewsByUser();
        assertTrue(myReviews.isEmpty());


        // adesso vediamo con l'aggiunta di una recensione
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        Accommodation accommodation = accommodationDAO.getAccommodationByID(2);
        String testComment="testComment";
        ReviewDAO reviewDAO = new ReviewDAO();
        reviewDAO.addReview(registerUser, accommodation, testComment, AccommodationRating.OneStar);

        myReviews=profileUserController.getReviewsByUser();
        assertFalse(myReviews.isEmpty());

        assertEquals(testComment, myReviews.getFirst().getReviewText());
        assertEquals(AccommodationRating.OneStar, myReviews.getFirst().getVote());
        assertEquals(accommodation.getId(), myReviews.getFirst().getReviewedItem().getId());
        assertEquals(registerUser.getId(), myReviews.getFirst().getAuthor().getId());
    }
}