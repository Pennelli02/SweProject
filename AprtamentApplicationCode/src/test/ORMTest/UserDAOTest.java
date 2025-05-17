package test.ORMTest;

import BusinessLogic.UserController;
import ORM.UserDAO;
import DomainModel.Location;
import DomainModel.RegisteredUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {
    private UserController userController;
    private UserDAO userDAO;
    private RegisteredUser registeredUser;

    //per l'utente
    private final String testEmail = "test.user@example.com";
    private final String testPassword = "Test123!";
    private final String testUsername = "testuser";
    private final String testName = "Test";
    private final String testSurname = "User";
    private final Location testLocation= Location.Nothing;

    //per l'admin
    private final String testAdminEmail="admin@apt.com";
    private final String testAdminPassword="adminPassword";
    private final String testAdminFirstName="adminFirstName";
    private final String testAdminLastName="adminLastName";
    private final String testAdminUsername="adminUsername";

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        userController = new UserController();
        userDAO = new UserDAO();

        registeredUser =userController.register(testEmail,testPassword,testUsername,testName,testSurname,testLocation);



    }

    @AfterEach
    void tearDown() {
        if(registeredUser !=null){
            userDAO.removeUser(registeredUser.getId());
        }
    }

    @Test
    void getUserByEmailPassword() {
        registeredUser = assertDoesNotThrow(() -> userDAO.getUserByEmailPassword(testEmail,testPassword));
        assertEquals(testEmail, registeredUser.getEmail());
        assertEquals(testPassword, registeredUser.getPassword());
        assertEquals(testUsername, registeredUser.getUsername());
        assertEquals(testName, registeredUser.getName());
        assertEquals(testSurname, registeredUser.getSurname());
        assertEquals(testLocation, registeredUser.getFavouriteLocations());
    }

    @Test
    void getPassword() {
        String password = assertDoesNotThrow(() -> userDAO.getPassword(testEmail));
        assertEquals(testPassword,password);
    }

    @Test
    void addUser() {
        //testiamo che non inserisca se è presente già l'email
        IllegalArgumentException thrown =assertThrows(IllegalArgumentException.class, () -> userDAO.addUser(testEmail, testPassword, testUsername, testName, testSurname, testLocation));
        assertEquals("Email già registrata: "+ testEmail, thrown.getMessage());

        //testiamo che non inserisca un utente con l'email vuota
        thrown =assertThrows(IllegalArgumentException.class, () -> userDAO.addUser("  ", testPassword, testUsername, testName, testSurname, testLocation));
        assertEquals("Email non può essere vuota", thrown.getMessage());

        //testiamo l'inserimento di un utente
        String testEmail2 = "test2.user@example.com";
        String testPassword2 = "Test1223!";
        String testUsername2 = "testuser2";
        String testName2 = "Test2";
        String testSurname2 = "User2";
        Location testLocation= Location.Nothing;

        assertDoesNotThrow(()->userDAO.addUser(testEmail2, testPassword2, testUsername2, testName2, testSurname2, testLocation));

        //serve per rimuoverlo
        RegisteredUser reg2= userDAO.getUserByEmailPassword(testEmail2, testPassword2);
        userDAO.removeUser(reg2.getId());
    }

    @Test
    void removeUser() throws SQLException, ClassNotFoundException {
        assertDoesNotThrow(() -> userDAO.removeUser(registeredUser.getId()));

        assertNull(registeredUser =userController.login(testEmail, testPassword));
    }

    @Test
    void getAdminByPassword() {
        int adminID= userDAO.createTestAdmin(testAdminEmail,testAdminPassword,testAdminFirstName,testAdminLastName,testAdminUsername);

        assertDoesNotThrow(()->userDAO.getAdminByPassword(testAdminPassword));
        assertEquals(testAdminEmail,userDAO.getAdminByPassword(testAdminPassword));

        // solo per rimuovere l'admin
        userDAO.removeUser(adminID);
    }

    @Test
    void getAllUsers() {
        assertDoesNotThrow(()->userDAO.getAllUsers());
    }

    @Test
    void getUserById() {
        RegisteredUser user= assertDoesNotThrow(()->userDAO.getUserById(registeredUser.getId()));
        assertEquals(testEmail,user.getEmail());
        assertEquals(testPassword,user.getPassword());
        assertEquals(testUsername,user.getUsername());
        assertEquals(testName,user.getName());
        assertEquals(testSurname,user.getSurname());
        assertEquals(testLocation,user.getFavouriteLocations());

    }

    @Test
    void updateName() throws SQLException, ClassNotFoundException {
        String newName= testName+"2";

        assertDoesNotThrow(()->userDAO.updateName(registeredUser.getId(),newName));

        registeredUser = userDAO.getUserById(registeredUser.getId());
        assertEquals(newName, registeredUser.getName());
    }

    @Test
    void updateSurname() throws SQLException, ClassNotFoundException {
        String newSurname= testSurname+"2";

        assertDoesNotThrow(()->userDAO.updateSurname(registeredUser.getId(),newSurname));

        registeredUser =userDAO.getUserById(registeredUser.getId());
        assertEquals(newSurname, registeredUser.getSurname());
    }

    @Test
    void updateFavouriteLocations() throws SQLException, ClassNotFoundException {
        Location newFavouriteLocation= Location.Sea;

        assertDoesNotThrow(()->userDAO.updateFavouriteLocations(registeredUser.getId(),newFavouriteLocation));

        registeredUser =userDAO.getUserById(registeredUser.getId());
        assertEquals(newFavouriteLocation, registeredUser.getFavouriteLocations());
    }

    @Test
    void updateUsername() throws SQLException, ClassNotFoundException {
        String newUsername= testUsername+"2";

        assertDoesNotThrow(()->userDAO.updateUsername(registeredUser.getId(),newUsername));

        registeredUser =userDAO.getUserById(registeredUser.getId());
        assertEquals(newUsername, registeredUser.getUsername());
    }

    @Test
    void updatePassword() throws SQLException, ClassNotFoundException {
        String newPassword= testPassword+"2";
        assertDoesNotThrow(()->userDAO.updatePassword(registeredUser.getId(),newPassword));

        registeredUser =userDAO.getUserById(registeredUser.getId());
        assertEquals(newPassword, registeredUser.getPassword());

    }

    @Test
    void updateEmail() {
        //quando l'email che cambia non esiste
        String newEmail= testEmail+"2";
        assertDoesNotThrow(()->userDAO.updateEmail(registeredUser.getId(),newEmail));

        //testiamo il caso che l'email modificata esista di già nel db
        //inserimento di un utente
        String testEmail2 = "test2.user@example.com";
        String testPassword2 = "Test1223!";
        String testUsername2 = "testuser2";
        String testName2 = "Test2";
        String testSurname2 = "User2";

        userDAO.addUser(testEmail2, testPassword2, testUsername2, testName2, testSurname2, testLocation);
        IllegalArgumentException thrown =assertThrows(IllegalArgumentException.class,()->userDAO.updateEmail(registeredUser.getId(),testEmail2));
        assertEquals("Email già registrata: "+testEmail2,thrown.getMessage());

        RegisteredUser reg2= userDAO.getUserByEmailPassword(testEmail2, testPassword2);
        userDAO.removeUser(reg2.getId());

        //testiamo che l'email cambiata sia vuota e che non la inserisca
        thrown= assertThrows(IllegalArgumentException.class,()->userDAO.updateEmail(registeredUser.getId(),"  "));
        assertEquals("La nuova email non può essere vuota" ,thrown.getMessage());
    }

    @Test
    void updateFidPoints() throws SQLException, ClassNotFoundException {
        int fidPointsBefore= registeredUser.getFidelityPoints();
        assertDoesNotThrow(()->userDAO.updateFidPoints(registeredUser, 49));

        registeredUser =userDAO.getUserById(registeredUser.getId());
        assertNotEquals(fidPointsBefore, registeredUser.getFidelityPoints());
    }

    @Test
    void updateAdminPassword() {
        int adminID= userDAO.createTestAdmin(testAdminEmail,testAdminPassword,testAdminFirstName,testAdminLastName,testAdminUsername);

        String newPassword= testAdminPassword+"2";

        assertDoesNotThrow(()->userDAO.updateAdminPassword(testAdminEmail,newPassword));

        assertEquals(testAdminEmail, userDAO.getAdminByPassword(newPassword));

        userDAO.removeUser(adminID);
    }

    @Test
    void resetFidPoints() throws SQLException, ClassNotFoundException {
        //aggiungiamo dei punti
        userDAO.updateFidPoints(registeredUser, 300);
        registeredUser =userDAO.getUserById(registeredUser.getId());
        assertEquals(10, registeredUser.getFidelityPoints());

        assertDoesNotThrow(()->userDAO.resetFidPoints(registeredUser.getId(), 0));
        registeredUser =userDAO.getUserById(registeredUser.getId());
        assertEquals(0, registeredUser.getFidelityPoints());


    }

    @Test
    void createTestAdmin() throws SQLException, ClassNotFoundException {
        int adminID= assertDoesNotThrow(()->userDAO.createTestAdmin(testAdminEmail,testAdminPassword,testAdminFirstName,testAdminLastName,testAdminUsername));
        assertNotNull(userDAO.getUserById(adminID));

        userDAO.removeUser(adminID);
    }
}