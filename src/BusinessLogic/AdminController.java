package BusinessLogic;

import DAO.AccommodationDAO;
import DAO.ReviewDAO;
import DAO.UserDAO;
import DomainModel.Accommodation;
import DomainModel.RegisterUser;
import DomainModel.Review;

import java.sql.SQLException;
import java.util.ArrayList;

public class AdminController {
    private boolean isLoggedIn;

    public AdminController() {
        isLoggedIn = false;
    }

    public void deleteAccomodation(int idAccomodation){
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        accommodationDAO.deleteAccommodation(idAccomodation);
    }

    public void updateAccomodation(){

    }

    public void addAccomodation(){
        
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
        return accommodationDAO.getAllAccomodation();
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

    /// non so se tenerla è praticamente inutile e difficile da implementare
    public void updatePassword(String password, String newPassword) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        userDAO.updatePassword(password, newPassword, isLoggedIn);
    }

    public boolean loginAdmin(String password) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        isLoggedIn = userDAO.getAdminByPassword(password);
        return isLoggedIn;
    }
}
