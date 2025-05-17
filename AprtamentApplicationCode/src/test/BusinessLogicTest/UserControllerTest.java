package test.BusinessLogicTest;

import BusinessLogic.UserController;
import ORM.UserDAO;
import DomainModel.Location;
import DomainModel.RegisteredUser;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;



class UserControllerTest {

    private UserController userController;
    private UserDAO userDAO;
    private RegisteredUser user;

    private final String testEmail = "test.user@example.com";
    private final String testPassword = "Test123!";
    private final String testUsername = "testuser";
    private final String testName = "Test";
    private final String testSurname = "User";
    private final Location testLocation= Location.Nothing;

    @BeforeEach
    void setUp() {
         userController = new UserController();
         userDAO = new UserDAO();
    }

    @AfterEach
    void tearDown() throws SQLException, ClassNotFoundException {
        userDAO.removeUser(user.getId());
    }

    @Test
    void login() throws SQLException, ClassNotFoundException {
        //testiamo il login di una persona registrata nel database
        user=userController.register(testEmail, testPassword, testUsername, testName, testSurname, testLocation);
        RegisteredUser loginUser=userController.login(testEmail, testPassword);
        assertNotNull(loginUser);
        assertEquals(testUsername,loginUser.getUsername());
        assertEquals(testPassword,loginUser.getPassword());
        assertEquals(testEmail,loginUser.getEmail());
        assertEquals(testName,loginUser.getName());
        assertEquals(testSurname,loginUser.getSurname());
        assertEquals(testLocation, loginUser.getFavouriteLocations());

        //testiamo il login nel caso di una persona non registrata
        String testEmail2="test2@gmail.com";
        String testPassword2="Test1234!";
        loginUser=userController.login(testEmail2, testPassword2);
        assertNull(loginUser);


        //testiamo il login nel caso uno metta la password errata, ma l'email giusta

        loginUser=userController.login(testEmail, testPassword2);
        //possibilità di recupero password
        assertEquals(-1, loginUser.getId());
        assertEquals(testEmail, loginUser.getEmail());

    }

    @Test
    void register() throws SQLException, ClassNotFoundException {
        //testiamo il caso che vada tutto bene
        user=userController.register(testEmail, testPassword, testUsername, testName, testSurname, testLocation);
        assertNotNull(user);
        assertEquals(testEmail, user.getEmail());
        assertEquals(testPassword, user.getPassword());
        assertEquals(testUsername, user.getUsername());
        assertEquals(testName, user.getName());
        assertEquals(testSurname, user.getSurname());
        assertEquals(testLocation, user.getFavouriteLocations());

        //testiamo il caso che un utente provi a registrarsi con un email già usata
        RegisteredUser user2=userController.register(testEmail, "test", "test", testName, testSurname, testLocation);
        assertNull(user2); // non viene registrato

        //testiamo il caso che inserisca degli spazi al posto dell'email
        user2=userController.register("    ", "test2", "test", testName, testSurname, testLocation);
        assertNull(user2); // non viene registrato
    }

    @Test
    void getForgottenPassword() throws SQLException, ClassNotFoundException {
        // caso che l'email sia giusta
        user=userController.register(testEmail, testPassword, testUsername, testName, testSurname, testLocation);
        assertNotNull(user);
        String myPassword = userController.getForgottenPassword(testEmail);
        assertEquals(testPassword, myPassword);

        // caso che l'email sia sbagliata
        String testEmail2="testEmail@gmail.com";
        myPassword = userController.getForgottenPassword(testEmail2);
        assertNotEquals(testPassword, myPassword);
        assertNull(myPassword);
    }
}