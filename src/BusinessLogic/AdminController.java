package BusinessLogic;

import DAO.AccommodationDAO;
import DAO.BookingDAO;
import DAO.ReviewDAO;
import DAO.UserDAO;
import DomainModel.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class AdminController {
    private boolean isLoggedIn;

    //testing
    public String getAdminEmail() {
        return adminEmail;
    }

    private String adminEmail = null;

    public AdminController() {
        isLoggedIn = false;
    }

    public void deleteAccommodation(int idAccommodation) {
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        BookingDAO bookingDAO = new BookingDAO();
        bookingDAO.updateBookingsAfterDeleteAccommodation(idAccommodation);
        accommodationDAO.deleteAccommodation(idAccommodation);

    }

    public void updateAccommodation(Accommodation accommodation) {
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        accommodationDAO.updateAccommodationDirty(accommodation);
        accommodation.clearModifiedFields();
    }

    // teniamo conto che se la disponibilità è uguale a zero allora darà errore inoltre quando creo un accommodation di default avrà una stella
    public void addAccommodation(String name, String address, String place, int disponibility, AccommodationType type, float ratePrice, String description, LocalDateTime availableFrom, LocalDateTime availableEnd, boolean coffeMachine, boolean roomService, boolean welcomeAnimal, int numberOfRoom, boolean goodForKids, boolean haveSpa, boolean cleaningService, boolean refundable, boolean freewifi, boolean haveSmokingArea, boolean haveParking, int maxPeople) {
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        AccommodationRating rating = AccommodationRating.OneStar;
        try {
            accommodationDAO.addAccommodation(name, address, place, disponibility, type, ratePrice, availableFrom, availableEnd, description, rating, refundable, freewifi, haveSmokingArea, haveParking, coffeMachine, roomService, cleaningService, haveSpa, goodForKids, numberOfRoom, welcomeAnimal, maxPeople);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }

    public void removeUser(int idUser) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        ArrayList<Accommodation> accommodations = accommodationDAO.getAccommodationFromUser(idUser);
        for (Accommodation accommodation : accommodations) {
            accommodationDAO.updateAccommodationDisponibility(accommodation.getId(), accommodation.getDisponibility() + 1);
        }
        userDAO.removeUser(idUser);
    }

    public RegisteredUser searchUser(int idUser) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        return userDAO.getUserById(idUser);
    }

    public ArrayList<Accommodation> getAllAccommodation() {
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        return accommodationDAO.getAllAccommodation();
    }

    public ArrayList<RegisteredUser> getAllUser() throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        return userDAO.getAllUsers();
    }

    public ArrayList<Review> getAllReview() throws SQLException, ClassNotFoundException {
        ReviewDAO reviewDAO = new ReviewDAO();
        return reviewDAO.getAllReview();
    }

    public void removeReview(int idReview) {
        ReviewDAO reviewDAO = new ReviewDAO();
        reviewDAO.removeReview(idReview);
    }

    public ArrayList<Review> getReviewByUser(RegisteredUser user) {
        ReviewDAO reviewDAO = new ReviewDAO();
        return reviewDAO.getReviewByUser(user);
    }

    public ArrayList<Review> getReviewByAccommodation(Accommodation accommodation) {
        ReviewDAO reviewDAO = new ReviewDAO();
        return reviewDAO.getReviewByAccommodation(accommodation);
    }

    public Accommodation getAccommodationById(int id) {
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        return accommodationDAO.getAccommodationByID(id);
    }

    public void exit() {
        isLoggedIn = false;
    }

    public boolean loginAdmin(String password) {
        UserDAO userDAO = new UserDAO();
        adminEmail = userDAO.getAdminByPassword(password);
        isLoggedIn = adminEmail != null;
        return isLoggedIn;
    }

    public void changePassword(String newPassword) {
        UserDAO userDAO = new UserDAO();
        try {
            userDAO.updateAdminPassword(adminEmail, newPassword);
            System.out.println("Password changed");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }
}

