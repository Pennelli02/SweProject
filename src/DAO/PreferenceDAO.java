package DAO;

import DomainModel.Accommodation;
import DomainModel.AccommodationRating;
import DomainModel.AccommodationType;
import DomainModel.RegisterUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PreferenceDAO {
    private Connection connection;

    public PreferenceDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public void unSave(int userID, int AccommodationID) {
        try {
            String query = "DELETE FROM preferences WHERE userID = ? AND accommodationID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, AccommodationID);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void save(int Userid, int accommodationID) {
        try {
            String query = "INSERT INTO favourites VALUES(?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Userid);
            preparedStatement.setInt(2, accommodationID);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // FIXME gestire i casi che siano nulli
    public ArrayList<Accommodation> getFavouritesByUser(int id) {
        ArrayList<Accommodation> favourites = new ArrayList<>();
        try {
            String query = "SELECT * FROM Accommodation JOIN Favourites on Accommodation.id = Favourites.id WHERE user_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Accommodation accommodation = new Accommodation();
                // Set degli attributi base
                accommodation.setId(resultSet.getInt("id"));
                accommodation.setName(resultSet.getString("name"));
                accommodation.setDescription(resultSet.getString("description"));
                accommodation.setPlace(resultSet.getString("place"));
                accommodation.setAddress(resultSet.getString("address"));
                accommodation.setDisponibility(resultSet.getInt("disponibility"));
                accommodation.setRatePrice(resultSet.getFloat("ratePrice")); // Usa getFloat per decimali

                // Date
                accommodation.setAvailableFrom(resultSet.getDate("availableFrom"));
                accommodation.setAvailableEnd(resultSet.getDate("availableEnd"));

                // Enum (gestisci eventuali eccezioni se il valore è null o non valido)
                accommodation.setType(AccommodationType.valueOf(
                        resultSet.getString("type")
                ));

                // Gestione rating con nuovo enum
                int ratingValue = resultSet.getInt("rating");
                AccommodationRating rating = AccommodationRating.OneStar; // Default
                for (AccommodationRating ar : AccommodationRating.values()) {
                    if (ar.getNumericValue() == ratingValue) {
                        rating = ar;
                        break;
                    }
                }
                accommodation.setRating(rating);


                // Boolean (usa getBoolean o verifica valori come 1/0 se necessario)
                accommodation.setRefundable(resultSet.getBoolean("refundable"));
                accommodation.setFreewifi(resultSet.getBoolean("freewifi"));
                accommodation.setHaveSmokingArea(resultSet.getBoolean("haveSmokingArea"));
                accommodation.setHaveParking(resultSet.getBoolean("haveParking"));
                accommodation.setCoffeMachine(resultSet.getBoolean("coffeMachine"));
                accommodation.setRoomService(resultSet.getBoolean("roomService"));
                accommodation.setCleaningService(resultSet.getBoolean("cleaningService"));
                accommodation.setHaveSpa(resultSet.getBoolean("haveSpa"));
                accommodation.setGoodForKids(resultSet.getBoolean("goodForKids"));
                accommodation.setWelcomeAnimal(resultSet.getBoolean("welcomeAnimal"));

                accommodation.setNumberOfRoom(resultSet.getInt("numberOfRoom"));

                favourites.add(accommodation);
            }
            return favourites;
        } catch (RuntimeException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
