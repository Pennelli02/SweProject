package DAO;

import DomainModel.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReviewDAO {
    private Connection connection;

    public ReviewDAO() {
        try {
            this.connection=DatabaseConnection.getInstance().getConnection();
        }catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // esempio di jdbc
    public ArrayList<Review> getReviewByUser(RegisterUser user) {
        // Inizializza una lista vuota per contenere le recensioni
        ArrayList<Review> reviews = new ArrayList<>();
        //test utilizzando l'email dell'utente come elemento unico
        // Query SQL per selezionare tutte le recensioni associate all'email dell'utente
        String sql = "SELECT * FROM reviews WHERE userId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Sostituisce il placeholder '?' con l'email dell'utente
            stmt.setInt(1, user.getId());

            // Esegue la query e ottiene il risultato sotto forma di ResultSet
            ResultSet rs = stmt.executeQuery();

            // Itera su ogni riga del ResultSet
            while (rs.next()) {
                // Recupera i dati della recensione dalla riga corrente
                int id = rs.getInt("id");
                String content = rs.getString("commenttext");
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
            DBUtils.printSQLException(e);
        }

        // Restituisce la lista di recensioni trovate
        return reviews;
    }
//  rating di accommodation si attiva grazie a un trigger nel db
    public void removeReview(int reviewID) {
        String sql = "DELETE FROM reviews WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reviewID);
            stmt.executeUpdate();
            System.out.println("Successfully deleted review");
        } catch (Exception e) {
            System.out.println("Error something went wrong: " + e.getMessage());
        }
    }
    //  rating di accommodation si attiva grazie a un trigger nel db
    public void addReview(RegisterUser user, Accommodation accommodation, String content, AccommodationRating rating) {
       PreparedStatement stmt = null;
        try {
            String sql = "INSERT INTO reviews (userid, accommodationid, rating, commenttext) VALUES(?, ?, ?, ?)";
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, user.getId());
            stmt.setInt(2, accommodation.getId());
            stmt.setInt(3, rating.getNumericValue());
            stmt.setString(4, content);
            stmt.executeUpdate();

        } catch (SQLException e) {
            DBUtils.printSQLException(e);
        }finally {
            DBUtils.closeQuietly(stmt);
        }
    }

    public ArrayList<Review> getReviewByAccommodation(Accommodation accommodation) {
        ArrayList<Review> reviews = new ArrayList<>();
        PreparedStatement stmt = null;
        try {
            String sql = "SELECT * FROM reviews WHERE accommodationId = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, accommodation.getId());
            ResultSet rs = stmt.executeQuery();
            UserDAO userDAO = new UserDAO();
            while (rs.next()) {
                int id = rs.getInt("id");
                String content = rs.getString("commenttext");
                int authorID = rs.getInt("userId");
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
        } catch (SQLException e) {
           DBUtils.printSQLException(e);
        }catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }finally {
            DBUtils.closeQuietly(stmt);
        }
        return reviews;
    }

    public ArrayList<Review> getAllReview() {
        ArrayList<Review> reviews = new ArrayList<>();
        PreparedStatement stmt = null;
        try {
            String sql = "SELECT * FROM reviews";
            stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            UserDAO userDAO = new UserDAO();
            AccommodationDAO accommodationDAO = new AccommodationDAO();
            while (rs.next()) {
                int id = rs.getInt("id");
                String content = rs.getString("commenttext");
                int authorID = rs.getInt("userId");
                RegisterUser author=userDAO.getUserById(authorID);
                int accommodationid = rs.getInt("accommodationId");
                Accommodation accommodation=accommodationDAO.getAccommodationByID(accommodationid);
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
        } catch (SQLException e) {
            DBUtils.printSQLException(e);
        }catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }finally {
            DBUtils.closeQuietly(stmt);
        }
        return reviews;
    }
}
