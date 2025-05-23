package test.ORMTest;

import BusinessLogic.UserController;
import ORM.AccommodationDAO;
import ORM.BookingDAO;
import ORM.UserDAO;
import DomainModel.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BookingDAOTest {
    private BookingDAO bookingDAO;
    private RegisteredUser registeredUser;
    private UserController userController;
    private UserDAO userDAO;
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
        bookingDAO = new BookingDAO();
        userController = new UserController();
        userDAO = new UserDAO();

        registeredUser =userController.register(testEmail,testPassword,testUsername,testName,testSurname,testLocation);


        //associamo all'utente di test alcune prenotazioni così possiamo fare i testing
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
        SearchParameters sp= SearchParametersBuilder.newBuilder("Test").build();
        ArrayList<Accommodation> acc= accommodationDAO.getAccommodationByParameter(sp);
        bookingDAO.addBooking(registeredUser, acc.getFirst(), acc.getFirst().getAvailableFrom(), acc.getFirst().getAvailableEnd(), 3, 400);
        accommodationId=acc.getFirst().getId();
        registeredUser =userController.login(testEmail,testPassword);
    }

    @AfterEach
    void tearDown() {
        if(registeredUser !=null){
            userDAO.removeUser(registeredUser.getId());
            accommodationDAO.deleteAccommodation(accommodationId);
        }

    }

    @Test
    void removeBooking() throws SQLException, ClassNotFoundException {
       ArrayList<Booking>myBookings= bookingDAO.getBookingsFromUser(registeredUser);
       assertFalse(myBookings.isEmpty());

       //controlliamo il caso di rimozione non possibile
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> bookingDAO.removeBooking(myBookings.getFirst().getBookingID(), myBookings.getFirst().getState())// il valore è BookingConfirmed
        );

        assertTrue(thrown.getMessage().contains("You can't delete booking from confirmed booking or in state of checking, you have to remove first"));

        assertFalse(myBookings.isEmpty());
        myBookings.getFirst().setState(State.Booking_Refunded); // settiamo un valore per testare il caso di rimozione possibile

        assertDoesNotThrow(()->bookingDAO.removeBooking(myBookings.getFirst().getBookingID(), myBookings.getFirst().getState()));

        registeredUser =userController.login(testEmail, testPassword);
        assertTrue(bookingDAO.getBookingsFromUser(registeredUser).isEmpty());
    }

    @Test
    void addBooking() {
        Accommodation acc= accommodationDAO.getAccommodationByID(accommodationId);
         assertDoesNotThrow(() ->
                bookingDAO.addBooking(registeredUser, acc, acc.getAvailableFrom(), acc.getAvailableEnd(), 3, 400)
        );
        assertFalse(bookingDAO.getBookingsFromUser(registeredUser).isEmpty());

    }

    @Test
    void getBookingsFromUser() {
        assertDoesNotThrow(()->bookingDAO.getBookingsFromUser(registeredUser));
    }

    @Test
    void cancelBook() {
        assertDoesNotThrow(()->bookingDAO.cancelBook(bookingDAO.getBookingsFromUser(registeredUser).getFirst()));
        Booking updated = bookingDAO.getBookingsFromUser(registeredUser).getFirst();
        assertTrue(updated.getState() == State.Booking_Refunded || updated.getState() == State.Cancelled);
    }

    @Test
    void updateBookingsAfterDeleteAccommodation() {
        assertDoesNotThrow(()->bookingDAO.updateBookingsAfterDeleteAccommodation(accommodationId));
    }
}