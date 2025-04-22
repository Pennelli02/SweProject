package BusinessLogic;

import java.sql.SQLException;
import DAO.UserDAO;
import DomainModel.Location;
import DomainModel.RegisterUser;
public class UserController {
    public UserController() {}
    public RegisterUser login(String email, String password) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        return userDAO.getUserByEmailPassword(email, password);
    }

    public RegisterUser register(String email, String password, String username, String name, String surname, Location favouriteLocation) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        try {
            userDAO.addUser(email, password, username, name, surname, favouriteLocation); // fornisce errore o comunque un messaggio di avviso se ci sono 2 email uguali...
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
        return userDAO.getUserByEmailPassword(email, password);
    }

    public String getForgottenPassword(String email) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        return userDAO.getPassword(email);
    }
}
