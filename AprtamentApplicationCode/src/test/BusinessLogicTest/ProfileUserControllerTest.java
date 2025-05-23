package test.BusinessLogicTest;

import BusinessLogic.ProfileUserController;
import BusinessLogic.ResearchController;
import BusinessLogic.UserController;
import ORM.*;
import DomainModel.Location;
import DomainModel.RegisteredUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import DomainModel.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProfileUserControllerTest {
    private ProfileUserController profileUserController;
    private RegisteredUser registeredUser;
    private UserController userController;
    private UserDAO userDAO;

    LocalDateTime now = LocalDateTime.now();

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

        registeredUser =userController.register(testEmail,testPassword,testUsername,testName,testSurname,testLocation);

        profileUserController=new ProfileUserController(registeredUser);
    }

    @AfterEach
    void tearDown() {
        if (registeredUser !=null){
            userDAO.removeUser(registeredUser.getId());
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

        assertNotEquals(testEmail, registeredUser.getEmail());
        assertNotEquals(testPassword, registeredUser.getPassword());
        assertNotEquals(testUsername, registeredUser.getUsername());
        assertNotEquals(testName, registeredUser.getName());
        assertNotEquals(testSurname, registeredUser.getSurname());
        assertNotEquals(testLocation, registeredUser.getFavouriteLocations());

    }

    @Test
    void unRegister() throws SQLException, ClassNotFoundException {
        profileUserController.unRegister();
        registeredUser =userController.login(testEmail, testPassword);
        assertNull(registeredUser);
    }

    @Test
    void viewMyBookings() {
        ArrayList<Booking> bookings = profileUserController.viewMyBookings();
        assertTrue(bookings.isEmpty());
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        // Aggiungi una accommodation per l’utente
        accommodationDAO.addAccommodation(
                "Test House", "Via Test 1", "test", 3,
                AccommodationType.Hotel, 45.0f, now.plusDays(1), now.plusDays(10),
                "Alloggio test", AccommodationRating.OneStar,
                true, true, false, false, false, false, false,
                false, false, 2, false, 4);

        SearchParameters params = SearchParametersBuilder.newBuilder("Test").build();
        ResearchController rc = new ResearchController(registeredUser);
        List<Accommodation> results = rc.doResearch(params);

        // 2. Crea una nuova prenotazione
        BookingDAO bookingDAO = new BookingDAO();
        bookingDAO.addBooking(
                registeredUser,
                results.getFirst(),
                results.getFirst().getAvailableFrom(),
                results.getFirst().getAvailableEnd(),
                1,
                400
        );
        bookings = profileUserController.viewMyBookings();
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());

        accommodationDAO.deleteAccommodation(results.getFirst().getId());


    }

    @Test
    void cancelABooking() throws SQLException, ClassNotFoundException {
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        // Aggiungi una accommodation per l’utente
        accommodationDAO.addAccommodation(
                "Test House", "Via Test 1", "test", 3,
                AccommodationType.Hotel, 45.0f, now.plusDays(1), now.plusDays(10),
                "Alloggio test", AccommodationRating.OneStar,
                true, true, false, false, false, false, false,
                false, false, 2, false, 4);

        SearchParameters params = SearchParametersBuilder.newBuilder("Test").build();
        ResearchController rc = new ResearchController(registeredUser);
        List<Accommodation> results = rc.doResearch(params);

        // 2. Crea una nuova prenotazione
        BookingDAO bookingDAO = new BookingDAO();
        bookingDAO.addBooking(
                registeredUser,
                results.getFirst(),
                results.getFirst().getAvailableFrom(),
                results.getFirst().getAvailableEnd(),
                1,
                400
        );

        assertEquals(0, registeredUser.getFidelityPoints());
        // 3. Simula logout e nuovo login per testare persistenza
        profileUserController.exit();
        registeredUser = userController.login(testEmail, testPassword);
        profileUserController = new ProfileUserController(registeredUser);

        // 4. Verifica che la prenotazione esista e sia confermata
        var bookingsBefore = profileUserController.viewMyBookings();
        assertFalse(bookingsBefore.isEmpty());
        assertEquals(State.Booking_Confirmed, bookingsBefore.getFirst().getState());
        var Disponibility= results.getFirst().getDisponibility();
        // 5. Cancella la prenotazione
        profileUserController.cancelABooking(bookingsBefore.getFirst());

        // 6. Verifica che lo stato sia cambiato
        var bookingsAfter = profileUserController.viewMyBookings();
        assertNotEquals(State.Booking_Confirmed, bookingsAfter.getFirst().getState());

        assertEquals(0, registeredUser.getFidelityPoints());
        Accommodation accommodation= accommodationDAO.getAccommodationByID(results.getFirst().getId());
        assertNotEquals(accommodation.getDisponibility(), Disponibility);
        // 7. Cleanup manuale
        bookingDAO.removeBooking(bookingsAfter.getFirst().getBookingID(), State.Cancelled);

        accommodationDAO.deleteAccommodation(results.getFirst().getId());
    }

    @Test
    void removeBooking() throws SQLException, ClassNotFoundException {
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        // Aggiungi una accommodation per l’utente
        accommodationDAO.addAccommodation(
                "Test House", "Via Test 1", "test", 3,
                AccommodationType.Hotel, 45.0f, now.plusDays(1), now.plusDays(10),
                "Alloggio test", AccommodationRating.OneStar,
                true, true, false, false, false, false, false,
                false, false, 2, false, 4);

        SearchParameters params = SearchParametersBuilder.newBuilder("Test").build();
        ResearchController rc = new ResearchController(registeredUser);
        List<Accommodation> results = rc.doResearch(params);

        // 2. Crea una nuova prenotazione
        BookingDAO bookingDAO = new BookingDAO();
        bookingDAO.addBooking(
                registeredUser,
                results.getFirst(),
                results.getFirst().getAvailableFrom(),
                results.getFirst().getAvailableEnd(),
                1,
                400
        );

        // 3. Simula logout e nuovo login per testare persistenza
        profileUserController.exit();
        registeredUser = userController.login(testEmail, testPassword);
        profileUserController = new ProfileUserController(registeredUser);

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
        registeredUser = userController.login(testEmail, testPassword);
        profileUserController = new ProfileUserController(registeredUser);
        var bookingsBefore2 = profileUserController.viewMyBookings();
        assertTrue(bookingsBefore2.isEmpty());

        accommodationDAO.deleteAccommodation(results.getFirst().getId());
    }

    @Test
    void removeReview() throws SQLException, ClassNotFoundException {
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        // Aggiungi una accommodation per l’utente
        accommodationDAO.addAccommodation(
                "Test House", "Via Test 1", "test", 3,
                AccommodationType.Hotel, 45.0f, now.plusDays(1), now.plusDays(10),
                "Alloggio test", AccommodationRating.OneStar,
                true, true, false, false, false, false, false,
                false, false, 2, false, 4);

        SearchParameters params = SearchParametersBuilder.newBuilder("Test").build();
        ResearchController rc = new ResearchController(registeredUser);
        List<Accommodation> results = rc.doResearch(params);
        String testComment="testComment";
        ReviewDAO reviewDAO = new ReviewDAO();

        var reviews=profileUserController.viewMyReviews();
        assertTrue(reviews.isEmpty());
        reviewDAO.addReview(registeredUser, results.getFirst(), testComment, AccommodationRating.OneStar);


        reviews=profileUserController.getReviewsByUser();
        assertFalse(reviews.isEmpty());

        profileUserController.removeReview(reviews.getFirst());

        reviews=profileUserController.getReviewsByUser();
        assertTrue(reviews.isEmpty());

        accommodationDAO.deleteAccommodation(results.getFirst().getId());
    }

    @Test
    void unSaveAccommodation() throws SQLException, ClassNotFoundException {
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        // Aggiungi una accommodation per l’utente
        accommodationDAO.addAccommodation(
                "Test House", "Via Test 1", "test", 3,
                AccommodationType.Hotel, 45.0f, now.plusDays(1), now.plusDays(10),
                "Alloggio test", AccommodationRating.OneStar,
                true, true, false, false, false, false, false,
                false, false, 2, false, 4);

        SearchParameters params = SearchParametersBuilder.newBuilder("Test").build();
        ResearchController rc = new ResearchController(registeredUser);
        List<Accommodation> results = rc.doResearch(params);

        PreferenceDAO preferenceDAO = new PreferenceDAO();
        preferenceDAO.save(registeredUser.getId(), results.getFirst().getId());

        profileUserController.exit();
        registeredUser =userController.login(testEmail, testPassword);
        profileUserController = new ProfileUserController(registeredUser);

        var savingsBefore = registeredUser.getMyPreferences();
        assertFalse(savingsBefore.isEmpty());

        profileUserController.unSaveAccommodation(savingsBefore.getFirst());
        profileUserController.exit();
        registeredUser =userController.login(testEmail, testPassword);
        profileUserController = new ProfileUserController(registeredUser);
        var savingsAfter = registeredUser.getMyPreferences();
        assertTrue(savingsAfter.isEmpty());

        accommodationDAO.deleteAccommodation(results.getFirst().getId());
    }

    @Test
    void getReviewsByUser() {
        // non sono state scritte nessuna recensione quindi ci si aspetta 0
        var myReviews= profileUserController.getReviewsByUser();
        assertTrue(myReviews.isEmpty());


        // adesso vediamo con l'aggiunta di una recensione
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        // Aggiungi una accommodation per l’utente
        accommodationDAO.addAccommodation(
                "Test House", "Via Test 1", "test", 3,
                AccommodationType.Hotel, 45.0f, now.plusDays(1), now.plusDays(10),
                "Alloggio test", AccommodationRating.OneStar,
                true, true, false, false, false, false, false,
                false, false, 2, false, 4);

        SearchParameters params = SearchParametersBuilder.newBuilder("Test").build();
        ResearchController rc = new ResearchController(registeredUser);
        List<Accommodation> results = rc.doResearch(params);
        String testComment="testComment";
        ReviewDAO reviewDAO = new ReviewDAO();
        reviewDAO.addReview(registeredUser, results.getFirst(), testComment, AccommodationRating.OneStar);

        myReviews=profileUserController.getReviewsByUser();
        assertFalse(myReviews.isEmpty());

        assertEquals(testComment, myReviews.getFirst().getReviewText());
        assertEquals(AccommodationRating.OneStar, myReviews.getFirst().getVote());
        assertEquals(results.getFirst().getId(), myReviews.getFirst().getReviewedItem().getId());
        assertEquals(registeredUser.getId(), myReviews.getFirst().getAuthor().getId());

        accommodationDAO.deleteAccommodation(results.getFirst().getId());
    }
}