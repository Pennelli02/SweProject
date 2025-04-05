package DAO;

import DomainModel.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        try{
            // Prima verifica se l'email esiste
            String emailQuery = "SELECT * FROM users WHERE email = ?";
            PreparedStatement emailPs = connection.prepareStatement(emailQuery);
            emailPs.setString(1, email);
            ResultSet emailRs = emailPs.executeQuery();
            if(emailRs.next()){
                //esiste l'email
                String passwordQuery = "SELECT * FROM users WHERE email = ? AND password = ?";
                PreparedStatement passwordPs = connection.prepareStatement(passwordQuery);
                passwordPs.setString(1, email);
                passwordPs.setString(2, password);
                ResultSet passwordRs = passwordPs.executeQuery();
                if(passwordRs.next()){
                    //esiste l'utente
                    // recuperiamo i dati
                    int id = passwordRs.getInt("id");
                    String name = passwordRs.getString("Name");
                    String surname = passwordRs.getString("Surname");
                    String username = passwordRs.getString("Username");
                    int fidelityPoints = passwordRs.getInt("FidelityPoints");
                    //String mail =passwordRs.getString("Email");
                    //String pass = passwordRs.getString("Password");
                    // gestione enumerazione
                    String locationString = passwordRs.getString("Location");
                    Location location;
                    if (locationString == null) {
                        location=Location.Nothing;
                    }else {
                        try {
                            location = Location.valueOf(locationString);
                        }catch (IllegalArgumentException e){
                            location=Location.Nothing;
                        }
                    }
                    RegisterUser user = new RegisterUser(id, username, password, email, fidelityPoints, name, surname, location);
                    // gestire le mie prenotazioni
                    BookingDAO bookingDAO = new BookingDAO();
                    ArrayList<Booking> myBookings = new ArrayList<>();
                    bookingDAO.getBookingsFromUser(user);

                    // gestire i miei preferiti
                    PreferenceDAO preferenceDAO = new PreferenceDAO();
                    ArrayList<Accommodation> mySavings= new ArrayList<>();
                    mySavings=preferenceDAO.getFavouritesByUser(id);
                    user.setMyPreferences(mySavings);

                    return user;

                }else{
                    System.out.println("Wrong password");
                    // magari ci si mette qualcosa per indicare al main questa cosa valore boolen?
                    return null;
                }

            }else {
                System.out.println("No such user");
                return null;
            }
        } catch (SQLException SQLe) {
            while( SQLe != null) {
                System.out.println(SQLe.getMessage());
                System.out.print("EC: "+SQLe.getErrorCode());
                System.out.println (" SS: "+SQLe.getSQLState());
                SQLe = SQLe.getNextException();
            }
        }
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
