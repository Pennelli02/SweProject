package BusinessLogic;

import DAO.AccommodationDAO;
import DAO.BookingDAO;
import DAO.ReviewDAO;
import DAO.UserDAO;
import DomainModel.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class AdminController {
    private boolean isLoggedIn;
    private String adminEmail=null;

    public AdminController() {
        isLoggedIn = false;
    }

    public void deleteAccomodation(int idAccomodation){
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        BookingDAO bookingDAO = new BookingDAO();
        accommodationDAO.deleteAccommodation(idAccomodation);
        bookingDAO.updateBookingsAfterDeleteAccommodation(idAccomodation);
    }


    public void updateAccommodation(Accommodation accommodation){
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        accommodationDAO.updateAccommodationDirty(accommodation);
        accommodation.clearModifiedFields();
    }

 // teniamo conto che se la disponibilità è uguale a zero allora darà errore inoltre quando creo un accommodation di default avrà una stella
    public void addAccommodation(String name, String address, String place, int disponibility, AccommodationType type, float ratePrice, LocalDateTime availableFrom, LocalDateTime availableEnd, String description, boolean refundable, boolean freewifi, boolean haveSmokingArea, boolean haveParking, boolean coffeMachine, boolean roomService, boolean cleaningService, boolean haveSpa, boolean goodForKids, int numberOfRoom, boolean welcomeAnimal, int maxPeople ){
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        AccommodationRating rating= AccommodationRating.OneStar;
        accommodationDAO.addAccommodation( name,  address,  place,  disponibility, type,  ratePrice,  availableFrom, availableEnd, description, rating, refundable,  freewifi,  haveSmokingArea, haveParking,  coffeMachine,  roomService, cleaningService,  haveSpa,  goodForKids, numberOfRoom,  welcomeAnimal, maxPeople);
        
    }
    
    public void removeUser(int idUser) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        userDAO.removeUser(idUser);
    }
    
    public RegisterUser searchUser(int idUser) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        return userDAO.getUserById(idUser);
    }

    public ArrayList<Accommodation> getAllAccomodation() {
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        return accommodationDAO.getAllAccommodation();
    }

    public ArrayList<RegisterUser> getAllUser() throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        return userDAO.getAllUsers();
    }

    public ArrayList<Review> getReviewByUser(RegisterUser user){
        ReviewDAO reviewDAO = new ReviewDAO();
        return reviewDAO.getReviewByUser(user);
    }

    public ArrayList<Review> getReviewByAccomodation(Accommodation accommodation){
        ReviewDAO reviewDAO = new ReviewDAO();
        return reviewDAO.getReviewByAccommodation(accommodation);
    }

    public Accommodation getAccomodationById(int id){
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        return accommodationDAO.getAccommodationByID(id);
    }

    public void exit(){
        isLoggedIn = false;
    }

    public boolean loginAdmin(String password) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        adminEmail = userDAO.getAdminByPassword(password);
        isLoggedIn = adminEmail != null;
        return isLoggedIn;
    }

    public void changePassword(String newPassword) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        userDAO.updateAdminPassword(adminEmail, newPassword);
    }
}
