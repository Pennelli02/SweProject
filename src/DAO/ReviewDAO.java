package DAO;

import DomainModel.RegisterUser;
import DomainModel.Review;

import java.sql.Connection;
import java.util.ArrayList;

public class ReviewDAO {
    private Connection connection;

    public ReviewDAO() {
        this.connection=DatabaseConnection.getInstance().getConnection();
    }
    public ArrayList<Review> getReviewByUser(RegisterUser user) {
        //si può fare sempre con solo l'email
        return null;// ovviamente è solo per ora
    }

    public void removeReview(int reviewID) {
    }
}
