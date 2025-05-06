package test.DAOTest;

import BusinessLogic.UserController;
import DAO.AccommodationDAO;
import DAO.BookingDAO;
import DAO.UserDAO;
import DomainModel.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AccommodationDAOTest {
    private AccommodationDAO accommodationDAO;
    private final ArrayList<Integer> testAccommodationIds = new ArrayList<>();
    LocalDateTime now = LocalDateTime.now();

    private final String testEmail = "test.user@example.com";
    private final String testPassword = "Test123!";
    private final String testUsername = "testuser";
    private final String testName = "Test";
    private final String testSurname = "User";
    private final Location testLocation= Location.Nothing;


    @BeforeEach
    void setUp() {
        accommodationDAO=new AccommodationDAO();


        accommodationDAO.addAccommodation(
                "Family Spa Apartment",
                "Via delle Terme 12",
                "Test",
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
                "Test",
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
                "Test",
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

        SearchParameters msp= SearchParametersBuilder.newBuilder("Test").build();
        ArrayList<Accommodation> accommodations=accommodationDAO.getAccommodationByParameter(msp);
        for (Accommodation accommodation : accommodations) {
            testAccommodationIds.add(accommodation.getId());
        }

    }

    @AfterEach
    void tearDown() {
        for (Integer id : new ArrayList<>(testAccommodationIds)) {
            accommodationDAO.deleteAccommodation(id);
        }
        testAccommodationIds.clear();
    }

    @Test
    void getAccommodationByID() {
        Accommodation accommodation=accommodationDAO.getAccommodationByID(testAccommodationIds.getFirst());
        assertNotNull(accommodation);

        //sapendo a priori i dati dell'alloggio
        assertEquals("Family Spa Apartment",accommodation.getName());
        assertEquals("Via delle Terme 12",accommodation.getAddress());
        assertEquals("Test",accommodation.getPlace());
        assertEquals(2, accommodation.getDisponibility());
        assertEquals(AccommodationType.Apartment,accommodation.getType());
        assertEquals(150.0f,accommodation.getRatePrice());
        assertEquals(AccommodationRating.FourStar,accommodation.getRating());
        assertTrue(accommodation.getAvailableFrom().isEqual(now.plusDays(5)));
        assertTrue(accommodation.getAvailableEnd().isEqual(now.plusDays(20)));
        assertEquals("Appartamento familiare con centro benessere incluso.", accommodation.getDescription());
        assertFalse(accommodation.isRefundable());
        assertTrue(accommodation.isFreewifi());
        assertFalse(accommodation.isHaveSmokingArea());
        assertFalse(accommodation.isHaveParking());
        assertTrue(accommodation.isCoffeMachine());
        assertTrue(accommodation.isRoomService());
        assertTrue(accommodation.isCleaningService());
        assertTrue(accommodation.isHaveSpa());
        assertTrue(accommodation.isGoodForKids());
        assertTrue(accommodation.isWelcomeAnimal());
        assertEquals(3, accommodation.getNumberOfRoom());
        assertEquals(4, accommodation.getMaxNumberOfPeople());

        ;



    }

    @Test
    void getAccommodationByParameter() {
        SearchParameters msp= SearchParametersBuilder.newBuilder("Test").setCategory(AccommodationType.Apartment).build();
        assertDoesNotThrow(()-> accommodationDAO.getAccommodationByParameter(msp));
    }

    @Test
    void getAllAccommodation() {
        assertDoesNotThrow(()-> accommodationDAO.getAllAccommodation());
    }

    @Test
    void deleteAccommodation() {
        assertDoesNotThrow(()->accommodationDAO.deleteAccommodation(testAccommodationIds.getFirst()));
        testAccommodationIds.remove(testAccommodationIds.getFirst());
    }

//    @Test
//    void updateRating() { viene fatto con il trigger nel database
//    }

    @Test
    void addAccommodation() {
        assertDoesNotThrow(()->accommodationDAO.addAccommodation(
                "Student bnb Test",
                "Via Studenti test 99",
                "AddTest",
                20,
                AccommodationType.BnB,
                35.5f,
                now.plusDays(3),
                now.plusDays(30),
                "Test per studenti vicino all'università.",
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
        ));
        SearchParameters msp= SearchParametersBuilder.newBuilder("ADDTest").build();
        ArrayList<Accommodation> accommodations=accommodationDAO.getAccommodationByParameter(msp);
        assertNotNull(accommodations);
        assertFalse(accommodations.isEmpty());
        for (Accommodation accommodation : accommodations) {
            accommodationDAO.deleteAccommodation(accommodation.getId());
        }

    }

    @Test
    void updateAccommodationDisponibility() {
        assertDoesNotThrow(()->accommodationDAO.updateAccommodationDisponibility(testAccommodationIds.getFirst(), accommodationDAO.getAccommodationByID(testAccommodationIds.getFirst()).getDisponibility()));
    }


    @Test
    void updateAccommodationDirty() {
        assertDoesNotThrow(()->accommodationDAO.updateAccommodationDirty(accommodationDAO.getAccommodationByID(testAccommodationIds.getFirst())));
    }

    @Test
    void getAccommodationFromUser() throws SQLException, ClassNotFoundException {
        //creiamo un utente
        UserController uc = new UserController();
        BookingDAO bdao = new BookingDAO();
        RegisterUser ru = uc.register(testEmail,testPassword,testUsername,testName,testSurname,testLocation);
        //associamo delle prenotazioni all'utente
        Accommodation accommodation = accommodationDAO.getAccommodationByID(testAccommodationIds.getFirst());
        bdao.addBooking(ru, accommodation, accommodation.getAvailableFrom(), accommodation.getAvailableEnd(), 4, 500);
        assertDoesNotThrow(()->accommodationDAO.getAccommodationFromUser(ru.getId()));

        UserDAO udao = new UserDAO();
        udao.removeUser(ru.getId());
    }
}