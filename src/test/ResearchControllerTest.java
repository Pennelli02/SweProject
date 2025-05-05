package test;

import BusinessLogic.ProfileUserController;
import BusinessLogic.ResearchController;
import BusinessLogic.UserController;
import DAO.AccommodationDAO;
import DAO.UserDAO;
import DomainModel.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ResearchControllerTest {
    private ResearchController researchController;
    private RegisterUser registerUser;
    private UserController userController;
    private UserDAO userDAO;
    private AccommodationDAO accommodationDAO;
    private ProfileUserController profileUserController;
    private SearchParametersBuilder testSearchParametersBuilder;

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
        accommodationDAO = new AccommodationDAO();

        registerUser=userController.register(testEmail,testPassword,testUsername,testName,testSurname,testLocation);

        researchController=new ResearchController(registerUser);
        profileUserController= new ProfileUserController(registerUser);

    }

    @AfterEach
    void tearDown() {
        if (registerUser!=null){
            profileUserController.unRegister();
            userDAO.removeUser(registerUser.getId());
        }
        researchController=null;
    }

    @Test
    void doResearch() {
        // suppongo che ci siano già delle Accommodation nel db
        Accommodation accommodation=accommodationDAO.getAccommodationByID(3);

        LocalDateTime now = LocalDateTime.now();


        // proviamo a vedere utilizzando solo il luogo
        testSearchParametersBuilder= SearchParametersBuilder.newBuilder(accommodation.getPlace());

        // avviamo la ricerca
        ArrayList<Accommodation> accommodations= researchController.doResearch(testSearchParametersBuilder.build());

        //controlliamo che restituisca qualcolsa
        assertNotNull(accommodations);

        //controlliamo che per i risultati ottenuti abbiano tutti lo stesso luogo
        for (Accommodation value : accommodations) {
            assertEquals(value.getPlace(), accommodation.getPlace());
        }
        // controlliamo che se non c'è niente restituisce niente
        testSearchParametersBuilder= SearchParametersBuilder.newBuilder("Bangkok"); // non c'è nel db
        accommodations= researchController.doResearch(testSearchParametersBuilder.build());
        assertTrue(accommodations.isEmpty());

        // testiamo gli errori di sintassi
        testSearchParametersBuilder= SearchParametersBuilder.newBuilder("miLanO"); // troverà lo stesso
        accommodations= researchController.doResearch(testSearchParametersBuilder.build());
        assertNotNull(accommodations);
        for (Accommodation value : accommodations) {
            assertEquals(value.getPlace(), accommodation.getPlace());
        }

        // aggiungiamo dei test per la ricerca
        accommodationDAO.addAccommodation(
                "Family Spa Apartment",
                "Via delle Terme 12",
                "Zurigo",
                2,
                AccommodationType.Apartment,
                150.0f,
                now.plusDays(5),
                now.plusDays(20),
                "Appartamento familiare con centro benessere incluso.",
                AccommodationRating.FourStar,
                false,
                true,
                false,
                false,
                true,
                true,
                true,
                true,
                true,
                3,
                true,
                4
        );

        accommodationDAO.addAccommodation(
                "Hotel Roma Centro",
                "Via Roma 1",
                "Zurigo",
                5,
                AccommodationType.Hotel,
                99.99f,
                now.plusDays(1),
                now.plusDays(10),
                "Hotel economico in centro a Roma con WiFi.",
                AccommodationRating.ThreeStar,
                true,  // refundable
                true,  // freewifi
                false, // smoking
                true,  // parking
                false, // coffee
                true,  // room service
                true,  // cleaning
                false, // spa
                true,  // kids
                10,
                true,
                2
        );

        accommodationDAO.addAccommodation(
                "Student Hostel Rome",
                "Via Studenti 99",
                "Zurigo",
                20,
                AccommodationType.BnB,
                35.5f,
                now.plusDays(3),
                now.plusDays(30),
                "Ostello per studenti vicino all'università.",
                AccommodationRating.OneStar,
                false,
                true,
                true,
                false,
                false,
                false,
                true,
                false,
                false,
                15,
                false,
                1
        );

        // testiamo che cambiando certi parametri di ricerca restituisce valori diversi
        testSearchParametersBuilder= SearchParametersBuilder.newBuilder("Zurigo");
        testSearchParametersBuilder.setCategory(AccommodationType.Apartment); // filtriamo per categoria e si prende solo uno dei tre

        accommodations= researchController.doResearch(testSearchParametersBuilder.build());
        assertNotNull(accommodations);
        assertEquals(AccommodationType.Apartment, accommodations.getFirst().getType());
        assertEquals("Family Spa Apartment", accommodations.getFirst().getName());

        // controlliamo che qualsiasi parametro anche i vero e falso influenza la ricerca
        testSearchParametersBuilder= SearchParametersBuilder.newBuilder("Zurigo");
        testSearchParametersBuilder.setHaveFreeWifi(true); // tutti e tre ci rientra
        testSearchParametersBuilder.setHaveSpa(true); // solo uno ci rientra

        accommodations= researchController.doResearch(testSearchParametersBuilder.build());
        assertNotNull(accommodations);
        assertEquals("Zurigo", accommodations.getFirst().getPlace());
        assertTrue(accommodations.getFirst().isFreewifi());
        assertTrue(accommodations.getFirst().isWelcomeAnimal());

        // controlliamo quelli più "importanti"
        // per periodo
        testSearchParametersBuilder= SearchParametersBuilder.newBuilder("Zurigo");
        testSearchParametersBuilder.setDateOfCheckIn(now.plusDays(2));
        testSearchParametersBuilder.setDateOfCheckOut(now.plusDays(8));
        SearchParameters myPara=testSearchParametersBuilder.build();
        accommodations= researchController.doResearch(myPara);
        assertNotNull(accommodations);
        for (Accommodation value : accommodations) {
            assertEquals("Zurigo", value.getPlace());
            assertFalse(value.getAvailableFrom().isAfter(myPara.getDateOfCheckIn())); // availableFrom <= check-in
            assertFalse(value.getAvailableEnd().isBefore(myPara.getDateOfCheckOut())); // check-out <= availableEnd

        }

        //per numero di stanze
        testSearchParametersBuilder= SearchParametersBuilder.newBuilder("Zurigo");
        testSearchParametersBuilder.setHowMuchRooms(8);
        accommodations= researchController.doResearch(testSearchParametersBuilder.build());
        assertNotNull(accommodations);
        for (Accommodation value : accommodations) {
            assertEquals("Zurigo", value.getPlace());
            assertTrue(value.getNumberOfRoom() >= 8);
        }

        // per numero di persone
        testSearchParametersBuilder = SearchParametersBuilder.newBuilder("Zurigo");
        testSearchParametersBuilder.setHowMuchPeople(3);
        accommodations= researchController.doResearch(testSearchParametersBuilder.build());
        assertNotNull(accommodations);
        for (Accommodation value : accommodations) {
            assertEquals("Zurigo", value.getPlace());
            assertTrue(value.getMaxNumberOfPeople()>= 3);
        }

        // testiamo la gestione delle casistiche dei parametri rating and price
        // rating
        testSearchParametersBuilder= SearchParametersBuilder.newBuilder("Zurigo");
        testSearchParametersBuilder.setMinRatingStars(AccommodationRating.ThreeStar);
        SearchParameters testSearchParameters= testSearchParametersBuilder.build();
        accommodations= researchController.doResearch(testSearchParameters);
        assertNull(testSearchParameters.getSpecificAccommodationRating());
        assertNotNull(accommodations);
        for (Accommodation value : accommodations) {
            assertEquals("Zurigo", value.getPlace());
            assertTrue(value.getRating().getNumericValue() >= AccommodationRating.ThreeStar.getNumericValue());
        }

        testSearchParametersBuilder= SearchParametersBuilder.newBuilder("Zurigo");
        testSearchParametersBuilder.setSpecificRatingStars(AccommodationRating.OneStar);
        testSearchParameters= testSearchParametersBuilder.build();
        accommodations= researchController.doResearch(testSearchParameters);
        assertNull(testSearchParameters.getMinAccommodationRating());
        assertNotNull(accommodations);
        for (Accommodation value : accommodations) {
            assertEquals("Zurigo", value.getPlace());
            assertEquals(value.getRating().getNumericValue(), AccommodationRating.OneStar.getNumericValue());
        }

        //price
        testSearchParametersBuilder= SearchParametersBuilder.newBuilder("Zurigo");
        testSearchParametersBuilder.setMaxPrice(124.f);
        accommodations= researchController.doResearch(testSearchParametersBuilder.build());
        assertNotNull(accommodations);
        for (Accommodation value : accommodations) {
            assertEquals("Zurigo", value.getPlace());
            assertTrue(value.getRatePrice()<=124.f);
        }


        // per eliminare i test a fine
        testSearchParametersBuilder= SearchParametersBuilder.newBuilder("Zurigo");
        accommodations= researchController.doResearch(testSearchParametersBuilder.build());
        for (Accommodation value : accommodations) {
            accommodationDAO.deleteAccommodation(value.getId());
        }


    }

    @Test
    void booking() throws SQLException, ClassNotFoundException {

        Accommodation accommodation=accommodationDAO.getAccommodationByID(2);
        int disponibilityBefore=accommodation.getDisponibility();

        //senza sconto
        researchController.booking(accommodation, accommodation.getAvailableFrom(), accommodation.getAvailableEnd(), 3, 300, false);
        assertNotEquals(0, researchController.getUser().getFidelityPoints());
        assertEquals(disponibilityBefore-1, accommodation.getDisponibility());

        registerUser=userController.login(testEmail, testPassword);
        ProfileUserController puc=new ProfileUserController(registerUser);
        var MyBookings= puc.viewMyBookings();
        assertEquals(1, MyBookings.size());
        for (Booking booking : MyBookings) {
            assertEquals(booking.getAccommodation().getId(), accommodation.getId());
            assertEquals(booking.getCustomer().getId(), registerUser.getId());
            assertEquals(booking.getCheckInDate(), accommodation.getAvailableFrom());
            assertEquals(booking.getCheckOutDate(), accommodation.getAvailableEnd());
            assertEquals(3, booking.getNumPeople());
            assertEquals(300, booking.getPrice());
        }
        accommodation=accommodationDAO.getAccommodationByID(2);
        disponibilityBefore=accommodation.getDisponibility();

        //con sconto
        researchController.booking(accommodation, accommodation.getAvailableFrom(), accommodation.getAvailableEnd(), 3, 300, true);
        assertEquals(0, researchController.getUser().getFidelityPoints());
        assertEquals(disponibilityBefore-1, accommodation.getDisponibility());

        registerUser=userController.login(testEmail, testPassword);
        puc=new ProfileUserController(registerUser);
        MyBookings= puc.viewMyBookings();
        assertEquals(2, MyBookings.size());
        for (Booking booking : MyBookings) {
            assertEquals(booking.getAccommodation().getId(), accommodation.getId());
            assertEquals(booking.getCustomer().getId(), registerUser.getId());
            assertEquals(booking.getCheckInDate(), accommodation.getAvailableFrom());
            assertEquals(booking.getCheckOutDate(), accommodation.getAvailableEnd());
            assertEquals(3, booking.getNumPeople());
        }
        assertEquals( (int)(300 * 0.7), MyBookings.getLast().getPrice());
    }

    @Test
    void saveAccommodation() throws SQLException, ClassNotFoundException {
        Accommodation accommodation=accommodationDAO.getAccommodationByID(2);
        researchController.saveAccommodation(accommodation);

        ProfileUserController puc=new ProfileUserController(registerUser);
        var MySavings= puc.viewMySavings();
        assertEquals(1, MySavings.size());

        registerUser=userController.login(testEmail, testPassword);
        puc=new ProfileUserController(registerUser);
        MySavings= puc.viewMySavings();
        assertEquals(1, MySavings.size());
        assertEquals(accommodation.getId(), MySavings.getFirst().getId());
    }

    @Test
    void writeReview() {
        LocalDateTime now = LocalDateTime.now();
        accommodationDAO.addAccommodation(
                "Student Hostel Rome",
                "Via Studenti 99",
                "Zurigo",
                20,
                AccommodationType.BnB,
                35.5f,
                now.plusDays(3),
                now.plusDays(30),
                "Ostello per studenti vicino all'università.",
                AccommodationRating.OneStar,
                false,
                true,
                true,
                false,
                false,
                false,
                true,
                false,
                false,
                15,
                false,
                1
        );
        SearchParametersBuilder testSB= SearchParametersBuilder.newBuilder("Zurigo");
        ArrayList<Accommodation> accommodations=researchController.doResearch(testSB.build());
        researchController.writeReview(accommodations.getFirst(), "test test test", AccommodationRating.OneStar);

        //controlliamo le recensioni utente
        ProfileUserController puc=new ProfileUserController(registerUser);
        ArrayList<Review> reviews= puc.getReviewsByUser();
        assertEquals(1, reviews.size());
        assertEquals(reviews.getFirst().getReviewedItem().getId(), accommodations.getFirst().getId());
        assertEquals(reviews.getFirst().getAuthor().getId(), registerUser.getId());
        assertEquals("test test test", reviews.getFirst().getReviewText());
        assertEquals(AccommodationRating.OneStar, reviews.getFirst().getVote());

        // controlliamo le recensioni accommodation
        reviews= researchController.getReviews(accommodations.getFirst());
        assertEquals(1, reviews.size());
        assertEquals(reviews.getFirst().getReviewedItem().getId(), accommodations.getFirst().getId());
        assertEquals(reviews.getFirst().getAuthor().getId(), registerUser.getId());
        assertEquals("test test test", reviews.getFirst().getReviewText());
        assertEquals(AccommodationRating.OneStar, reviews.getFirst().getVote());

        for (Accommodation accommodation : accommodations) {
            accommodationDAO.deleteAccommodation(accommodation.getId());

        }
    }

    @Test
    void applyDiscount() {
        InputStream originalIn = System.in; // salviamo lo stream originale

        try {
            // === Caso 1: Utente idoneo, sceglie Sì (1) ===
            System.setIn(new ByteArrayInputStream("1\n".getBytes()));
            registerUser.setFidelityPoints(15);
            researchController.setUser(registerUser);
            assertTrue(researchController.applyDiscount(400f));

            // === Caso 2: Utente idoneo, sceglie No (2) ===
            System.setIn(new ByteArrayInputStream("2\n".getBytes()));
            registerUser.setFidelityPoints(20);
            researchController.setUser(registerUser);
            assertFalse(researchController.applyDiscount(400f));

            // === Caso 3: Utente NON idoneo (punti insufficienti) ===
            System.setIn(new ByteArrayInputStream("1\n".getBytes())); // anche se inserisce "1", non conta
            registerUser.setFidelityPoints(5);
            researchController.setUser(registerUser);
            assertFalse(researchController.applyDiscount(400f));

            // === Caso 4: Utente NON idoneo (prezzo troppo basso) ===
            System.setIn(new ByteArrayInputStream("1\n".getBytes()));
            registerUser.setFidelityPoints(20);
            researchController.setUser(registerUser);
            assertFalse(researchController.applyDiscount(100f));

        } finally {
            //  Ripristina lo stream originale
            System.setIn(originalIn);
        }
    }

    @Test
    void getReviews() {
        LocalDateTime now = LocalDateTime.now();
        accommodationDAO.addAccommodation(
                "Student Hostel Rome",
                "Via Studenti 99",
                "Zurigo",
                20,
                AccommodationType.BnB,
                35.5f,
                now.plusDays(3),
                now.plusDays(30),
                "Ostello per studenti vicino all'università.",
                AccommodationRating.OneStar,
                false,
                true,
                true,
                false,
                false,
                false,
                true,
                false,
                false,
                15,
                false,
                1
        );
        SearchParametersBuilder testSB= SearchParametersBuilder.newBuilder("Zurigo");
        ArrayList<Accommodation> accommodations=researchController.doResearch(testSB.build());
        researchController.writeReview(accommodations.getFirst(), "test test test", AccommodationRating.OneStar);

        // conntrolliamo che l'accommodation abbia la recensione
        ArrayList<Review> reviews=researchController.getReviews(accommodations.getFirst());
        assertEquals(1, reviews.size());
        assertEquals(reviews.getFirst().getReviewedItem().getId(), accommodations.getFirst().getId());
        assertEquals(reviews.getFirst().getAuthor().getId(), registerUser.getId());
        assertEquals("test test test", reviews.getFirst().getReviewText());
        assertEquals(AccommodationRating.OneStar, reviews.getFirst().getVote());


        // elimino l'alloggio
        for (Accommodation accommodation : accommodations) {
            accommodationDAO.deleteAccommodation(accommodation.getId());

        }
    }
}