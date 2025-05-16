package test.BusinessLogicTest;

import BusinessLogic.ProfileUserController;
import BusinessLogic.ResearchController;
import BusinessLogic.UserController;
import ORM.AccommodationDAO;
import ORM.UserDAO;
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
    private RegisteredUser registeredUser;
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

        registeredUser =userController.register(testEmail,testPassword,testUsername,testName,testSurname,testLocation);

        researchController=new ResearchController(registeredUser);
        profileUserController= new ProfileUserController(registeredUser);

    }

    @AfterEach
    void tearDown() {
        if (registeredUser !=null){
            ArrayList<Integer> ids=new ArrayList<>();
            if(profileUserController.viewMyBookings()!=null){
                ArrayList<Accommodation>accommodations=accommodationDAO.getAccommodationFromUser(registeredUser.getId());
                for(Accommodation accommodation:accommodations){
                    ids.add(accommodation.getId());
                }
            }
            profileUserController.unRegister();
            for(Integer id : ids){
                accommodationDAO.deleteAccommodation(id);
            }
        }
        researchController=null;
    }

    @Test
    void doResearch() {


        // suppongo che ci siano già delle Accommodation nel db
        LocalDateTime now = LocalDateTime.now();
        accommodationDAO.addAccommodation(
                "Test Apartment",
                "Via Test 123",
                "Milano",
                4,
                AccommodationType.Apartment,
                100.0f,
                now.minusDays(1),
                now.plusDays(30),
                "Appartamento di test",
                AccommodationRating.FiveStar,
                true,
                true,
                false,
                true,
                true,
                true,
                true,
                false,
                true,
                5,
                true,
                4
        );


        // proviamo a vedere utilizzando solo il luogo
        testSearchParametersBuilder= SearchParametersBuilder.newBuilder("Milano");

        // avviamo la ricerca
        ArrayList<Accommodation> accommodations= researchController.doResearch(testSearchParametersBuilder.build());

        //controlliamo che restituisca qualcolsa
        assertNotNull(accommodations);

        //controlliamo che per i risultati ottenuti abbiano tutti lo stesso luogo
        for (Accommodation value : accommodations) {
            assertEquals("Milano", value.getPlace());
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
            assertEquals("Milano", value.getPlace());
            accommodationDAO.deleteAccommodation(value.getId());
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
        LocalDateTime now = LocalDateTime.now();
        // Creazione dinamica di un alloggio di test
        accommodationDAO.addAccommodation("Test Hotel","Via Test Booking 1","Test",10, AccommodationType.Hotel,200.0f, now.plusDays(1),
                now.plusDays(10),"Hotel di test per booking", AccommodationRating.FourStar, true, true,false,true,  // parking
                false, true, true,false,true,5,true,3);
        SearchParameters SPM=SearchParametersBuilder.newBuilder("Test").build();
        ArrayList<Accommodation> accommodations= researchController.doResearch(SPM);
        int disponibilityBefore=accommodations.getFirst().getDisponibility();

        //senza sconto
        researchController.booking(accommodations.getFirst(), accommodations.getFirst().getAvailableFrom(), accommodations.getFirst().getAvailableEnd(), 3, 300, false);
        assertNotEquals(0, researchController.getUser().getFidelityPoints());
        assertEquals(disponibilityBefore-1, accommodations.getFirst().getDisponibility());

        registeredUser =userController.login(testEmail, testPassword);
        ProfileUserController puc=new ProfileUserController(registeredUser);
        var MyBookings= puc.viewMyBookings();
        assertEquals(1, MyBookings.size());
        for (Booking booking : MyBookings) {
            assertEquals(booking.getAccommodation().getId(), accommodations.getFirst().getId());
            assertEquals(booking.getCustomer().getId(), registeredUser.getId());
            assertEquals(booking.getCheckInDate(), accommodations.getFirst().getAvailableFrom());
            assertEquals(booking.getCheckOutDate(), accommodations.getFirst().getAvailableEnd());
            assertEquals(3, booking.getNumPeople());
            assertEquals(300, booking.getPrice());
        }

        disponibilityBefore=accommodations.getFirst().getDisponibility();

        //con sconto
        researchController.booking(accommodations.getFirst(), accommodations.getFirst().getAvailableFrom(), accommodations.getFirst().getAvailableEnd(), 3, 300, true);
        assertEquals(0, researchController.getUser().getFidelityPoints());
        assertEquals(disponibilityBefore-1, accommodations.getFirst().getDisponibility());

        registeredUser =userController.login(testEmail, testPassword);
        puc=new ProfileUserController(registeredUser);
        MyBookings= puc.viewMyBookings();
        assertEquals(2, MyBookings.size());
        for (Booking booking : MyBookings) {
            assertEquals(booking.getAccommodation().getId(), accommodations.getFirst().getId());
            assertEquals(booking.getCustomer().getId(), registeredUser.getId());
            assertEquals(booking.getCheckInDate(), accommodations.getFirst().getAvailableFrom());
            assertEquals(booking.getCheckOutDate(), accommodations.getFirst().getAvailableEnd());
            assertEquals(3, booking.getNumPeople());
        }
        assertEquals( (int)(300 * 0.7), MyBookings.getLast().getPrice());

    }

    @Test
    void saveAccommodation() throws SQLException, ClassNotFoundException {
        LocalDateTime now = LocalDateTime.now();
        // Creazione dinamica di un alloggio di test
        accommodationDAO.addAccommodation(
                "Test Hotel",
                "Via Test Booking 1",
                "Test",
                10,
                AccommodationType.Hotel,
                200.0f,
                now.plusDays(1),
                now.plusDays(10),
                "Hotel di test per booking",
                AccommodationRating.FourStar,
                true,  // refundable
                true,  // freewifi
                false, // smoking
                true,  // parking
                false, // coffee
                true,  // room service
                true,  // cleaning
                false, // spa
                true,  // kids
                5,
                true,
                3
        );
        SearchParameters SPM=SearchParametersBuilder.newBuilder("Test").build();
        ArrayList<Accommodation> accommodations= researchController.doResearch(SPM);
        researchController.saveAccommodation(accommodations.getFirst());

        ProfileUserController puc=new ProfileUserController(registeredUser);
        var MySavings= puc.viewMySavings();
        assertEquals(1, MySavings.size());

        registeredUser =userController.login(testEmail, testPassword);
        puc=new ProfileUserController(registeredUser);
        MySavings= puc.viewMySavings();
        assertEquals(1, MySavings.size());
        assertEquals(accommodations.getFirst().getId(), MySavings.getFirst().getId());

        accommodationDAO.deleteAccommodation(accommodations.getFirst().getId());
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
        ProfileUserController puc=new ProfileUserController(registeredUser);
        ArrayList<Review> reviews= puc.getReviewsByUser();
        assertEquals(1, reviews.size());
        assertEquals(reviews.getFirst().getReviewedItem().getId(), accommodations.getFirst().getId());
        assertEquals(reviews.getFirst().getAuthor().getId(), registeredUser.getId());
        assertEquals("test test test", reviews.getFirst().getReviewText());
        assertEquals(AccommodationRating.OneStar, reviews.getFirst().getVote());

        // controlliamo le recensioni accommodation
        reviews= researchController.getReviews(accommodations.getFirst());
        assertEquals(1, reviews.size());
        assertEquals(reviews.getFirst().getReviewedItem().getId(), accommodations.getFirst().getId());
        assertEquals(reviews.getFirst().getAuthor().getId(), registeredUser.getId());
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
            registeredUser.setFidelityPoints(15);
            researchController.setUser(registeredUser);
            assertTrue(researchController.applyDiscount(400f));

            // === Caso 2: Utente idoneo, sceglie No (2) ===
            System.setIn(new ByteArrayInputStream("2\n".getBytes()));
            registeredUser.setFidelityPoints(20);
            researchController.setUser(registeredUser);
            assertFalse(researchController.applyDiscount(400f));

            // === Caso 3: Utente NON idoneo (punti insufficienti) ===
            System.setIn(new ByteArrayInputStream("1\n".getBytes())); // anche se inserisce "1", non conta
            registeredUser.setFidelityPoints(5);
            researchController.setUser(registeredUser);
            assertFalse(researchController.applyDiscount(400f));

            // === Caso 4: Utente NON idoneo (prezzo troppo basso) ===
            System.setIn(new ByteArrayInputStream("1\n".getBytes()));
            registeredUser.setFidelityPoints(20);
            researchController.setUser(registeredUser);
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
        assertEquals(reviews.getFirst().getAuthor().getId(), registeredUser.getId());
        assertEquals("test test test", reviews.getFirst().getReviewText());
        assertEquals(AccommodationRating.OneStar, reviews.getFirst().getVote());


        // elimino l'alloggio
        for (Accommodation accommodation : accommodations) {
            accommodationDAO.deleteAccommodation(accommodation.getId());

        }
    }
}