package DAO;

import DomainModel.Accommodation;
import DomainModel.RegisterUser;
import DomainModel.SearchParameters;

import java.sql.SQLException;
import java.util.ArrayList;

public class AccommodationDAO {
    public Accommodation getAccommodationByID(int accommodationID) {
        return null;
    }

    public ArrayList<Accommodation> getAccommodationByParameter(SearchParameters searchParameters) {
        return null;
    }

    public ArrayList<Accommodation> getAllAccomodation() throws SQLException, ClassNotFoundException {
        ArrayList<Accommodation> accommodations = new ArrayList<>();
        return accommodations;
    }

    public void deleteAccommodation(int idAccommodation){

    }
}
