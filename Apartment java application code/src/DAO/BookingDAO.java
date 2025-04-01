package DAO;

import DomainModel.Accommodation;
import DomainModel.Booking;
import DomainModel.RegisterUser;

import java.sql.Connection;
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
}
