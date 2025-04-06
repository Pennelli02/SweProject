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
      }catch (Exception e){ // da valutare se inserire la gestione delle eccezioni
           System.err.println("Error: " + e.getMessage());
      }
    }
    // valuatare come gestire i tre casi
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
                    bookingDAO.getBookingsFromUser(user);

                    // gestire i miei preferiti
                    PreferenceDAO preferenceDAO = new PreferenceDAO();
                    ArrayList<Accommodation> mySavings;
                    mySavings=preferenceDAO.getFavouritesByUser(id);
                    user.setMyPreferences(mySavings);

                    return user;

                }else{
                    System.out.println("Wrong password");
                    // magari ci si mette qualcosa per indicare al main questa cosa valore boolean?
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
        return null;
    }

    //supponiamo che l'email sia unica non teniamo conto della sicurezza
    public String getPassword(String email) throws SQLException, ClassNotFoundException {
         try {
             String pswdQuery = "SELECT password FROM users WHERE email = ?";
             PreparedStatement ps = connection.prepareStatement(pswdQuery);
             ps.setString(1, email);
             ResultSet passwordRs = ps.executeQuery();
             if(passwordRs.next()){
                 return passwordRs.getString("password");
             }
         } catch (RuntimeException e) {
             throw new RuntimeException(e);
         }
        return null;
    }
    public RegisterUser addUser(String email, String password, String username, String name, String surname, Location favouriteLocation) throws SQLException, ClassNotFoundException {
        // 1. Validazione input
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email non può essere vuota");
        }

        // 2. Controllo programmatico esistenza email (opzionale ma utile)
        if (checkEmail(email)) {
            throw new IllegalArgumentException("Email già registrata: " + email);
        }

        // 3. Inserimento con controllo di unicità a livello DB
        String query = "INSERT INTO users (email, password_hash, username, name, surname, favourite_location) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try  {
           PreparedStatement ps = connection.prepareStatement(query);


            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, username);
            ps.setString(4, name);
            ps.setString(5, surname);
            ps.setObject(6, favouriteLocation); // Per tipi complessi come Location

            ps.executeUpdate();
            ResultSet rs = ps.executeQuery();
            RegisterUser user;
           if (rs.next()) {
               int id = rs.getInt("id"); // se vogliamo usare l'id... altrimenti elimino tutto e uso solo l'email
               return new RegisterUser(id, username, password, email, 0, name, surname, favouriteLocation);
           }
            throw new SQLException("Failed to get generated ID");

        } catch (SQLException e) {
            // 4. Controllo violazione unique constraint
            if (e.getSQLState().equals("23505")) { // Codice errore per violazione unique constraint
                throw new IllegalArgumentException("Email già registrata: " + email, e);
            }

        }
        return null;
    }

    public void removeUser(int id) throws SQLException, ClassNotFoundException {
        // ce la gestiamo qui magari si può usare direttamente l'email
    }

    // per controllare che non ci siano 2 email uguali aggiunta per una doppia sicurezza
    private boolean checkEmail(String email) throws SQLException, ClassNotFoundException {
        String query = "SELECT 1 FROM users WHERE email = ? LIMIT 1";
        try {
             PreparedStatement ps = connection.prepareStatement(query);

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // True se esiste già
            }
        }catch (Exception e){
            throw new SQLException(e.getMessage());
        }
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

    public void updateName(int id, String newFirstName) {
        
    }

    public void updateSurname(int id, String newLastName) {
    }

    public void updateFavouriteLocations(int id, Location newLocation) {
    }

    public void updateUsername(int id, String newUsername) {
    }

    public void updatePassword(int id, String newPassword) {
    }

    public void updateEmail(int userId, String newEmail) throws SQLException, ClassNotFoundException {
            if (newEmail == null || newEmail.trim().isEmpty()) {
                throw new IllegalArgumentException("La nuova email non può essere vuota");
            }
            if (checkEmail(newEmail)) {
                throw new IllegalArgumentException("Email già registrata: " + newEmail);
            }
        try {
            String query = "UPDATE users SET email = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, newEmail);
            ps.setInt(2, userId);
            ps.executeUpdate();
            System.out.println("La nuova email registrata: " + newEmail);
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { // Codice errore per violazione unique constraint
                throw new IllegalArgumentException("Email già registrata: " + newEmail, e);
            }
            throw new RuntimeException(e);
        }

    }
}
