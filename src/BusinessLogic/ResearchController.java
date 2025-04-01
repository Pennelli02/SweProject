package BusinessLogic;

import DAO.AccommodationDAO;
import DAO.BookingDAO;
import DAO.PreferenceDAO;
import DAO.ReviewDAO;
import DomainModel.*;

import java.util.ArrayList;
import java.util.Date;

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

    public void booking(Accommodation accommodation, Date checkInDate, Date checkOutDate, int numOfMembers) {
        BookingDAO bookingDAO = new BookingDAO();
        Booking booking=bookingDAO.addBooking(user, accommodation, checkInDate, checkOutDate, numOfMembers);// oltre a restituire un valore lo mettiamo direttamente nel db
        user.addBooking(booking);
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
    public RegisterUser getUser(){
        return user;
    }

    public void setUser(RegisterUser User){
        this.user = user;
    }
}
