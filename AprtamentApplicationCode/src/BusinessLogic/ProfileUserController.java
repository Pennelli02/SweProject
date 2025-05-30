package BusinessLogic;

import ORM.*;
import DomainModel.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class ProfileUserController {
    private RegisteredUser user;

    public ProfileUserController(RegisteredUser user) {
        this.user = user;
    }

    public void seeProfile() {
        user.showMyPersonalInfo();
    }

    public void updateProfile(String newFirstName, String newLastName, String newEmail, String newpPassword, String newUsername, Location newLocation) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        try {
            if (newFirstName != null && !Objects.equals(newFirstName, user.getName())) {
                userDAO.updateName(user.getId(), newFirstName);
                user.setName(newFirstName);
            }
            if (newLastName != null && !Objects.equals(newLastName, user.getSurname())) {
                userDAO.updateSurname(user.getId(), newLastName);
                user.setSurname(newLastName);
            }
            if (newEmail != null && !Objects.equals(newEmail, user.getEmail())) {
                userDAO.updateEmail(user.getId(), newEmail);
                user.setEmail(newEmail);
            }
            if (newpPassword != null && !Objects.equals(newpPassword, user.getPassword())) {
                userDAO.updatePassword(user.getId(), newpPassword);
                user.setPassword(newpPassword);
            }
            if (newUsername != null && !Objects.equals(newUsername, user.getUsername())) {
                userDAO.updateUsername(user.getId(), newUsername);
                user.setUsername(newUsername);
            }
            if (newLocation != null && newLocation != user.getFavouriteLocations()) {
                userDAO.updateFavouriteLocations(user.getId(), newLocation);
                user.setFavouriteLocations(newLocation);
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //logout
    public void exit(){
        user=null; // penso basti
    }

    public void unRegister() {
        UserDAO userDAO=new UserDAO();
        AccommodationDAO accommodationDAO=new AccommodationDAO();
        ArrayList <Accommodation> accommodations=accommodationDAO.getAccommodationFromUser(user.getId());
        for(Accommodation accommodation:accommodations) {
            accommodationDAO.updateAccommodationDisponibility(accommodation.getId(), accommodation.getDisponibility()+1);
        }
        userDAO.removeUser(user.getId());
        user=null;
    }

    public ArrayList<Accommodation> viewMySavings() {
        return user.getMyPreferences();
    }

    public ArrayList<Booking> viewMyBookings() {
        BookingDAO bookingDAO=new BookingDAO();
        return bookingDAO.getBookingsFromUser(user);
    }

    public ArrayList<Review> viewMyReviews() {
        ReviewDAO reviewDAO=new ReviewDAO();
        return reviewDAO.getReviewByUser(user);
    }


    // cancella una prenotazione ma non la rimuove e attiva tutte le funzioni del caso
    public void cancelABooking(Booking booking) {
        BookingDAO bookingDAO=new BookingDAO();
        bookingDAO.cancelBook(booking);
        AccommodationDAO accommodationDAO=new AccommodationDAO();
        UserDAO userDAO=new UserDAO();
        userDAO.updateFidPoints(user, -booking.getPrice());
        accommodationDAO.updateAccommodationDisponibility(booking.getAccommodation().getId(), booking.getAccommodation().getDisponibility()+1);
    }

    // si può fare se e solo se lo stato della prenotazione è Checking out, Cancelled, Refunded, accommodation cancelled
    public void removeBooking(Booking booking) {
        BookingDAO bookingDAO=new BookingDAO();
        try {
            bookingDAO.removeBooking(booking.getBookingID(), booking.getState());
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }

    public void removeReview(Review review) {
        ReviewDAO reviewDAO=new ReviewDAO();
        reviewDAO.removeReview(review.getReviewID());
    }

    public void unSaveAccommodation(Accommodation accommodation) {
        PreferenceDAO preferenceDAO=new PreferenceDAO();
        preferenceDAO.unSave(user.getId(), accommodation.getId());
    }

    public ArrayList<Review> getReviewsByUser() {
        ReviewDAO reviewDAO = new ReviewDAO();
        return reviewDAO.getReviewByUser(user);
    }
}
