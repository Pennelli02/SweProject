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
                    return new RegisterUser(-1 ,email) ; // ci metto l'email che magari può essere utile
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

    public void addUser(String email, String password, String username, String name, String surname, Location favouriteLocation) throws SQLException, ClassNotFoundException {
        // 1. Validazione input
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email non può essere vuota");
        }

        // 2. Controllo programmatico esistenza email (opzionale ma utile)
        if (checkEmail(email)) {
            throw new IllegalArgumentException("Email già registrata: " + email);
        }

        // 3. Inserimento con controllo di unicità a livello DB
        String query = "INSERT INTO users (email, password_hash, username, name, surname, favourite_location, isAdmin) " +
                "VALUES (?, ?, ?, ?, ?, ?, FALSE)";

        try  {
           PreparedStatement ps = connection.prepareStatement(query);


            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, username);
            ps.setString(4, name);
            ps.setString(5, surname);
            ps.setObject(6, favouriteLocation); // Per tipi complessi come Location
            ps.executeUpdate();


        } catch (SQLException e) {
            // 4. Controllo violazione unique constraint
            if (e.getSQLState().equals("23505")) { // Codice errore per violazione unique constraint
                throw new IllegalArgumentException("Email già registrata: " + email, e);
            }

        }
    }

    //ToDo gestire logica di cancellazione utente elimino qualsiasi cosa
    public void removeUser(int id) throws SQLException, ClassNotFoundException {
        //si suppone che il database agisca on cascade nell'eliminazione
        try {
            String query = "DELETE FROM users WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        try {
            String query = "SELECT 1 FROM users WHERE password = ? AND isAdmin = TRUE LIMIT 1";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }
// // suppongo che chi è admin possieda solo un'email di tipo admin@apt? però questo update password rende il tutto più difficile
//    public void updatePassword(String email, String newPassword, Boolean logged) throws SQLException, ClassNotFoundException {
//        if (newPassword == null || newPassword.trim().isEmpty()) {
//            throw new IllegalArgumentException("La password non può essere vuota");
//        }
//        if(logged){
//            try {
//                String query = "UPDATE users SET password = ? WHERE email = ?";
//                PreparedStatement ps = connection.prepareStatement(query);
//                ps.setString(1, newPassword);
//                ps.setString(2, email);
//            }catch (Exception e){
//                throw new RuntimeException(e);
//            }
//        }else {
//            throw new IllegalArgumentException("Non puoi modificare la password");
//        }
//
// }

    public ArrayList<RegisterUser> getAllUsers() throws SQLException, ClassNotFoundException {
        ArrayList<RegisterUser> users = new ArrayList<>();
       try {
           String query = "SELECT id FROM users";
           PreparedStatement ps = connection.prepareStatement(query);
           ResultSet rs = ps.executeQuery();
           while (rs.next()) {
               RegisterUser user=getUserById(rs.getInt("id"));
               users.add(user);
           }
       } catch (Exception e) {
           throw new RuntimeException(e);
       }

        return users;
    }
    // da gestire il fatto che ci siano 2 funzioni che fanno la stessa cosa
    public RegisterUser getUserById(int id) throws SQLException, ClassNotFoundException {
        try {
            String query = "SELECT * FROM users WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString("Name");
                String surname = rs.getString("Surname");
                String username = rs.getString("Username");
                int fidelityPoints = rs.getInt("FidelityPoints");
                String mail =rs.getString("Email");
                String pass = rs.getString("Password");
                // gestione enumerazione
                String locationString = rs.getString("Location");
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
                RegisterUser user = new RegisterUser(id, username, pass, mail, fidelityPoints, name, surname, location);
                // gestire le mie prenotazioni
                BookingDAO bookingDAO = new BookingDAO();
                bookingDAO.getBookingsFromUser(user);

                // gestire i miei preferiti
                PreferenceDAO preferenceDAO = new PreferenceDAO();
                ArrayList<Accommodation> mySavings;
                mySavings=preferenceDAO.getFavouritesByUser(id);
                user.setMyPreferences(mySavings);

                return user;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void updateName(int id, String newFirstName) {
        if (newFirstName == null || newFirstName.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nuovo nome non può essere vuoto");
        }
        try {
            String query = "UPDATE users SET name = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, newFirstName);
            ps.setInt(2, id);
            ps.executeUpdate();
            System.out.println("New name updated");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateSurname(int id, String newLastName) {
        if (newLastName == null || newLastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nuovo cognome non può essere vuoto");
        }
        try {
            String query = "UPDATE users SET surname = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, newLastName);
            ps.setInt(2, id);
            ps.executeUpdate();
            System.out.println("New surname updated");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateFavouriteLocations(int id, Location newLocation) {
        if (newLocation == null) {
            newLocation = Location.Nothing;
        }
        try {
            String query = "UPDATE users SET FavouriteLocation = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            String favouriteLocationString = newLocation.toString();
            ps.setObject(1, favouriteLocationString);
            ps.setInt(2, id);
            ps.executeUpdate();
            System.out.println("New favourite location updated");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateUsername(int id, String newUsername) {
        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("L'username non può essere vuoto");
        }
        try {
            String query = "UPDATE users SET username = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, newUsername);
            ps.setInt(2, id);
            ps.executeUpdate();
            System.out.println("New username updated");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePassword(int id, String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La password non può essere vuota");
        }
        try {
            String query = "UPDATE users SET password = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, newPassword);
            ps.setInt(2, id);
            ps.executeUpdate();
            System.out.println("New password updated");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    // si attiva a ogni pagamento o eliminazione
    public void updateFidPoints(RegisterUser user, float transactionAmount) {
        // Calcola la variazione punti
        int pointsVariation = calculatePointsVariation(transactionAmount);
        int newFidelityPoints = user.getFidelityPoints() + pointsVariation;


        try {
            String query = "UPDATE users SET FidelityPoints = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, newFidelityPoints);
            ps.setInt(2, user.getId());
            ps.executeUpdate();

            user.setFidelityPoints(newFidelityPoints);
            System.out.println("New fidelity points updated");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int calculatePointsVariation(float amount) {
        // 1 punto ogni 30€ spesi (arrotondato per difetto)
        // Per resi (amount negativo), la variazione sarà negativa
        return (int)(amount / 30);
    }
}
