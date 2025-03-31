package BusinessLogic;
import java.util.ArrayList;
import java.sql.SQLException;
import DAO.UserDAO;
import DomainModel.RegisterUser;
public class UserController {
    public UserController() {}
    public RegisterUser login(String email, String password) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        return userDAO.getUserByEmailPassword(email, password);
    }

    public void register(){

    }

    public String getForgottenPassword(String email) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        return userDAO.getPassword(email);
    }
}
