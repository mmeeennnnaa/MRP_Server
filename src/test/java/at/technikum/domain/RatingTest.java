package at.technikum.domain;

import at.technikum.domain.Media;
import at.technikum.domain.Rating;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RatingTest {

    @Test
    void testCreateRating() {
        Rating rating = new Rating("user1", "media1", 5);

        assertEquals("user1", rating.getUserId());
        assertEquals("media1", rating.getMediaId());
        assertEquals(5, rating.getValue());
    }

    @Test
    void testAddRatingToMedia() {
        Media media = new Media();
        Rating rating = new Rating("user1", "media1", 4);

        media.addRating(rating);

        assertEquals(4, media.getRatings().get(0).getValue());

    }
    @Test
    void ratingValueCanBeUpdated() {
        Rating rating = new Rating("user1", "media1", 2);

        assertEquals(2, rating.getValue());

        rating.setValue(5);
        assertEquals(5, rating.getValue());
    }

    @Test
    void ratingKeepsCorrectUserAndMediaReference() {
        Rating rating = new Rating("alice", "movie42", 3);

        assertAll(
                () -> assertEquals("alice", rating.getUserId()),
                () -> assertEquals("movie42", rating.getMediaId()),
                () -> assertEquals(3, rating.getValue())
        );
    }

}

