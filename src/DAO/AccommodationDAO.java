package DAO;

import DomainModel.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

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

    public ArrayList<Accommodation> getAllAccommodation() throws SQLException, ClassNotFoundException {
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

    //Fixme problemi con la logica di cancellazione forse si ritorna al trigger? vogliamo eliminare le prenotazioni inerenti o settarle con un nuovo valore?
    public void deleteAccommodation(int idAccommodation){
        try {
            String query = "DELETE FROM accommodation WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, idAccommodation);
            preparedStatement.executeUpdate();
//            BookingDAO bookingDAO = new BookingDAO();
//            bookingDAO.updateBookingsAfterDeleteAccommodation(idAccommodation);
            System.out.println("Accommodation deleted successfully");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // si può fare con un trigger nel db
    public void updateRating(int accommodationID) {}

    //FixMe da vedere in futuro se funziona
    public void addAccommodation(String name, String address, String place, int disponibility, AccommodationType type, float ratePrice, Date availableFrom, Date availableEnd, String description, AccommodationRating rating, boolean refundable, boolean freewifi, boolean haveSmokingArea, boolean haveParking, boolean coffeMachine, boolean roomService, boolean cleaningService, boolean haveSpa, boolean goodForKids, int numberOfRoom, boolean welcomeAnimal) {
        try {
            String query="INSERT INTO accommodation (name, address, place, disponibility, type, rate_price, available_from, available_end, description, rating, refundable, free_wifi, smoking_area, parking, coffee_machine, room_service, cleaning_service, spa, good_for_kids, number_of_rooms, welcome_animals) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)  ";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, place);
            preparedStatement.setInt(4, disponibility);
            preparedStatement.setString(5, type.toString());
            preparedStatement.setFloat(6, ratePrice);
            preparedStatement.setDate(7, new java.sql.Date(availableFrom.getTime()));
            preparedStatement.setDate(8, new java.sql.Date(availableEnd.getTime()));
            preparedStatement.setString(9, description);
            preparedStatement.setInt(10, rating.getNumericValue());
            preparedStatement.setBoolean(11, refundable);
            preparedStatement.setBoolean(12, freewifi);
            preparedStatement.setBoolean(13, haveSmokingArea);
            preparedStatement.setBoolean(14, haveParking);
            preparedStatement.setBoolean(15, coffeMachine);
            preparedStatement.setInt(16, numberOfRoom);
            preparedStatement.setBoolean(17, cleaningService);
            preparedStatement.setBoolean(18, haveSpa);
            preparedStatement.setBoolean(19, goodForKids);
            preparedStatement.setBoolean(20, roomService);
            preparedStatement.setBoolean(21, welcomeAnimal);
            System.out.println("Accommodation added successfully");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
