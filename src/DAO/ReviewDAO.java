package DAO;

import DomainModel.Accommodation;
import DomainModel.AccommodationRating;
import DomainModel.RegisterUser;
import DomainModel.Review;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReviewDAO {
    private Connection connection;

    public ReviewDAO() {
        this.connection=DatabaseConnection.getInstance().getConnection();
    }

    // esempio di jdbc
    public ArrayList<Review> getReviewByUser(RegisterUser user) {
        // Inizializza una lista vuota per contenere le recensioni
        ArrayList<Review> reviews = new ArrayList<>();
        //test utilizzando l'email dell'utente come elemento unico
        // Query SQL per selezionare tutte le recensioni associate all'email dell'utente
        String sql = "SELECT * FROM reviews WHERE user_email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Sostituisce il placeholder '?' con l'email dell'utente
            stmt.setString(1, user.getEmail());

            // Esegue la query e ottiene il risultato sotto forma di ResultSet
            ResultSet rs = stmt.executeQuery();

            // Itera su ogni riga del ResultSet
            while (rs.next()) {
                // Recupera i dati della recensione dalla riga corrente
                int id = rs.getInt("id");
                String content = rs.getString("content");
                int accommodationID = rs.getInt("accommodationID");
                int rating = rs.getInt("rating");

                // Conversione del rating usando il nuovo enum
                AccommodationRating accRating = AccommodationRating.OneStar; // Default
                for (AccommodationRating ar : AccommodationRating.values()) {
                    if (ar.getNumericValue() == rating) {
                        accRating = ar;
                        break;
                    }
                }
                AccommodationDAO accommodationDAO = new AccommodationDAO();
                Accommodation acc=accommodationDAO.getAccommodationByID(accommodationID);
                Review review = new Review(id, user, acc, content, accRating);

                // Aggiunge la recensione alla lista
                reviews.add(review);
            }
        } catch (SQLException e) {
            // Stampa l'errore SQL nel caso si verifichi un'eccezione
            e.printStackTrace();
        }

        // Restituisce la lista di recensioni trovate
        return reviews;
    }
///  rating di accommodation si attiva grazie a un trigger
    public void removeReview(int reviewID) {
        String sql = "DELETE FROM reviews WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reviewID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    ///  rating di accommodation si attiva grazie a un trigger
    // utile tener conto di quando è stata pubblicata?
    public void addReview(RegisterUser user, Accommodation accommodation, String content, AccommodationRating rating) {
        try {
            String sql = "INSERT INTO reviews VALUES(?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, user.getEmail());
            stmt.setString(2, accommodation.getName());
            stmt.setString(3, content);
            stmt.setInt(4, rating.getNumericValue());
            stmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Review> getReviewByAccomodation(Accommodation accommodation) {
        ArrayList<Review> reviews = new ArrayList<>();
        try {
            String sql = "SELECT * FROM reviews WHERE accommodationID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, accommodation.getId());
            ResultSet rs = stmt.executeQuery();
            UserDAO userDAO = new UserDAO();
            while (rs.next()) {
                int id = rs.getInt("id");
                String content = rs.getString("content");
                int authorID = rs.getInt("authorID");
                RegisterUser author=userDAO.getUserById(authorID);
                int rating = rs.getInt("rating");
                AccommodationRating accRating = AccommodationRating.OneStar;
                for (AccommodationRating ar : AccommodationRating.values()) {
                    if (ar.getNumericValue() == rating) {
                        accRating = ar;
                        break;
                    }
                }
                Review review= new Review(id, author, accommodation, content, accRating);
                reviews.add(review);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return reviews;
    }

}
