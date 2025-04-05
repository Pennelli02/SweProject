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
    public Booking addBooking(RegisterUser user, Accommodation accommodation, Date datein, Date dateout, int nPeople) {
        // codice per inserire un booking nel database
        // e ottenere i dati che ci servono tipo rating price etc... serve sia dei valori si searchParameters (check-in check-out numPersone)
        // recuperiamo l'id se serve(serve)
        int bookingID=1;// qui si inserisce id
    //  Booking booking = new Booking(bookingID, user, accommodation, etc...);
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
                booking.setCheckInDate(resultSet.getDate("checkIn"));
                booking.setCheckOutDate(resultSet.getDate("checkOut"));
                int accID = resultSet.getInt("accommodationID");
                AccommodationDAO accommodationDAO =new AccommodationDAO();
                Accommodation accommodation = accommodationDAO.getAccommodationByID(accID);
                booking.setAccommodation(accommodation);
                booking.setNumPeople(resultSet.getInt("numPeople"));
                booking.setPrice(resultSet.getFloat("price"));
                State state= State.valueOf(resultSet.getString("state"));
                booking.setState(state);
                user.addBooking(booking);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
