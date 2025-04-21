package DAO;

import DomainModel.Accommodation;
import DomainModel.Booking;
import DomainModel.RegisterUser;
import DomainModel.State;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class BookingDAO {
    private Connection connection;
    public BookingDAO() {
        try {
            this.connection=DatabaseConnection.getInstance().getConnection();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void removeBooking(int bookingID, State stateBooking) {
        if(stateBooking==State.Booking_Confirmed|| stateBooking==State.Checking_In){
            throw new RuntimeException("You can't delete booking from confirmed booking or in state of checking, you have to remove first");
        }else{
            PreparedStatement preparedStatement=null;
            try {
                String query="DELETE FROM booking WHERE id=?";
                preparedStatement=connection.prepareStatement(query);
                preparedStatement.setInt(1, bookingID);
                preparedStatement.executeUpdate();
                System.out.println("Booking removed successfully");
            } catch (SQLException e) {
                DBUtils.printSQLException(e);
            }finally {
                DBUtils.closeQuietly(preparedStatement);
            }
        }
    }

    public Booking addBooking(RegisterUser user, Accommodation accommodation, LocalDateTime datein, LocalDateTime dateout, int nPeople, int price) {
            if(accommodation.getDisponibility()==0){
                throw new RuntimeException("This accommodation is not disponible");
            }
            PreparedStatement preparedStatement=null;
        try {
            String query="insert into bookings values(?,?,?,?,?) RETURNING id";
            preparedStatement=connection.prepareStatement(query);
            preparedStatement.setInt(1, user.getId());
            preparedStatement.setInt(2, accommodation.getId());
            preparedStatement.setTimestamp(7, java.sql.Timestamp.valueOf(datein));
            preparedStatement.setTimestamp(8, java.sql.Timestamp.valueOf(dateout));
            preparedStatement.setInt(5, nPeople);
            preparedStatement.setInt(6, price);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                return new Booking( rs.getInt(1), user, accommodation, price, nPeople, datein, dateout, State.Booking_Confirmed);
            }

        } catch (SQLException e) {
            DBUtils.printSQLException(e);
        }finally{
            DBUtils.closeQuietly(preparedStatement);
        }
        return null;
    }

    public void getBookingsFromUser(RegisterUser user) {
        PreparedStatement preparedStatement=null;
        try {
            String query = "SELECT * FROM booking WHERE userId=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Booking booking = new Booking();
                booking.setBookingID(resultSet.getInt("id"));
                booking.setCustomer(user);

                // Date
                // Get timestamps from ResultSet
                java.sql.Timestamp sqlAvailableFrom = resultSet.getTimestamp("checkin");
                java.sql.Timestamp sqlAvailableEnd = resultSet.getTimestamp("checkout");

                // Convert to LocalDateTime (handling null values)
                if (sqlAvailableFrom != null) {
                    booking.setCheckInDate(sqlAvailableFrom.toLocalDateTime());
                }

                if (sqlAvailableEnd != null) {
                    booking.setCheckOutDate(sqlAvailableEnd.toLocalDateTime());
                }

                int accID = resultSet.getInt("accommodationId");
                Accommodation accommodation = null;
                if(!resultSet.wasNull()) { //on delete set NULL
                    AccommodationDAO accommodationDAO = new AccommodationDAO();
                    accommodation = accommodationDAO.getAccommodationByID(accID);
                }
                booking.setAccommodation(accommodation);
                booking.setNumPeople(resultSet.getInt("numpeople"));
                booking.setPrice(resultSet.getFloat("price"));

                // Determinare lo stato in base alla data corrente
                State state = determineBookingState(
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        State.valueOf(resultSet.getString("state"))
                );
                booking.setState(state);

                // Aggiornare lo stato nel DB se è cambiato
                if (!state.toString().equals(resultSet.getString("state"))) {
                    updateBookingState(booking.getBookingID(), state);
                }
            }
        } catch (SQLException e) {
            DBUtils.printSQLException(e);
        }finally{
            DBUtils.closeQuietly(preparedStatement);
        }
    }

    private void updateBookingState(int bookingId, State newState) {
        PreparedStatement preparedStatement=null;
        try {
            String query = "UPDATE booking SET state = ? WHERE id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newState.toString());
            preparedStatement.setInt(2, bookingId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            DBUtils.printSQLException(e);
        }
    }

    private State determineBookingState(LocalDateTime checkIn, LocalDateTime checkOut, State currentState) {
        LocalDateTime now = LocalDateTime.now();

        // Se la prenotazione è cancellata o rimborsata o non esiste più l'alloggio, non cambia stato
        if (currentState == State.Cancelled || currentState == State.Booking_Refunded || currentState == State.Accommodation_Cancelled) {
            return currentState;
        }


        // Verifica se siamo nell'ora esatta del check-in
        if (isSameDayAndTime(now, checkIn)) {
            return State.Checking_In;
        }

        // Verifica se siamo nell'ora esatta del check-out
        if (isSameDayAndTime(now, checkOut)) {
            return State.Checking_Out;
        }

        // Verifica se il soggiorno è in corso (tra check-in e check-out)
        if (now.isAfter(checkIn) && now.isBefore(checkOut)) {
            return State.Checking_In; // Oppure "In_Progress" se definito
        }

        // Se la data di check-in è passata ma lo stato non è stato aggiornato
        if (now.isAfter(checkIn) && currentState == State.Booking_Confirmed) {
            return State.Checking_In;
        }

        // Se la data di check-out è passata e lo stato era Checking_In o Checking_Out
        if (now.isAfter(checkOut) &&
                (currentState == State.Checking_In || currentState == State.Checking_Out)) {
            return State.Checking_Out;
        }

        return currentState;
    }

    private boolean isSameDayAndTime(LocalDateTime date1, LocalDateTime date2) {
        if (date1 == null || date2 == null) {
            return false;
        }



        return date1.getYear() == date2.getYear() &&
                date1.getMonth() == date2.getMonth() &&
                date1.getDayOfMonth() == date2.getDayOfMonth() &&
                date1.getHour() == date2.getHour();
    }

    public void cancelBook(Booking booking) {
        if(booking.getAccommodation().isRefundable()){
            booking.setState(State.Booking_Refunded);
            updateBookingState(booking.getBookingID(), booking.getState());
        }else{
            booking.setState(State.Cancelled);
            updateBookingState(booking.getBookingID(), booking.getState());
        }
    }

    public void updateBookingsAfterDeleteAccommodation(int idAccommodation) {
        PreparedStatement preparedStatement=null;
        try {
            String query = "SELECT id, userId, price FROM bookings WHERE accommodationId = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, idAccommodation);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                updateBookingState(resultSet.getInt("id"), State.Accommodation_Cancelled);
            }
        } catch (SQLException e) {
            DBUtils.printSQLException(e);
        }finally{
            DBUtils.closeQuietly(preparedStatement);
        }
    }

   /* public void updateBookingsAfterDeleteAccommodation(int idAccommodation) {
        UserDAO userDAO = new UserDAO();
        try {
            String query = "SELECT Id, userID, price FROM bookings WHERE accommodationID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, idAccommodation);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                updateBookingState(resultSet.getInt("id"), State.Accommodation_Cancelled);
                userDAO.updateFidPoints(userDAO.getUserById(resultSet.getInt("userID")), -resultSet.getInt("price"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }*/
}
