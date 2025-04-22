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
    //avvia una prenotazione con tutte le funzioni del caso tenere presente che lo sconto funziona con l'uso di un boolenao
    public void booking(Accommodation accommodation, LocalDateTime checkInDate, LocalDateTime checkOutDate, int numOfMembers, int price, boolean applydiscount) {
        if(accommodation.getDisponibility()>0) { // controllo aggiuntivo per sicurezza teoricamente è gestito da addBooking
            BookingDAO bookingDAO = new BookingDAO();
            UserDAO userDAO = new UserDAO();
            AccommodationDAO accommodationDAO = new AccommodationDAO();
            try {
                Booking booking = bookingDAO.addBooking(user, accommodation, checkInDate, checkOutDate, numOfMembers, price);// oltre a restituire un valore lo mettiamo direttamente nel db
                user.addBooking(booking);
                if(applydiscount) {
                    userDAO.resetFidPoints(user.getId());
                }else {
                    userDAO.updateFidPoints(user, price);
                    accommodationDAO.updateAccommodationDisponibility(accommodation.getId(), accommodation.getDisponibility() - 1);
                }
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
    //fixme vedere se è utile nel caso
    public float applyDiscount(float originalPrice) {
        UserDAO userDAO = new UserDAO();
        if (originalPrice > 300) {
            return originalPrice - 300;
        }else{
            throw new RuntimeException("you need to spend more than 300, discount not allowed");
        }
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
