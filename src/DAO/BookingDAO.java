package DAO;

import DomainModel.Accommodation;
import DomainModel.Booking;
import DomainModel.RegisterUser;
import DomainModel.State;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class BookingDAO {
    private Connection connection;
    public BookingDAO() {
        this.connection=DatabaseConnection.getInstance().getConnection();
    }

    public void removeBooking(int bookingID, State stateBooking) {
        if(stateBooking==State.Booking_Confirmed|| stateBooking==State.Checking_In){
            throw new RuntimeException("You can't remove booking from confirmed booking or in state of checking, you have to remove first");
        }else{
            try {
                String query="DELETE FROM booking WHERE bookingID=?";
                PreparedStatement preparedStatement=connection.prepareStatement(query);
                preparedStatement.setInt(1, bookingID);
                preparedStatement.executeUpdate();
                System.out.println("Booking removed successfully");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Booking addBooking(RegisterUser user, Accommodation accommodation, Date datein, Date dateout, int nPeople, int price) {
        java.sql.Date dateIn = new java.sql.Date(datein.getTime());
        java.sql.Date dateOut = new java.sql.Date(dateout.getTime());
        try {
            String query="insert into bookings values(?,?,?,?,?) RETURNING id";
            PreparedStatement preparedStatement=connection.prepareStatement(query);
            preparedStatement.setInt(1, user.getId());
            preparedStatement.setInt(2, accommodation.getId());
            preparedStatement.setDate(3, dateIn);
            preparedStatement.setDate(4, dateOut);
            preparedStatement.setInt(5, nPeople);
            preparedStatement.setInt(6, price);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                return new Booking( rs.getInt(1), user, accommodation, price, nPeople, dateIn, dateout, State.Booking_Confirmed);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void getBookingsFromUser(RegisterUser user) {
        try {
            String query = "SELECT * FROM bookings WHERE userID=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Booking booking = new Booking();
                booking.setBookingID(resultSet.getInt("id"));
                booking.setCustomer(user);

                // Convertire le date
                java.sql.Date sqlCheckIn = resultSet.getDate("checkIn");
                java.sql.Date sqlCheckOut = resultSet.getDate("checkOut");
                Date checkInDate = new Date(sqlCheckIn.getTime());
                Date checkOutDate = new Date(sqlCheckOut.getTime());

                booking.setCheckInDate(checkInDate);
                booking.setCheckOutDate(checkOutDate);

                int accID = resultSet.getInt("accommodationID");
                AccommodationDAO accommodationDAO = new AccommodationDAO();
                Accommodation accommodation = accommodationDAO.getAccommodationByID(accID);
                booking.setAccommodation(accommodation);
                booking.setNumPeople(resultSet.getInt("numPeople"));
                booking.setPrice(resultSet.getFloat("price"));

                // Determinare lo stato in base alla data corrente
                State state = determineBookingState(
                        checkInDate,
                        checkOutDate,
                        State.valueOf(resultSet.getString("state"))
                );
                booking.setState(state);

                // Aggiornare lo stato nel DB se è cambiato
                if (!state.toString().equals(resultSet.getString("state"))) {
                    updateBookingState(booking.getBookingID(), state);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateBookingState(int bookingId, State newState) {
        try {
            String query = "UPDATE bookings SET state = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newState.toString());
            preparedStatement.setInt(2, bookingId);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update booking state", e);
        }
    }

    private State determineBookingState(Date checkIn, Date checkOut, State currentState) {
        Date now = new Date();

        // Se la prenotazione è cancellata o rimborsata, non cambia stato
        if (currentState == State.Cancelled || currentState == State.Booking_Refunded) {
            return currentState;
        }

        // Converti le date in LocalDateTime per un confronto più preciso
        LocalDateTime nowLdt = now.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime checkInLdt = checkIn.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime checkOutLdt = checkOut.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        // Verifica se siamo nell'ora esatta del check-in
        if (isSameDayAndTime(now, checkIn)) {
            return State.Checking_In;
        }

        // Verifica se siamo nell'ora esatta del check-out
        if (isSameDayAndTime(now, checkOut)) {
            return State.Checking_Out;
        }

        // Verifica se il soggiorno è in corso (tra check-in e check-out)
        if (nowLdt.isAfter(checkInLdt) && nowLdt.isBefore(checkOutLdt)) {
            return State.Checking_In; // Oppure "In_Progress" se definito
        }

        // Se la data di check-in è passata ma lo stato non è stato aggiornato
        if (nowLdt.isAfter(checkInLdt) && currentState == State.Booking_Confirmed) {
            return State.Checking_In;
        }

        // Se la data di check-out è passata e lo stato era Checking_In o Checking_Out
        if (nowLdt.isAfter(checkOutLdt) &&
                (currentState == State.Checking_In || currentState == State.Checking_Out)) {
            return State.Checking_Out;
        }

        return currentState;
    }

    private boolean isSameDayAndTime(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }

        LocalDateTime ldt1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime ldt2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        return ldt1.getYear() == ldt2.getYear() &&
                ldt1.getMonth() == ldt2.getMonth() &&
                ldt1.getDayOfMonth() == ldt2.getDayOfMonth() &&
                ldt1.getHour() == ldt2.getHour();
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
}
