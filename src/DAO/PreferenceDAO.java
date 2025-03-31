package DAO;

import DomainModel.Accommodation;
import DomainModel.RegisterUser;

import java.sql.Connection;

public class PreferenceDAO {
    private Connection connection;
    public PreferenceDAO() {
        this.connection=DatabaseConnection.getInstance().getConnection();
    }
    public void unSave(RegisterUser user, int AccommodationID) {
    }

    public void save(int id, int accommodationID) {
    }
}
