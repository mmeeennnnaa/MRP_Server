package at.technikum.domain;

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
}

