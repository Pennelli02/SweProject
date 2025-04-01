package BusinessLogic;

import DAO.BookingDAO;
import DAO.PreferenceDAO;
import DAO.ReviewDAO;
import DAO.UserDAO;
import DomainModel.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class ProfileUserController {
    private RegisterUser user;

    public ProfileUserController(RegisterUser user) {
        this.user = user;
    }

    public void seeProfile() {
        user.showMyPersonalInfo();
    }

    public void updateProfile(String newFirstName, String newLastName, String newEmail, String newpPassword, String newUsername, Location newLocation) {
        UserDAO userDAO = new UserDAO();
        if(!Objects.equals(newFirstName, user.getName())) {
            userDAO.updateName(newFirstName);
            user.setName(newFirstName);
        }
        if(!Objects.equals(newLastName, user.getSurname())) {
            userDAO.updateSurname(newLastName);
            user.setSurname(newLastName);
        }
        if(!Objects.equals(newEmail, user.getEmail())) {
            userDAO.updateEmail(newEmail);
            user.setEmail(newEmail);
        }
        if(!Objects.equals(newpPassword, user.getPassword())) {
            userDAO.updatePassword(newpPassword);
            user.setPassword(newpPassword);
        }
        if(!Objects.equals(newUsername, user.getUsername())) {
            userDAO.updateUsername(newUsername);
            user.setUsername(newUsername);
        }
        if(newLocation != user.getFavouriteLocations()) {
            userDAO.updateFavouriteLocations(newLocation);
            user.setFavouriteLocations(newLocation);
        }
    }

    //logout
    public void exit(){
        user=null; // penso basti
    }

    public void unRegister() throws SQLException, ClassNotFoundException {
        UserDAO userDAO=new UserDAO();
        userDAO.removeUser(user);
        user=null;
    }

    public ArrayList<Accommodation> viewMySavings() {
        //UserDAO userDAO=new UserDAO(); vediamo se va bene o no... cioè o si fa qui o si fa nel login
        return user.getMyPreferences();
    }
    public ArrayList<Booking> viewMyBookings() {
        return user.getMyBookings();
    }

    public ArrayList<Review> viewMyReviews() {
        ReviewDAO reviewDAO=new ReviewDAO();
        return reviewDAO.getReviewByUser(user);
    }

    public void removeBooking(Booking booking) throws SQLException, ClassNotFoundException {
        BookingDAO bookingDAO=new BookingDAO();
        bookingDAO.removeBooking(booking.getBookingID());
        user.removeBooking(booking);
    }
    public void removeReview(Review review) throws SQLException, ClassNotFoundException {
        ReviewDAO reviewDAO=new ReviewDAO();
        reviewDAO.removeReview(review.getReviewID());
    }

    public void unSaveAccommodation(Accommodation accommodation) throws SQLException, ClassNotFoundException {
        PreferenceDAO preferenceDAO=new PreferenceDAO();
        preferenceDAO.unSave(user, accommodation.getId());
        user.removePreference(accommodation);
    }
}
