package DAO;

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


}
