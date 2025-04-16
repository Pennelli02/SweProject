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
      }catch (Exception e){
           System.err.println("Error: " + e.getMessage());
      }
    }

    public RegisterUser getUserByEmailPassword(String email, String password) {
        PreparedStatement emailPs=null;
        PreparedStatement passwordPs=null;
        try{
            // Prima verifica se l'email esiste
            String emailQuery = "SELECT * FROM \"users\" WHERE \"email\" = ?";
            emailPs = connection.prepareStatement(emailQuery);
            emailPs.setString(1, email);
            ResultSet emailRs = emailPs.executeQuery();
            if(emailRs.next()){
                //esiste l'email
                String passwordQuery = "SELECT * FROM \"users\" WHERE \"email\" = ? AND \"password\" = ?";
                passwordPs= connection.prepareStatement(passwordQuery);
                passwordPs.setString(1, email);
                passwordPs.setString(2, password);
                ResultSet passwordRs = passwordPs.executeQuery();
                if(passwordRs.next()){
                    //esiste l'utente
                    // recuperiamo i dati
                    int id = passwordRs.getInt("id");
                    String name = passwordRs.getString("name");
                    String surname = passwordRs.getString("surname");
                    String username = passwordRs.getString("username");
                    int fidelityPoints = passwordRs.getInt("fidelitypoints");
                    //String mail =passwordRs.getString("Email");
                    //String pass = passwordRs.getString("Password");
                    // gestione enumerazione
                    String locationString = passwordRs.getString("favouritelocation");
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
            DBUtils.printSQLException(SQLe);
        }finally{
            DBUtils.closeQuietly(emailPs);
            DBUtils.closeQuietly(passwordPs);
        }
        return null;
    }

    //supponiamo che l'email sia unica non teniamo conto della sicurezza
    public String getPassword(String email) {
        PreparedStatement ps=null;
         try {
             String pswdQuery = "SELECT password FROM users WHERE email = ?";
             ps = connection.prepareStatement(pswdQuery);
             ps.setString(1, email);
             ResultSet passwordRs = ps.executeQuery();
             if(passwordRs.next()){
                 return passwordRs.getString("password");
             }
         } catch (SQLException SQLe) {
             DBUtils.printSQLException(SQLe);
         }finally {
             DBUtils.closeQuietly(ps);
         }
        return null;
    }

    public void addUser(String email, String password, String username, String name, String surname, Location favouriteLocation) {
        // 1. Validazione input
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email non può essere vuota");
        }

        // 2. Controllo programmatico esistenza email (opzionale ma utile)
        if (checkEmail(email)) {
            throw new IllegalArgumentException("Email già registrata: " + email);
        }

        // 3. Inserimento con controllo di unicità a livello DB
        String query = "INSERT INTO users (name, surname, email, username, password, favouritelocation) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps=null;
        try  {
            ps = connection.prepareStatement(query);


            ps.setString(1, name);
            ps.setString(2, surname);
            ps.setString(3, email);
            ps.setString(4, username);
            ps.setString(5, password);
            ps.setString(6, favouriteLocation.toString()); // Per tipi complessi come Location
            ps.executeUpdate();


        } catch (SQLException e) {
            // 4. Controllo violazione unique constraint
            if (e.getSQLState().equals("23505")) { // Codice errore per violazione unique constraint
                throw new IllegalArgumentException("Email già registrata: " + email, e);
            }
            DBUtils.printSQLException(e);

        }
        finally {
            DBUtils.closeQuietly(ps);
        }
    }

    public void removeUser(int id) {
        //si suppone che il database agisca on cascade nell'eliminazione
        PreparedStatement ps=null;
        try {
            String query = "DELETE FROM users WHERE id = ?";
            ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            DBUtils.printSQLException(e);
        }finally {
            DBUtils.closeQuietly(ps);
        }
    }

    // per controllare che non ci siano 2 email uguali aggiunta per una doppia sicurezza
    private boolean checkEmail(String email) {
        String query = "SELECT 1 FROM users WHERE email = ? LIMIT 1";
        PreparedStatement ps=null;
        try {
            ps = connection.prepareStatement(query);

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // True se esiste già
            }
        }catch (SQLException SQLe) {
           DBUtils.printSQLException(SQLe);
        }finally {
            DBUtils.closeQuietly(ps);
        }
        return false;
    }

    public String getAdminByPassword(String password) {
        PreparedStatement ps=null;
        try {
            String query = "SELECT email FROM users WHERE password = ? AND isAdmin = TRUE LIMIT 1";
            ps = connection.prepareStatement(query);
            ps.setString(1, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            DBUtils.printSQLException(e);
        }finally {
            DBUtils.closeQuietly(ps);
        }
        return null;
    }

    public ArrayList<RegisterUser> getAllUsers() throws ClassNotFoundException {
        ArrayList<RegisterUser> users = new ArrayList<>();
        PreparedStatement ps=null;
       try {
           String query = "SELECT id FROM users";
           ps = connection.prepareStatement(query);
           ResultSet rs = ps.executeQuery();
           while (rs.next()) {
               RegisterUser user=getUserById(rs.getInt("id"));
               users.add(user);
           }
       } catch (SQLException e) {
           DBUtils.printSQLException(e);
       }finally {
           DBUtils.closeQuietly(ps);
       }

        return users;
    }
    // da gestire il fatto che ci siano 2 funzioni che fanno la stessa cosa
    public RegisterUser getUserById(int id) throws SQLException, ClassNotFoundException {
        PreparedStatement ps=null;
        try {
            String query = "SELECT * FROM users WHERE id = ?";
            ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                String username = rs.getString("username");
                int fidelityPoints = rs.getInt("fidelitypoints");
                String mail =rs.getString("email");
                String pass = rs.getString("password");
                // gestione enumerazione
                String locationString = rs.getString("favouritelocation");
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
        } catch (SQLException e) {
            DBUtils.printSQLException(e);
        }finally {
            DBUtils.closeQuietly(ps);
        }
        return null;
    }

    public void updateName(int id, String newFirstName) {
        if (newFirstName == null || newFirstName.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nuovo nome non può essere vuoto");
        }
        PreparedStatement ps=null;
        try {
            String query = "UPDATE users SET name = ? WHERE id = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, newFirstName);
            ps.setInt(2, id);
            ps.executeUpdate();
            System.out.println("New name updated");
        } catch (SQLException e) {
            DBUtils.printSQLException(e);
        }finally {
            DBUtils.closeQuietly(ps);
        }
    }

    public void updateSurname(int id, String newLastName) {
        if (newLastName == null || newLastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nuovo cognome non può essere vuoto");
        }
        PreparedStatement ps=null;
        try {
            String query = "UPDATE users SET surname = ? WHERE id = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, newLastName);
            ps.setInt(2, id);
            ps.executeUpdate();
            System.out.println("New surname updated");
        } catch (SQLException e) {
            DBUtils.printSQLException(e);
        }finally {
            DBUtils.closeQuietly(ps);
        }
    }

    public void updateFavouriteLocations(int id, Location newLocation) {
        if (newLocation == null) {
            newLocation = Location.Nothing;
        }
        PreparedStatement ps=null;
        try {
            String query = "UPDATE users SET FavouriteLocation = ? WHERE id = ?";
            ps = connection.prepareStatement(query);
            String favouriteLocationString = newLocation.toString();
            ps.setObject(1, favouriteLocationString);
            ps.setInt(2, id);
            ps.executeUpdate();
            System.out.println("New favourite location updated");
        } catch (SQLException e) {
            DBUtils.printSQLException(e);
        }finally {
            DBUtils.closeQuietly(ps);
        }
    }

    public void updateUsername(int id, String newUsername) {
        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("L'username non può essere vuoto");
        }
        PreparedStatement ps=null;
        try {
            String query = "UPDATE users SET username = ? WHERE id = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, newUsername);
            ps.setInt(2, id);
            ps.executeUpdate();
            System.out.println("New username updated");
        } catch (SQLException e) {
            DBUtils.printSQLException(e);
        }finally {
            DBUtils.closeQuietly(ps);
        }
    }

    public void updatePassword(int id, String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La password non può essere vuota");
        }
        PreparedStatement ps=null;
        try {
            String query = "UPDATE users SET password = ? WHERE id = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, newPassword);
            ps.setInt(2, id);
            ps.executeUpdate();
            System.out.println("New password updated");
        } catch (SQLException e) {
            DBUtils.printSQLException(e);
        }finally {
            DBUtils.closeQuietly(ps);
        }
    }

    public void updateEmail(int userId, String newEmail) {
            if (newEmail == null || newEmail.trim().isEmpty()) {
                throw new IllegalArgumentException("La nuova email non può essere vuota");
            }
            if (checkEmail(newEmail)) {
                throw new IllegalArgumentException("Email già registrata: " + newEmail);
            }
            PreparedStatement ps=null;
        try {
            String query = "UPDATE users SET email = ? WHERE id = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, newEmail);
            ps.setInt(2, userId);
            ps.executeUpdate();
            System.out.println("La nuova email registrata: " + newEmail);
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { // Codice errore per violazione unique constraint
                throw new IllegalArgumentException("Email già registrata: " + newEmail, e);
            }
            DBUtils.printSQLException(e);
        }finally {
            DBUtils.closeQuietly(ps);
        }

    }

    // si attiva a ogni pagamento o eliminazione
    public void updateFidPoints(RegisterUser user, float transactionAmount) {
        // Calcola la variazione punti
        int pointsVariation = calculatePointsVariation(transactionAmount);
        int newFidelityPoints = user.getFidelityPoints() + pointsVariation;

        PreparedStatement ps=null;
        try {
            String query = "UPDATE users SET fidelitypoints = ? WHERE id = ?";
            ps = connection.prepareStatement(query);
            ps.setInt(1, newFidelityPoints);
            ps.setInt(2, user.getId());
            ps.executeUpdate();

            user.setFidelityPoints(newFidelityPoints);
            System.out.println("New fidelity points updated");
        } catch (SQLException e) {
            DBUtils.printSQLException(e);
        }finally {
            DBUtils.closeQuietly(ps);
        }
    }

    private int calculatePointsVariation(float amount) {
        // 1 punto ogni 30€ spesi (arrotondato per difetto)
        // Per resi (amount negativo), la variazione sarà negativa
        return (int)(amount / 30);
    }
// si  suppone che un admin abbia un'email che non può modificare di tipo admin@apt.com
    public void updateAdminPassword(String adminEmail, String newPassword) {
        PreparedStatement ps=null;
        try {
            String query = "UPDATE user SET password = ? WHERE email = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, newPassword);
            ps.setString(2, adminEmail);
            ps.executeUpdate();

        }catch (SQLException e){
            DBUtils.printSQLException(e);
        }finally {
            DBUtils.closeQuietly(ps);
        }
    }
}
