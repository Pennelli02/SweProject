package DAO;

import DomainModel.Accommodation;
import DomainModel.Booking;
import DomainModel.RegisterUser;
import DomainModel.State;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

public class BookingDAO {
    private Connection connection;
    public BookingDAO() {
        this.connection=DatabaseConnection.getInstance().getConnection();
    }
    public void removeBooking(int bookingID) {
    }
    // solo un esempio
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

        // Verifica se oggi è il giorno del check-in
        if (isSameDay(now, checkIn)) {
            return State.Checking_In;
        }

        // Verifica se oggi è il giorno del check-out
        if (isSameDay(now, checkOut)) {
            return State.Checking_Out;
        }

        // Verifica se il soggiorno è in corso
        if (now.after(checkIn) && now.before(checkOut)) {
            return State.Checking_In; // Oppure potresti avere uno stato "In_Progress"
        }

        // Se la data di check-in è passata ma lo stato non è stato aggiornato
        if (now.after(checkIn) && currentState == State.Booking_Confirmed) {
            return State.Checking_In;
        }

        return currentState;
    }

    private boolean isSameDay(Date date1, Date date2) {
        // Confronta solo giorno, mese e anno ignorando ore/minuti/secondi
        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        java.util.Calendar cal2 = java.util.Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
                cal1.get(java.util.Calendar.MONTH) == cal2.get(java.util.Calendar.MONTH) &&
                cal1.get(java.util.Calendar.DAY_OF_MONTH) == cal2.get(java.util.Calendar.DAY_OF_MONTH);
    }


}
