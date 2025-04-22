package BusinessLogic;

import DAO.*;
import DomainModel.*;

import java.time.LocalDateTime;
import java.util.ArrayList;


public class ResearchController {
    private RegisterUser user;

    // non so se sia necessario
    public ResearchController(RegisterUser user) {
        this.user = user;
    }

    public ArrayList<Accommodation> doResearch(SearchParameters searchParameters) {
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        return accommodationDAO.getAccommodationByParameter(searchParameters);
    }
    //avvia una prenotazione con tutte le funzioni del caso
    public void booking(Accommodation accommodation, LocalDateTime checkInDate, LocalDateTime checkOutDate, int numOfMembers, int price) {
        if(accommodation.getDisponibility()>0) { // controllo aggiuntivo per sicurezza teoricamente è gestito da addBooking
            BookingDAO bookingDAO = new BookingDAO();
            UserDAO userDAO = new UserDAO();
            AccommodationDAO accommodationDAO = new AccommodationDAO();
            try {
                Booking booking = bookingDAO.addBooking(user, accommodation, checkInDate, checkOutDate, numOfMembers, price);// oltre a restituire un valore lo mettiamo direttamente nel db
                user.addBooking(booking);
                userDAO.updateFidPoints(user, price);
                accommodationDAO.updateAccommodationDisponibility(accommodation.getId(), accommodation.getDisponibility() - 1);
            }catch (RuntimeException e) {
                System.err.println(e.getMessage());
            }
        }else{
            System.out.println("You are not allowed to book this accommodation");
        }
    }

    public void saveAccommodation(Accommodation accommodation) {
        PreferenceDAO preferenceDAO = new PreferenceDAO();
        preferenceDAO.save(user.getId(), accommodation.getId());
        user.addPreference(accommodation);
    }

    public void writeReview(Accommodation accommodation, String content, AccommodationRating rating) {
        ReviewDAO reviewDAO = new ReviewDAO();
        reviewDAO.addReview(user, accommodation, content, rating);
    }

    public ArrayList<Review> getReviews(Accommodation accommodation) {
        ReviewDAO reviewDAO = new ReviewDAO();
        return reviewDAO.getReviewByAccommodation(accommodation);
    }

    public RegisterUser getUser(){
        return user;
    }

    public void setUser(RegisterUser User){
        this.user = user;
    }
}
