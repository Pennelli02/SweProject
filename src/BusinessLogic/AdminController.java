package BusinessLogic;

import DAO.AccommodationDAO;
import DAO.ReviewDAO;
import DAO.UserDAO;
import DomainModel.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class AdminController {
    private boolean isLoggedIn;

    public AdminController() {
        isLoggedIn = false;
    }

    public void deleteAccomodation(int idAccomodation){
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        accommodationDAO.deleteAccommodation(idAccomodation);
    }
    //TODO da implemtare tale funzione
    public void updateAccomodation(){

    }
 // teniamo conto che se la disponibilità è uguale a zero allora darà errore
    public void addAccommodation(String name, String address, String place, int disponibility, AccommodationType type, float ratePrice, LocalDateTime availableFrom, LocalDateTime availableEnd, String description, AccommodationRating rating, boolean refundable, boolean freewifi, boolean haveSmokingArea, boolean haveParking, boolean coffeMachine, boolean roomService, boolean cleaningService, boolean haveSpa, boolean goodForKids, int numberOfRoom, boolean welcomeAnimal, int maxPeople ){
        AccommodationDAO accommodationDAO = new AccommodationDAO();
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

    public ArrayList<Accommodation> getAllAccomodation() throws SQLException, ClassNotFoundException {
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
        return reviewDAO.getReviewByAccomodation(accommodation);
    }

    public Accommodation getAccomodationById(int id){
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        return accommodationDAO.getAccommodationByID(id);
    }

    public void exit(){
        isLoggedIn = false;
    }

    //fixme non so se tenerla è praticamente inutile e difficile da implementare
//    public void updatePassword(String newPassword) throws SQLException, ClassNotFoundException {
//        String emailAdmin="admin@gmail.com";
//        UserDAO userDAO = new UserDAO();
//        userDAO.updatePassword(emailAdmin, newPassword, isLoggedIn);
//    }

    public boolean loginAdmin(String password) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        isLoggedIn = userDAO.getAdminByPassword(password);
        return isLoggedIn;
    }
}
