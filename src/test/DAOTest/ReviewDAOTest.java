package test.DAOTest;

import BusinessLogic.UserController;
import DAO.AccommodationDAO;
import DAO.ReviewDAO;
import DAO.UserDAO;
import DomainModel.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ReviewDAOTest {
    private ReviewDAO reviewDAO;
    private UserController userController;
    private UserDAO userDAO;
    private RegisteredUser registeredUser;
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
        reviewDAO = new ReviewDAO();
        accommodationDAO = new AccommodationDAO();

        registeredUser =userController.register(testEmail,testPassword,testUsername,testName,testSurname,testLocation);

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
        if(registeredUser !=null){
            userDAO.removeUser(registeredUser.getId());
        }
        accommodationDAO.deleteAccommodation(accommodationId);
    }

    @Test
    void getReviewByUser() {
        ArrayList<Review> reviews=assertDoesNotThrow(()->reviewDAO.getReviewByUser(registeredUser));
        assertNotNull(reviews);
        assertTrue(reviews.isEmpty());

        // aggiungiamo delle recensioni per test
        reviewDAO.addReview(registeredUser, accommodationDAO.getAccommodationByID(accommodationId), "test test test", AccommodationRating.FourStar);
        reviews=assertDoesNotThrow(()->reviewDAO.getReviewByUser(registeredUser));
        assertNotNull(reviews);
        assertFalse(reviews.isEmpty());
    }

    @Test
    void removeReview() {
        //aggiungo una recensione per il test
        reviewDAO.addReview(registeredUser, accommodationDAO.getAccommodationByID(accommodationId), "test test test", AccommodationRating.FourStar);
        Review review= reviewDAO.getReviewByUser(registeredUser).getFirst();
        assertDoesNotThrow(()->reviewDAO.removeReview(review.getReviewID()));

        assertTrue(reviewDAO.getReviewByUser(registeredUser).isEmpty());
    }

    @Test
    void addReview() {
        assertDoesNotThrow(()->reviewDAO.addReview(registeredUser, accommodationDAO.getAccommodationByID(accommodationId), "test test test", AccommodationRating.FourStar));
        assertNotNull(reviewDAO.getReviewByUser(registeredUser).getFirst());
    }

    @Test
    void getReviewByAccommodation() {
        //aggiungiamo una recensione per il test
        reviewDAO.addReview(registeredUser, accommodationDAO.getAccommodationByID(accommodationId), "test test test", AccommodationRating.FourStar);
        Review review= assertDoesNotThrow(()->reviewDAO.getReviewByAccommodation(accommodationDAO.getAccommodationByID(accommodationId)).getFirst());
        assertNotNull(review);
    }

    @Test
    void getAllReviews() {
        //aggiungiamo una recensione per il test
        reviewDAO.addReview(registeredUser, accommodationDAO.getAccommodationByID(accommodationId), "test test test", AccommodationRating.FourStar);
        ArrayList<Review>reviews=assertDoesNotThrow(()->reviewDAO.getAllReview());
        assertNotNull(reviews);
        assertFalse(reviews.isEmpty());

    }
}