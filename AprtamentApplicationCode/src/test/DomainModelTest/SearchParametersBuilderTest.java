package test.DomainModelTest;

import DomainModel.SearchParametersBuilder;
import DomainModel.AccommodationRating;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class SearchParametersBuilderTest {

    @Test
    void builderRejectsEmptyPlace() {
        assertThrows(IllegalArgumentException.class, () ->
                SearchParametersBuilder.newBuilder("")
        );
    }

    @Test
    void builderRejectsSingleDate() {
        var b = SearchParametersBuilder.newBuilder("Roma");
        b.setDateOfCheckIn(LocalDateTime.now().plusDays(1));
        assertThrows(IllegalArgumentException.class, b::build);
    }

    @Test
    void builderRejectsPastDates() {
        var b = SearchParametersBuilder.newBuilder("Milano");
        b.setDateOfCheckIn(LocalDateTime.now().minusDays(1))
                .setDateOfCheckOut(LocalDateTime.now().plusDays(1));
        assertThrows(IllegalArgumentException.class, b::build);
    }

    @Test
    void builderRejectsBothRatings() {
        var b = SearchParametersBuilder.newBuilder("Torino")
                .setMinRatingStars(AccommodationRating.ThreeStar)
                .setSpecificRatingStars(AccommodationRating.FourStar);
        assertThrows(IllegalArgumentException.class, b::build);
    }

    @Test
    void builderAcceptsValidParams() {
        var params = assertDoesNotThrow(()->SearchParametersBuilder.newBuilder("Venezia")
                .setDateOfCheckIn(LocalDateTime.now().plusDays(1))
                .setDateOfCheckOut(LocalDateTime.now().plusDays(3))
                .setHowMuchPeople(2)
                .setHowMuchRooms(1)
                .setAllCategories(true)
                .build());
        assertNotNull(params);
    }
}
