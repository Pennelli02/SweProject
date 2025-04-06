package DAO;

import DomainModel.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AccommodationDAO {
    private Connection connection;
    public AccommodationDAO() {
        this.connection=DatabaseConnection.getInstance().getConnection();
    }

    // manca la gestione dei casi in cui sono nulli
    public Accommodation getAccommodationByID(int accommodationID) {
        try {
            String query = "SELECT * FROM accommodation WHERE accommodationID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, accommodationID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                Accommodation accommodation = new Accommodation();
                // Set degli attributi base
                accommodation.setId(resultSet.getInt("id")); // O accommodationID se diverso
                accommodation.setName(resultSet.getString("name"));
                accommodation.setDescription(resultSet.getString("description"));
                accommodation.setPlace(resultSet.getString("place"));
                accommodation.setAddress(resultSet.getString("address"));
                accommodation.setDisponibility(resultSet.getInt("disponibility"));
                accommodation.setRatePrice(resultSet.getFloat("ratePrice")); // Usa getFloat per decimali

                // Date
                accommodation.setAvailableFrom(resultSet.getDate("availableFrom"));
                accommodation.setAvailableEnd(resultSet.getDate("availableEnd"));

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

                accommodation.setRating(AccommodationRating.valueOf(
                        resultSet.getString("rating")
                ));

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
                return accommodation;
            }
        } catch (RuntimeException | SQLException e) {
            throw new RuntimeException(e);

        }
        return null;
    }

    public ArrayList<Accommodation> getAccommodationByParameter(SearchParameters searchParameters) {
        //utilizzare un query generator cioè a ogni valore non nullo nei parametri aggiungo una stringa alla query
        return null;
    }

    public ArrayList<Accommodation> getAllAccomodation() throws SQLException, ClassNotFoundException {
        ArrayList<Accommodation> accommodations = new ArrayList<>();
        try {
            String query = "SELECT * FROM accommodation";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
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

                accommodations.add(accommodation);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return accommodations;
    }

    // o si fa cascade o si fa per funzioni... farei per cascade
    public void deleteAccommodation(int idAccommodation){
        try {
            String query = "DELETE FROM accommodation WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, idAccommodation);
            preparedStatement.executeUpdate();
            System.out.println("Accommodation deleted successfully");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // si può fare con un trigger nel db
    public void updateRating(int accommodationID) {}
}
