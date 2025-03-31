package DAO;

import java.sql.Connection;

public class BookingDAO {
    private Connection connection;
    public BookingDAO() {
        this.connection=DatabaseConnection.getInstance().getConnection();
    }
    public void removeBooking(int bookingID) {
    }
}
