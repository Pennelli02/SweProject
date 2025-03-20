package DomainModel;

public class ReviewMapper {
    public ReviewMapper() {}

    public Review map(RegisterUser registerUser, Accommodation accommodation, String reviewText, RatingStars ratingStars) {
        Review review = new Review(); // da valutare per l'id...
        review.setReviewText(reviewText);
        review.setAuthor(registerUser);
        review.setVote(ratingStars);
        return review;
    }
}
