package ORM;

import DomainModel.Accommodation;
import DomainModel.AccommodationRating;
import DomainModel.AccommodationType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PreferenceDAO {
    private Connection connection;

    public PreferenceDAO() {
        try{
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void unSave(int userID, int AccommodationID) {
        PreparedStatement preparedStatement = null;
        try {
            String query = "DELETE FROM favourites WHERE userId = ? AND accommodationId = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, AccommodationID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
           DBUtils.printSQLException(e);
        }finally {
            DBUtils.closeQuietly(preparedStatement);
        }
    }

    public void save(int Userid, int accommodationID) {
        PreparedStatement preparedStatement = null;
        try {
            String query = "INSERT INTO favourites VALUES(?,?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Userid);
            preparedStatement.setInt(2, accommodationID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            DBUtils.printSQLException(e);
        }finally {
            DBUtils.closeQuietly(preparedStatement);
        }
    }

    // FIXME gestire i casi che siano nulli
    public ArrayList<Accommodation> getFavouritesByUser(int id) {
        ArrayList<Accommodation> favourites = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        try {
            String query = "SELECT * FROM Accommodation JOIN Favourites on Accommodation.id = Favourites.accommodationId WHERE Favourites.userId=?";
            preparedStatement = connection.prepareStatement(query);
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
                accommodation.setRatePrice(resultSet.getFloat("rateprice")); // Usa getFloat per decimali

                // Date
                // Get timestamps from ResultSet
                java.sql.Timestamp sqlAvailableFrom = resultSet.getTimestamp("availablefrom");
                java.sql.Timestamp sqlAvailableEnd = resultSet.getTimestamp("availableend");

                // Convert to LocalDateTime (handling null values)
                if (sqlAvailableFrom != null) {
                    accommodation.setAvailableFrom(sqlAvailableFrom.toLocalDateTime());
                }

                if (sqlAvailableEnd != null) {
                    accommodation.setAvailableEnd(sqlAvailableEnd.toLocalDateTime());
                }

                // Gestione del tipo di alloggio (enum)
                String typeString = resultSet.getString("type");
                if (typeString != null) {
                    try {
                        AccommodationType type = AccommodationType.fromString(typeString);
                        accommodation.setType(type);
                    } catch (IllegalArgumentException ex) {
                        System.err.println("Tipo di alloggio non riconosciuto: " + typeString);
                    }
                }
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
                accommodation.setHaveSmokingArea(resultSet.getBoolean("havesmockingarea"));
                accommodation.setHaveParking(resultSet.getBoolean("haveparking"));
                accommodation.setCoffeMachine(resultSet.getBoolean("coffeemachine"));
                accommodation.setRoomService(resultSet.getBoolean("roomservice"));
                accommodation.setCleaningService(resultSet.getBoolean("cleaningservice"));
                accommodation.setHaveSpa(resultSet.getBoolean("havespa"));
                accommodation.setGoodForKids(resultSet.getBoolean("goodforkids"));
                accommodation.setWelcomeAnimal(resultSet.getBoolean("welcomeanimal"));

                accommodation.setNumberOfRoom(resultSet.getInt("numberofroom"));
                accommodation.setMaxNumberOfPeople(resultSet.getInt("maxpeople"));

                favourites.add(accommodation);
            }

        } catch (SQLException e) {
            DBUtils.printSQLException(e);
        }finally {
            DBUtils.closeQuietly(preparedStatement);
        }
        return favourites;
    }
}
