package DAO;

import DomainModel.Location;
import DomainModel.RegisterUser;

import java.sql.Connection;
import java.sql.SQLException;

public class UserDAO {
    private Connection connection;
    public UserDAO() {
       try{
            this.connection=DatabaseConnection.getInstance().getConnection();
      }catch (Exception e){ // da valutare se  inserire la gestione delle eccezioni
           System.err.println("Error: " + e.getMessage());
      }
    }
    public RegisterUser getUserByEmailPassword(String email, String password) throws SQLException, ClassNotFoundException {
        return null; // giusto per non avere errori
    }
    //supponiamo che l'email sia unica
    public String getPassword(String email) throws SQLException, ClassNotFoundException {
        return null; // giusto per non avere errori
    }
    public void addUser(String email, String password, String username, String name, String surname, Location favouriteLocation) throws SQLException, ClassNotFoundException {
        // da cntrollare che non ci siano email uguali magari una funzione del tipo checkEmail(email)
    }


}
