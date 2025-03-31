package BusinessLogic;

import DAO.BookingDAO;
import DAO.PreferenceDAO;
import DAO.ReviewDAO;
import DAO.UserDAO;
import DomainModel.Accommodation;
import DomainModel.Booking;
import DomainModel.RegisterUser;
import DomainModel.Review;

import java.sql.SQLException;
import java.util.ArrayList;

public class ProfileUserController {
    private RegisterUser user;

    public ProfileUserController(RegisterUser user) {
        this.user = user;
    }

    public void seeProfile() {
        user.showMyPersonalInfo();
    }
    // forse è piu comodo fare l'update per ogni attributo

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
    }
    public void removeReview(Review review) throws SQLException, ClassNotFoundException {
        ReviewDAO reviewDAO=new ReviewDAO();
        reviewDAO.removeReview(review.getReviewID());
    }

    public void unSaveAccommodation(Accommodation accommodation) throws SQLException, ClassNotFoundException {
        PreferenceDAO preferenceDAO=new PreferenceDAO();
        preferenceDAO.unSave(user, accommodation.getId());
    }
}
