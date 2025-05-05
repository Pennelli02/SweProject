package BusinessLogic;

import DAO.*;
import DomainModel.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;


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
    //avvia una prenotazione con tutte le funzioni del caso tenere presente che lo sconto viene gestito  con l'uso di un boolenao
    public void booking(Accommodation accommodation, LocalDateTime checkInDate, LocalDateTime checkOutDate, int numOfMembers, int price, boolean applydiscount) {
        if(accommodation.getDisponibility()>0) { // controllo aggiuntivo per sicurezza teoricamente è gestito da addBooking
            BookingDAO bookingDAO = new BookingDAO();
            UserDAO userDAO = new UserDAO();
            AccommodationDAO accommodationDAO = new AccommodationDAO();
            try {
                if (applydiscount){
                    price = (int)(price * 0.7);
                }
                Booking booking = bookingDAO.addBooking(user, accommodation, checkInDate, checkOutDate, numOfMembers, price);// oltre a restituire un valore lo mettiamo direttamente nel db
                user.addBooking(booking);
                if(applydiscount) {
                    userDAO.resetFidPoints(user.getId(), user.getFidelityPoints()-10);
                    user.setFidelityPoints(user.getFidelityPoints()-10);
                }else {
                    userDAO.updateFidPoints(user, price);
                }
                accommodationDAO.updateAccommodationDisponibility(accommodation.getId(), accommodation.getDisponibility() - 1);
                accommodation.setDisponibility(accommodation.getDisponibility() - 1);
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

    public boolean applyDiscount(float originalPrice) {
        if (originalPrice > 300 && user.getFidelityPoints() >= 10) {
            float discountPrice = (float)(originalPrice * 0.7);
            Scanner scanner = new Scanner(System.in);
            int choice;
            do{
                System.out.println("You have a lot of points,your future price will be"+discountPrice+" do you want to apply discount? (1 yes, 2 no): ");
                choice = scanner.nextInt();
                switch (choice) {
                    case 1:{
                        return true;
                    }
                    case 2:{
                        return false;
                    }
                    default:{
                        System.out.println("Invalid choice");
                    }
                }
            }while (choice < 1 || choice > 2);
        }
        return false;
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
