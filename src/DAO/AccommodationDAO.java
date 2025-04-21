package DAO;

import DomainModel.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class AccommodationDAO {
    private Connection connection;
    public AccommodationDAO() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    // manca la gestione dei casi in cui sono nulli
    public Accommodation getAccommodationByID(int accommodationID) {
        PreparedStatement preparedStatement = null;
        try {
            String query = "SELECT * FROM accommodation WHERE accommodationId = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, accommodationID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
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
                accommodation.setHaveSmokingArea(resultSet.getBoolean("havesmockingarea"));
                accommodation.setHaveParking(resultSet.getBoolean("haveparking"));
                accommodation.setCoffeMachine(resultSet.getBoolean("coffemachine"));
                accommodation.setRoomService(resultSet.getBoolean("roomservice"));
                accommodation.setCleaningService(resultSet.getBoolean("cleaningservice"));
                accommodation.setHaveSpa(resultSet.getBoolean("havespa"));
                accommodation.setGoodForKids(resultSet.getBoolean("goodforkids"));
                accommodation.setWelcomeAnimal(resultSet.getBoolean("welcomeanimal"));
                accommodation.setNumberOfRoom(resultSet.getInt("numberofroom"));
                accommodation.setMaxNumberOfPeople(resultSet.getInt("maxpeople"));
                return accommodation;
            }
        } catch (SQLException e) {
            DBUtils.printSQLException(e);
        }finally{
            DBUtils.closeQuietly(preparedStatement);
        }
        return null;
    }

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
        PreparedStatement ps = null;

        try {
          ps = connection.prepareStatement(queryBuilder.toString());

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
                accommodation.setCoffeMachine(resultSet.getBoolean("coffemachine"));
                accommodation.setRoomService(resultSet.getBoolean("roomservice"));
                accommodation.setCleaningService(resultSet.getBoolean("cleaningservice"));
                accommodation.setHaveSpa(resultSet.getBoolean("havespa"));
                accommodation.setGoodForKids(resultSet.getBoolean("goodforkids"));
                accommodation.setWelcomeAnimal(resultSet.getBoolean("welcomeanimal"));
                accommodation.setNumberOfRoom(resultSet.getInt("numberofroom"));
                accommodation.setMaxNumberOfPeople(resultSet.getInt("maxpeople"));
                accommodations.add(accommodation);
            }
            return accommodations;
        }catch (SQLException e) {
            DBUtils.printSQLException(e);
        }finally {
            DBUtils.closeQuietly(ps);
        }
        return null;
    }

    public ArrayList<Accommodation> getAllAccommodation() {
        ArrayList<Accommodation> accommodations = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        try {
            String query = "SELECT * FROM accommodation";
            preparedStatement = connection.prepareStatement(query);
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
                accommodation.setHaveSmokingArea(resultSet.getBoolean("havesmockingarea"));
                accommodation.setHaveParking(resultSet.getBoolean("haveparking"));
                accommodation.setCoffeMachine(resultSet.getBoolean("coffemachine"));
                accommodation.setRoomService(resultSet.getBoolean("roomservice"));
                accommodation.setCleaningService(resultSet.getBoolean("cleaningservice"));
                accommodation.setHaveSpa(resultSet.getBoolean("havespa"));
                accommodation.setGoodForKids(resultSet.getBoolean("goodforkids"));
                accommodation.setWelcomeAnimal(resultSet.getBoolean("welcomeanimal"));

                accommodation.setNumberOfRoom(resultSet.getInt("numberofroom"));
                accommodation.setMaxNumberOfPeople(resultSet.getInt("maxpeople"));

                accommodations.add(accommodation);
            }
        } catch (SQLException e) {
            DBUtils.printSQLException(e);
        }finally {
            DBUtils.closeQuietly(preparedStatement);
        }
        return accommodations;
    }

    //Fixme ci penso io  faccio che nelle preference sia cascade e nel booking sia on set null
    public void deleteAccommodation(int idAccommodation){
        PreparedStatement preparedStatement = null;
        try {
            String query = "DELETE FROM accommodation WHERE id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, idAccommodation);
            preparedStatement.executeUpdate();
            BookingDAO bookingDAO = new BookingDAO();
            bookingDAO.updateBookingsAfterDeleteAccommodation(idAccommodation);
            System.out.println("Accommodation deleted successfully");
        } catch (SQLException e) {
            DBUtils.printSQLException(e);
        }finally {
            DBUtils.closeQuietly(preparedStatement);
        }
    }


    // si può fare con un trigger nel db
    public void updateRating(int accommodationID) {}

    //FixMe da vedere in futuro se funziona
    public void addAccommodation(String name, String address, String place, int disponibility, AccommodationType type, float ratePrice, LocalDateTime availableFrom, LocalDateTime availableEnd, String description, AccommodationRating rating, boolean refundable, boolean freewifi, boolean haveSmokingArea, boolean haveParking, boolean coffeMachine, boolean roomService, boolean cleaningService, boolean haveSpa, boolean goodForKids, int numberOfRoom, boolean welcomeAnimal, int maxNumberOfPeople) {
        if(disponibility<=0){
            throw new RuntimeException("disponibility should be greater than 0");
        }
        PreparedStatement preparedStatement = null;
        try {
            String query="INSERT INTO accommodation (name, address, place, disponibility, type, rateprice, availablefrom, availableend, description, rating, refundable, freewifi, havesmokingarea, parking, coffeemachine, roomservice, cleaningservice, havespa, goodforkids, numberofrooms, welcomeanimals, maxPeople) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)  ";
            preparedStatement = connection.prepareStatement(query);
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
        } catch (SQLException e) {
            DBUtils.printSQLException(e);
        }finally {
            DBUtils.closeQuietly(preparedStatement);
        }
    }

    public void updateAccommodationDisponibility(int accommodationID, int statusDisponibility) {
        PreparedStatement preparedStatement=null;
        try {
            String query = "UPDATE accommodation SET disponibility = ? WHERE id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, statusDisponibility);
            preparedStatement.setInt(2, accommodationID);
            preparedStatement.executeUpdate();
            System.out.println("Accommodation updated successfully");
        }catch (SQLException e){
            DBUtils.printSQLException(e);
        }finally {
            DBUtils.closeQuietly(preparedStatement);
        }
    }

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
                updateField(accommodation.getId(), "rateprice", accommodation.getRatePrice());
            }
            if (accommodation.isFieldModified("availableFrom")) {
                updateField(accommodation.getId(), "availablefrom", accommodation.getAvailableFrom());
            }
            if (accommodation.isFieldModified("availableEnd")) {
                updateField(accommodation.getId(), "availableend", accommodation.getAvailableEnd());
            }
            if (accommodation.isFieldModified("description")) {
                updateField(accommodation.getId(), "description", accommodation.getDescription());
            }
            if (accommodation.isFieldModified("freewifi")) {
                updateField(accommodation.getId(), "freewifi", accommodation.isFreewifi());
            }
            if (accommodation.isFieldModified("haveSmokingArea")) {
                updateField(accommodation.getId(), "havesmockingarea", accommodation.isHaveSmokingArea());
            }
            if (accommodation.isFieldModified("haveParking")) {
                updateField(accommodation.getId(), "haveparking", accommodation.isHaveParking());
            }
            if (accommodation.isFieldModified("coffeMachine")) {
                updateField(accommodation.getId(), "coffemachine", accommodation.isCoffeMachine());
            }
            if (accommodation.isFieldModified("roomService")) {
                updateField(accommodation.getId(), "roomservice", accommodation.isRoomService());
            }
            if (accommodation.isFieldModified("cleaningService")) {
                updateField(accommodation.getId(), "cleaningService", accommodation.isCleaningService());
            }
            if (accommodation.isFieldModified("haveSpa")) {
                updateField(accommodation.getId(), "havespa", accommodation.isHaveSpa());
            }
            if (accommodation.isFieldModified("goodForKids")) {
                updateField(accommodation.getId(), "goodforkids", accommodation.isGoodForKids());
            }
            if (accommodation.isFieldModified("numberOfRoom")) {
                updateField(accommodation.getId(), "numberofroom", accommodation.getNumberOfRoom());
            }
            if (accommodation.isFieldModified("welcomeAnimal")) {
                updateField(accommodation.getId(), "welcomeanimal", accommodation.isWelcomeAnimal());
            }
            if (accommodation.isFieldModified("maxNumberOfPeople")) {
                updateField(accommodation.getId(), "maxpeople", accommodation.getMaxNumberOfPeople());
            }


        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

        // Esegue un singolo update (riutilizzabile) dirty flag
            private void updateField(int id, String field, Object value){
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
                } catch (SQLException e) {
                    DBUtils.printSQLException(e);
                }
            }


}
