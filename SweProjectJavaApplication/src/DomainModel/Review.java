package DomainModel;

public class Review {
    private int ReviewID;
    private RegisterUser author;
    private Accommodation reviewedItem;
    private String reviewText;
    private AccommodationRating vote;

    public Review(int reviewID, RegisterUser author, Accommodation reviewedItem, String reviewText, AccommodationRating vote) {
        ReviewID = reviewID;
        this.author = author;
        this.reviewedItem = reviewedItem;
        this.reviewText = reviewText;
        this.vote = vote;
    }

    public Review() {}

    public int getReviewID() {
        return ReviewID;
    }

    public void setReviewID(int reviewID) {
        ReviewID = reviewID;
    }

    public RegisterUser getAuthor() {
        return author;
    }

    public void setAuthor(RegisterUser author) {
        this.author = author;
    }

    public Accommodation getReviewedItem() {
        return reviewedItem;
    }

    public void setReviewedItem(Accommodation reviewedItem) {
        this.reviewedItem = reviewedItem;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public AccommodationRating getVote() {
        return vote;
    }

    public void setVote(AccommodationRating vote) {
        this.vote = vote;
    }

    public void removeReview(int id){}

    public String toStringUser() {
        return "ReviewID= " + ReviewID +
                ", Accommodation= " + reviewedItem.getName() +
                ", reviewText= " + reviewText +
                ", vote= " + vote;
    }

    public String toStringAccommodation() {
        return "ReviewID= " + ReviewID +
                ", author= " + author.getUsername() +
                ", reviewText= " + reviewText +
                ", vote= " + vote;
    }

}
