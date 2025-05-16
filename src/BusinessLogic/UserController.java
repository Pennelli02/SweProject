package BusinessLogic;

import java.sql.SQLException;
import DAO.UserDAO;
import DomainModel.Location;
import DomainModel.RegisteredUser;
public class UserController {
    public UserController() {}
    public RegisteredUser login(String email, String password) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        return userDAO.getUserByEmailPassword(email, password);
    }

    public RegisteredUser register(String email, String password, String username, String name, String surname, Location favouriteLocation) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        try {
            userDAO.addUser(email, password, username, name, surname, favouriteLocation); // fornisce errore o comunque un messaggio di avviso se ci sono 2 email uguali...
            return login(email, password);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public String getForgottenPassword(String email) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        return userDAO.getPassword(email);
    }
}
