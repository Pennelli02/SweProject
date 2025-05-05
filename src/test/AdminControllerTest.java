package test;

import BusinessLogic.AdminController;
import BusinessLogic.ProfileUserController;
import BusinessLogic.ResearchController;
import BusinessLogic.UserController;
import DAO.AccommodationDAO;
import DAO.BookingDAO;
import DAO.UserDAO;
import DomainModel.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdminControllerTest {
    private AdminController adminController;

    private int idAdmin;

    private final String testAdminEmail="admin@apt.com";
    private final String testAdminPassword="adminPassword";
    private final String testAdminFirstName="adminFirstName";
    private final String testAdminLastName="adminLastName";
    private final String testAdminUsername="adminUsername";

    private final String testEmail = "test.user@example.com";
    private final String testPassword = "Test123!";
    private final String testUsername = "testuser";
    private final String testName = "Test";
    private final String testSurname = "User";
    private final Location testLocation= Location.Nothing;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        adminController = new AdminController();
    }
//
//    @AfterEach
//    void tearDown() {
//    }

    @Test
    void deleteAccommodation() throws SQLException, ClassNotFoundException {

        AccommodationDAO accommodationDAO = new AccommodationDAO();
        // creiamo un'accommodation

        accommodationDAO.addAccommodation(
                "Student Hostel Rome",
                "Via Studenti 99",
                "test",
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
        UserController userController = new UserController();
        UserDAO userDAO = new UserDAO();

        //simuliamo l'utente che cerca questo alloggio
        RegisterUser testUser=userController.register(testEmail,testPassword,testUsername,testName,testSurname,testLocation);
        ResearchController researchController= new ResearchController(testUser);
        SearchParametersBuilder testSCP= SearchParametersBuilder.newBuilder("test");
        ArrayList<Accommodation> accommodations=researchController.doResearch(testSCP.build());
        assertFalse(accommodations.isEmpty());

        //cacelliamo con l'admin l'alloggio
        adminController.deleteAccommodation(accommodations.getFirst().getId());

        //simulando la ricerca da parte dell'utente
        ArrayList<Accommodation> accommodationsAfter=researchController.doResearch(testSCP.build());
        assertTrue(accommodationsAfter.isEmpty());

        //effettiva ricerca tramite id
        Accommodation accomm= accommodationDAO.getAccommodationByID(accommodations.getFirst().getId());
        assertNull(accomm);

        //controlliamo adesso che modifichi anche le prenotazioni associate
        accommodationDAO.addAccommodation(
                "Student Hostel Rome",
                "Via Studenti 99",
                "test",
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

        ArrayList<Accommodation> accommodations2=researchController.doResearch(testSCP.build());
        researchController.booking(accommodations2.getFirst(), now.plusDays(4), now.plusDays(10), 2, 150, false);

        adminController.deleteAccommodation(accommodations2.getFirst().getId());

        //refresh
        testUser=userController.login(testEmail,testPassword);
        ProfileUserController puc= new ProfileUserController(testUser);
        var MyBookings= puc.viewMyBookings();
        //supponiamo che il test abbia una sola prenotazione
        for (Booking booking : MyBookings) {
            assertEquals(State.Accommodation_Cancelled, booking.getState());
            assertNull(booking.getAccommodation());
        }

       Accommodation result= accommodationDAO.getAccommodationByID(accommodations2.getFirst().getId());
        assertNull(result);


        //rimuovere l'accommodation dopo il test
        for(Accommodation accommodation:accommodations){
            accommodationDAO.deleteAccommodation(accommodation.getId());
        }

        //rimuovere l'utente di prova dopo il test
        userDAO.removeUser(testUser.getId());

    }

    @Test
    void updateAccommodation() throws SQLException, ClassNotFoundException{
        AccommodationDAO accommodationDAO = new AccommodationDAO();

        // 1. Creazione di una accommodation
        LocalDateTime now = LocalDateTime.now();
        accommodationDAO.addAccommodation(
                "Test Hotel",
                "Via Test 123",
                "Test",
                10,
                AccommodationType.Hotel,
                100.0f,
                now.plusDays(1),
                now.plusDays(10),
                "Descrizione iniziale",
                AccommodationRating.ThreeStar,
                true,
                true,
                false,
                true,
                false,
                false,
                false,
                true,
                false,
                5,
                true,
                2
        );

        // 2. Recupera l'accommodation
        SearchParameters params = SearchParametersBuilder.newBuilder("Test").build();
        ResearchController rc = new ResearchController(new RegisterUser()); // finto utente
        List<Accommodation> results = rc.doResearch(params);
        assertFalse(results.isEmpty());

        Accommodation accommodation = results.getFirst();
        int id = accommodation.getId();

        // 3. Modifica alcuni campi e marca come modificati
        accommodation.setName("Updated Test Hotel");

        accommodation.setRatePrice(120.0f);

        accommodation.setDescription("Nuova descrizione test");

        // 4. Chiamata all’update
        AdminController adminController = new AdminController();
        adminController.updateAccommodation(accommodation);

        // 5. Recupera nuovamente dal DB e verifica i cambiamenti
        Accommodation updated = accommodationDAO.getAccommodationByID(id);
        assertNotNull(updated);

        assertEquals("Updated Test Hotel", updated.getName());
        assertEquals(120.0f, updated.getRatePrice());
        assertEquals("Nuova descrizione test", updated.getDescription());

        // 6. Pulisce dopo il test
        accommodationDAO.deleteAccommodation(id);
    }


    @Test
    void addAccommodation() {
        //aggiunta valida
        AdminController adminController = new AdminController();
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        LocalDateTime now = LocalDateTime.now();

        // 1. Aggiunta valida
        adminController.addAccommodation(
                "Test Add Hotel",
                "Via Roma 10",
                "Test",
                5,
                AccommodationType.Hotel,
                50.0f,
                now.plusDays(1),
                now.plusDays(10),
                "Hotel di test",
                true,
                true,
                false,
                true,
                true,
                false,
                false,
                false,
                true,
                3,
                true,
                2
        );

        // 2. Ricerca per verificare inserimento
        SearchParameters params = SearchParametersBuilder.newBuilder("test").build();
        ResearchController rc = new ResearchController(new RegisterUser()); // fake user
        List<Accommodation> results = rc.doResearch(params);
        assertFalse(results.isEmpty());

        Accommodation found = results.stream()
                .filter(a -> a.getName().equals("Test Add Hotel"))
                .findFirst()
                .orElse(null);

        assertNotNull(found);
        assertEquals(AccommodationRating.OneStar, found.getRating());

        // 3. Pulizia
        accommodationDAO.deleteAccommodation(found.getId());


        //test quando la disponibilità viene messa a zero
         adminController = new AdminController();
         now = LocalDateTime.now();

        // Cattura l'output d'errore
        PrintStream originalErr = System.err;
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        adminController.addAccommodation(
                "Hotel Fallimento",
                "Via Zero 1",
                "Torino",
                0, // disponibilità zero, errore atteso
                AccommodationType.Hotel,
                60.0f,
                now.plusDays(1),
                now.plusDays(5),
                "Dovrebbe fallire",
                true,
                false,
                false,
                true,
                false,
                false,
                true,
                true,
                false,
                1,
                false,
                2
        );

        // Ripristina output standard e verifica che ci sia stato errore
        System.setErr(originalErr);
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("disponibility should be greater than 0"));
    }



    @Test
    void removeUser() throws SQLException, ClassNotFoundException {
        // test con un utente che ha delle prenotazioni
        UserController userController = new UserController();
        RegisterUser regUser=userController.register(testEmail, testPassword, testUsername, testName, testSurname, testLocation);
        assertNotNull(regUser);

        // Recupera l'utente
        UserDAO userDAO = new UserDAO();
        RegisterUser user = userDAO.getUserByEmailPassword(testEmail, testPassword);
        assertNotNull(user);

        int userId = user.getId();

        // Aggiungi una accommodation per l’utente
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        LocalDateTime now = LocalDateTime.now();
        accommodationDAO.addAccommodation(
                "Test House", "Via Test 1", "test", 3,
                AccommodationType.Hotel, 45.0f, now.plusDays(1), now.plusDays(10),
                "Alloggio test", AccommodationRating.OneStar,
                true, true, false, false, false, false, false,
                false, false, 2, false, 4);

        SearchParameters params = SearchParametersBuilder.newBuilder("Test").build();
        ResearchController rc = new ResearchController(regUser);
        List<Accommodation> results = rc.doResearch(params);

        BookingDAO bookingDAO = new BookingDAO();
        bookingDAO.addBooking(regUser, results.getFirst(), now.plusDays(1), now.plusDays(10), 3, 500);

        regUser = userController.login(testEmail, testPassword);
        assertNotNull(regUser);

        // Recupera l'accommodation per verificarne la disponibilità
        ArrayList<Accommodation> accommodations = accommodationDAO.getAccommodationFromUser(userId);
        assertFalse(accommodations.isEmpty());

        Accommodation accommodation = accommodations.getFirst();
        int oldDisponibility = accommodation.getDisponibility();

        // Rimuove l'utente
        AdminController adminController = new AdminController();
        adminController.removeUser(userId);

        // Verifica che l’utente sia stato rimosso
        assertNull(userDAO.getUserByEmailPassword(testEmail, testPassword));

        // Verifica che la disponibilità sia aumentata
        Accommodation updatedAccommodation = accommodationDAO.getAccommodationByID(accommodation.getId());
        assertEquals(oldDisponibility + 1, updatedAccommodation.getDisponibility());

        // Cleanup finale
        accommodationDAO.deleteAccommodation(accommodation.getId());

        //test utente senza prenotazioni associate
        RegisterUser regUser2 = userController.register(testEmail, testPassword, testUsername, testName, testSurname, testLocation);
        assertNotNull(regUser2);

        RegisterUser user2= userDAO.getUserByEmailPassword(testEmail, testPassword);
        assertNotNull(user2);

        int user2Id = user2.getId();

        // Verifica che non ci siano accommodation associate

        ArrayList<Accommodation> accommodations2 = accommodationDAO.getAccommodationFromUser(user2Id);
        assertTrue(accommodations2.isEmpty());

        // Rimuove l’utente
        adminController.removeUser(user2Id);

        // Verifica che l’utente sia stato rimosso
        assertNull(userDAO.getUserByEmailPassword(testEmail, testPassword));

    }

    @Test
    void searchUser() throws SQLException, ClassNotFoundException {
        UserController userController = new UserController();
        RegisterUser regUser=userController.register(testEmail, testPassword, testUsername, testName, testSurname, testLocation);
        assertNotNull(regUser);
        int userId = regUser.getId();

        AdminController adminController = new AdminController();
        RegisterUser foundUser = adminController.searchUser(userId);
        assertNotNull(foundUser);

        assertEquals(userId, foundUser.getId());
        assertEquals(testEmail, foundUser.getEmail());
        assertEquals(testUsername, foundUser.getUsername());

        UserDAO userDAO = new UserDAO();
        userDAO.removeUser(userId);

    }

    @Test
    void getAllAccommodation() {
    }

    @Test
    void getAllUser() {
    }

    @Test
    void getReviewByUser() {
    }

    @Test
    void getReviewByAccommodation() {
    }

    @Test
    void getAccommodationById() {
    }

    @Test
    void exit() {
    }

    @Test
    void loginAdmin() {
    }

    @Test
    void changePassword() {
    }
}