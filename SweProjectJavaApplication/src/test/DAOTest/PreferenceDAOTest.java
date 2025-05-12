package test.DAOTest;

import BusinessLogic.UserController;
import DAO.AccommodationDAO;
import DAO.PreferenceDAO;
import DAO.UserDAO;
import DomainModel.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PreferenceDAOTest {
    private PreferenceDAO preferenceDAO;
    private UserController userController;
    private UserDAO userDAO;
    private RegisterUser registerUser;
    private AccommodationDAO accommodationDAO;

    LocalDateTime now = LocalDateTime.now();
    int accommodationId;

    private final String testEmail = "test.user@example.com";
    private final String testPassword = "Test123!";
    private final String testUsername = "testuser";
    private final String testName = "Test";
    private final String testSurname = "User";
    private final Location testLocation= Location.Nothing;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        userController = new UserController();
        userDAO = new UserDAO();
        preferenceDAO = new PreferenceDAO();
        accommodationDAO = new AccommodationDAO();

        registerUser=userController.register(testEmail,testPassword,testUsername,testName,testSurname,testLocation);

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
        SearchParameters sp= SearchParametersBuilder.newBuilder("Test").build();
        Accommodation acc= accommodationDAO.getAccommodationByParameter(sp).getFirst();
        accommodationId=acc.getId();
    }

    @AfterEach
    void tearDown() {
        if(registerUser!=null){
            userDAO.removeUser(registerUser.getId());
        }
        accommodationDAO.deleteAccommodation(accommodationId);
    }

    @Test
    void unSave() {
        // inseriamo tra i preferiti
        preferenceDAO.save(registerUser.getId(), accommodationId);
        assertFalse(preferenceDAO.getFavouritesByUser(registerUser.getId()).isEmpty());

        //eliminiamo tra i preferiti
        assertDoesNotThrow(()->preferenceDAO.unSave(registerUser.getId(), accommodationId));
        assertTrue(preferenceDAO.getFavouritesByUser(registerUser.getId()).isEmpty());

    }

    @Test
    void save() throws SQLException, ClassNotFoundException {
        assertDoesNotThrow(() -> preferenceDAO.save(registerUser.getId(),accommodationId));

        registerUser=userController.login(testEmail, testPassword);
        assertFalse(registerUser.getMyPreferences().isEmpty());
    }

    @Test
    void getFavouritesByUser() {
        assertDoesNotThrow(() -> preferenceDAO.getFavouritesByUser(registerUser.getId()));
    }
}