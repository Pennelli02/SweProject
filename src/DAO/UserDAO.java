package DAO;

import DomainModel.Location;
import DomainModel.RegisterUser;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

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
        // da controllare che non ci siano email uguali magari una funzione del tipo checkEmail(email)
    }
    public void removeUser(int id) throws SQLException, ClassNotFoundException {
        // ce la gestiamo qui magari si può usare direttamente l'email
    }
    // per controllare che non ci siano 2 email uguali
    public boolean checkEmail(String email) throws SQLException, ClassNotFoundException {
        return false;
    }

    public boolean getAdminByPassword(String password) throws SQLException, ClassNotFoundException {
        boolean controller = false;
        return controller;
    }

    public ArrayList<RegisterUser> getAllUsers() throws SQLException, ClassNotFoundException {
        ArrayList<RegisterUser> users = new ArrayList<>();
        return users;
    }

    public RegisterUser getUserById(int id) throws SQLException, ClassNotFoundException {
        return null;
    }

    public void updateName(String newFirstName) {
        
    }

    public void updateSurname(String newLastName) {
    }

    public void updateFavouriteLocations(Location newLocation) {
    }

    public void updateUsername(String newUsername) {
    }

    public void updatePassword(String newpPassword) {
    }

    public void updateEmail(String newEmail) {
    }
}
