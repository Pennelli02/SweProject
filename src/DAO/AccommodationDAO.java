package DAO;

import DomainModel.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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
                // Get timestamps from ResultSet
                java.sql.Timestamp sqlAvailableFrom = resultSet.getTimestamp("availableFrom");
                java.sql.Timestamp sqlAvailableEnd = resultSet.getTimestamp("availableEnd");

                // Convert to LocalDateTime (handling null values)
                if (sqlAvailableFrom != null) {
                    accommodation.setAvailableFrom(sqlAvailableFrom.toLocalDateTime());
                }

                if (sqlAvailableEnd != null) {
                    accommodation.setAvailableEnd(sqlAvailableEnd.toLocalDateTime());
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
                accommodation.setMaxNumberOfPeople(resultSet.getInt("maxPeople"));
                return accommodation;
            }
        } catch (RuntimeException | SQLException e) {
            throw new RuntimeException(e);

        }
        return null;
    }
    //TODO gestire la logica di ricerca per parametri
    public ArrayList<Accommodation> getAccommodationByParameter(SearchParameters searchParameters) {
        ArrayList<Accommodation> accommodations = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM accommodation WHERE disponibility > 0");

            // Lista parametri per PreparedStatement
            List<Object> parameters = new ArrayList<>();

            // Aggiunta condizioni in base ai parametri non nulli
            if (searchParameters.getPlace() != null && !searchParameters.getPlace().isEmpty()) {
                queryBuilder.append(" AND place LIKE ?");
                parameters.add(searchParameters.getPlace());
            }

            if (searchParameters.getDateOfCheckIn() != null && searchParameters.getDateOfCheckOut() != null) {
                queryBuilder.append(
                        " AND AvailableFrom <= ? " +  // Disponibile prima del check-out
                        " AND AvailableEnd >= ? "     // Disponibile dopo il check-in
                );
                parameters.add(java.sql.Timestamp.valueOf(searchParameters.getDateOfCheckOut()));
                parameters.add(java.sql.Timestamp.valueOf(searchParameters.getDateOfCheckIn()));
            }

            if (searchParameters.getHowMuchRooms() > 0) {
                queryBuilder.append(" AND numberOfRoom >= ?");
                parameters.add(searchParameters.getHowMuchRooms());
            }

            if (searchParameters.getHowMuchPeople() > 0) {
                queryBuilder.append(" AND  maxPeople <= ?");
                parameters.add(searchParameters.getHowMuchPeople());
            }

            if (!searchParameters.isAllCategories() && searchParameters.getCategory() != null) {
                queryBuilder.append(" AND type = ?");
                parameters.add(searchParameters.getCategory().name());
            }

            if (searchParameters.getMaxPrice() > 0) {
                queryBuilder.append(" AND ratePrice <= ?");
                parameters.add(searchParameters.getMaxPrice());
            }

            if (searchParameters.getMinAccommodationRating() != null) {
                queryBuilder.append(" AND rating >= ?");
                parameters.add(searchParameters.getMinAccommodationRating().getNumericValue());
            } else if (searchParameters.getSpecificAccommodationRating() != null) {
                queryBuilder.append(" AND a.rating = ?");
                parameters.add(searchParameters.getSpecificAccommodationRating().getNumericValue());
            }

            // Aggiunta condizioni per i servizi (solo se true)
            if (searchParameters.isRefundable()) {
                queryBuilder.append(" AND a.refundable = TRUE");
            }

            if (searchParameters.isHaveFreeWifi()) {
                queryBuilder.append(" AND a.freewifi = TRUE");
            }

            if (searchParameters.isCanISmoke()) {
                queryBuilder.append(" AND a.haveSmokingArea = TRUE");
            }

            if (searchParameters.isHaveParking()) {
                queryBuilder.append(" AND a.haveParking = TRUE");
            }

            if (searchParameters.isHaveCoffeeMachine()) {
                queryBuilder.append(" AND a.coffeMachine = TRUE");
            }

            if (searchParameters.isHaveRoomService()) {
                queryBuilder.append(" AND a.roomService = TRUE");
            }

            if (searchParameters.isHaveCleaningService()) {
                queryBuilder.append(" AND a.cleaningService = TRUE");
            }

            if (searchParameters.isHaveSpa()) {
                queryBuilder.append(" AND a.haveSpa = TRUE");
            }

            if (searchParameters.isGoodForKids()) {
                queryBuilder.append(" AND a.goodForKids = TRUE");
            }

            if (searchParameters.isCanHaveAnimal()) {
                queryBuilder.append(" AND a.welcomeAnimal = TRUE");
            }

            // Aggiunto ordinamento per rating (decrescente) e prezzo (crescente)
            queryBuilder.append(" ORDER BY a.rating DESC, a.ratePrice ASC");

            // Esecuzione query

        try {
          PreparedStatement ps = connection.prepareStatement(queryBuilder.toString());

          // Imposta tutti i parametri
            for (int i = 0; i < parameters.size(); i++) {
                Object param = parameters.get(i);
                if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                } else if (param instanceof Float) {
                    ps.setFloat(i + 1, (Float) param);
                } else if (param instanceof java.sql.Timestamp) {
                    ps.setTimestamp(i + 1, (java.sql.Timestamp) param);
                }
            }

          ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
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
                // Get timestamps from ResultSet
                java.sql.Timestamp sqlAvailableFrom = resultSet.getTimestamp("availableFrom");
                java.sql.Timestamp sqlAvailableEnd = resultSet.getTimestamp("availableEnd");

                // Convert to LocalDateTime (handling null values)
                if (sqlAvailableFrom != null) {
                    accommodation.setAvailableFrom(sqlAvailableFrom.toLocalDateTime());
                }

                if (sqlAvailableEnd != null) {
                    accommodation.setAvailableEnd(sqlAvailableEnd.toLocalDateTime());
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
                accommodation.setMaxNumberOfPeople(resultSet.getInt("maxPeople"));
                accommodations.add(accommodation);
            }
            return accommodations;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
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
                // Get timestamps from ResultSet
                java.sql.Timestamp sqlAvailableFrom = resultSet.getTimestamp("availableFrom");
                java.sql.Timestamp sqlAvailableEnd = resultSet.getTimestamp("availableEnd");

                // Convert to LocalDateTime (handling null values)
                if (sqlAvailableFrom != null) {
                    accommodation.setAvailableFrom(sqlAvailableFrom.toLocalDateTime());
                }

                if (sqlAvailableEnd != null) {
                    accommodation.setAvailableEnd(sqlAvailableEnd.toLocalDateTime());
                }

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
                accommodation.setMaxNumberOfPeople(resultSet.getInt("maxPeople"));

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
    public void addAccommodation(String name, String address, String place, int disponibility, AccommodationType type, float ratePrice, LocalDateTime availableFrom, LocalDateTime availableEnd, String description, AccommodationRating rating, boolean refundable, boolean freewifi, boolean haveSmokingArea, boolean haveParking, boolean coffeMachine, boolean roomService, boolean cleaningService, boolean haveSpa, boolean goodForKids, int numberOfRoom, boolean welcomeAnimal, int maxNumberOfPeople) {
        if(disponibility<=0){
            throw new RuntimeException("disponibility should be greater than 0");
        }
        try {
            String query="INSERT INTO accommodation (name, address, place, disponibility, type, rate_price, available_from, available_end, description, rating, refundable, free_wifi, smoking_area, parking, coffee_machine, room_service, cleaning_service, spa, good_for_kids, number_of_rooms, welcome_animals, maxPeople) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)  ";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, place);
            preparedStatement.setInt(4, disponibility);
            preparedStatement.setString(5, type.toString());
            preparedStatement.setFloat(6, ratePrice);
            preparedStatement.setTimestamp(7, java.sql.Timestamp.valueOf(availableFrom));
            preparedStatement.setTimestamp(8, java.sql.Timestamp.valueOf(availableEnd));
            preparedStatement.setString(9, description);
            preparedStatement.setInt(10, rating.getNumericValue());
            preparedStatement.setBoolean(11, refundable);
            preparedStatement.setBoolean(12, freewifi);
            preparedStatement.setBoolean(13, haveSmokingArea);
            preparedStatement.setBoolean(14, haveParking);
            preparedStatement.setBoolean(15, coffeMachine);
            preparedStatement.setBoolean(16, roomService);
            preparedStatement.setBoolean(17, cleaningService);
            preparedStatement.setBoolean(18, haveSpa);
            preparedStatement.setBoolean(19, goodForKids);
            preparedStatement.setInt(20, numberOfRoom);
            preparedStatement.setBoolean(21, welcomeAnimal);
            preparedStatement.setInt(22, maxNumberOfPeople);
            preparedStatement.execute();
            System.out.println("Accommodation added successfully");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateAccommodationDisponibility(int accommodationID, int statusDisponibility) {
        try {
            String query = "UPDATE accommodation SET disponibility = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, statusDisponibility);
            preparedStatement.setInt(2, accommodationID);
            preparedStatement.executeUpdate();
            System.out.println("Accommodation updated successfully");
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
// gestione update con dirty flag
    public void updateAccommodationDirty(Accommodation accommodation) {
        try {

            // Aggiorna solo i campi modificati
            if (accommodation.isFieldModified("name")) {
                updateField(accommodation.getId(), "name", accommodation.getName());
            }
            if (accommodation.isFieldModified("address")) {
                updateField(accommodation.getId(), "address", accommodation.getAddress());
            }
            if (accommodation.isFieldModified("place")) {
                updateField(accommodation.getId(), "place", accommodation.getPlace());
            }
            if (accommodation.isFieldModified("type")) {
                updateField(accommodation.getId(), "type", accommodation.getType().toString());
            }
            if (accommodation.isFieldModified("ratePrice")) {
                updateField(accommodation.getId(), "ratePrice", accommodation.getRatePrice());
            }
            if (accommodation.isFieldModified("availableFrom")) {
                updateField(accommodation.getId(), "availableFrom", accommodation.getAvailableFrom());
            }
            if (accommodation.isFieldModified("availableEnd")) {
                updateField(accommodation.getId(), "availableEnd", accommodation.getAvailableEnd());
            }
            if (accommodation.isFieldModified("description")) {
                updateField(accommodation.getId(), "description", accommodation.getDescription());
            }
            if (accommodation.isFieldModified("freewifi")) {
                updateField(accommodation.getId(), "freewifi", accommodation.isFreewifi());
            }
            if (accommodation.isFieldModified("haveSmokingArea")) {
                updateField(accommodation.getId(), "haveSmokingArea", accommodation.isHaveSmokingArea());
            }
            if (accommodation.isFieldModified("haveParking")) {
                updateField(accommodation.getId(), "haveParking", accommodation.isHaveParking());
            }
            if (accommodation.isFieldModified("coffeMachine")) {
                updateField(accommodation.getId(), "coffeMachine", accommodation.isCoffeMachine());
            }
            if (accommodation.isFieldModified("roomService")) {
                updateField(accommodation.getId(), "roomService", accommodation.isRoomService());
            }
            if (accommodation.isFieldModified("cleaningService")) {
                updateField(accommodation.getId(), "cleaningService", accommodation.isCleaningService());
            }
            if (accommodation.isFieldModified("haveSpa")) {
                updateField(accommodation.getId(), "haveSpa", accommodation.isHaveSpa());
            }
            if (accommodation.isFieldModified("goodForKids")) {
                updateField(accommodation.getId(), "goodForKids", accommodation.isGoodForKids());
            }
            if (accommodation.isFieldModified("numberOfRoom")) {
                updateField(accommodation.getId(), "numberOfRoom", accommodation.getNumberOfRoom());
            }
            if (accommodation.isFieldModified("welcomeAnimal")) {
                updateField(accommodation.getId(), "welcomeAnimal", accommodation.isWelcomeAnimal());
            }
            if (accommodation.isFieldModified("maxNumberOfPeople")) {
                updateField(accommodation.getId(), "maxNumberOfPeople", accommodation.getMaxNumberOfPeople());
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

        // Esegue un singolo update (riutilizzabile) dirty flag
            private void updateField(int id, String field, Object value) throws SQLException {
                String sql = "UPDATE accommodations SET " + field + " = ? WHERE id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    // Gestione speciale per LocalDateTime
                    if (value instanceof LocalDateTime) {
                        stmt.setTimestamp(1, Timestamp.valueOf((LocalDateTime) value));
                    } else {
                        stmt.setObject(1, value);
                    }
                    stmt.setInt(2, id);
                    stmt.executeUpdate();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

    public void updateName(int idAcc, String newName) {
        try {
            String query = "UPDATE accommodations SET name = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, newName);
            stmt.setInt(2, idAcc);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateAddress(int idAcc, String newAddress) {
        try {
            String query = "UPDATE accommodations SET address = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, newAddress);
            stmt.setInt(2, idAcc);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePlace(int idAcc, String place) {
        try {
            String query = "UPDATE accommodations SET place = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, place);
            stmt.setInt(2, idAcc);
            stmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateType(int idAcc, AccommodationType type) {
        try {
            String query = "UPDATE accommodations SET type = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, type.toString());
            stmt.setInt(2, idAcc);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateRatePrice(int idAcc, float ratePrice) {
        try {
            String query = "UPDATE accommodations SET ratePrice = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setFloat(1, ratePrice);
            stmt.setInt(2, idAcc);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateAvailableFrom(int idAcc, LocalDateTime availableFrom) {
    }

    public void updateAvailableEnd(int idAcc, LocalDateTime availableEnd) {
    }

    public void updateDescription(int idAcc, String description) {
    }

    public void updateFreewifi(int idAcc, boolean freewifi) {
    }

    public void updateHaveSmokingArea(int idAcc, boolean haveSmokingArea) {
    }

    public void updateHaveParking(int idAcc, boolean haveParking) {
    }

    public void updateCoffeMachine(int idAcc, boolean coffeMachine) {
    }

    public void updateRoomService(int idAcc, boolean roomService) {
    }

    public void updateCleaningService(int idAcc, boolean cleaningService) {
    }

    public void updateHaveSpa(int idAcc, boolean haveSpa) {
    }

    public void updateGoodForKids(int idAcc, boolean goodForKids) {
    }

    public void updateNumberOfRoom(int idAcc, int numberOfRoom) {
    }

    public void updateWelcomeAnimal(int idAcc, boolean welcomeAnimal) {
    }

    public void updateMaxNumberOfPeople(int idAcc, int maxNumberOfPeople) {
    }
}
