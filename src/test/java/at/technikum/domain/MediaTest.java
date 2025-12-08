package at.technikum.domain;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MediaTest {

    @Test
    public void testSetAndGetId() {
        Media media = new Media();
        media.setId(100);
        assertEquals(100, media.getId());
    }

    @Test
    public void testSetAndGetTitle() {
        Media media = new Media();
        media.setTitle("Inception");
        assertEquals("Inception", media.getTitle());
    }

    @Test
    public void testSetAndGetMediaType() {
        Media media = new Media();
        media.setMediaType("Movie");
        assertEquals("Movie", media.getMediaType());
    }

    @Test
    public void testSetAndGetDescription() {
        Media media = new Media();
        media.setDescription("A mind-bending thriller");
        assertEquals("A mind-bending thriller", media.getDescription());
    }

    @Test
    public void testSetAndGetReleaseYear() {
        Media media = new Media();
        media.setReleaseYear(2010);
        assertEquals(2010, media.getReleaseYear());
    }

    @Test
    public void testSetAndGetAgeRestriction() {
        Media media = new Media();
        media.setAgeRestriction(13);
        assertEquals(13, media.getAgeRestriction());
    }

    @Test
    public void testSetAndGetGenres() {
        Media media = new Media();
        List<String> genres = List.of("Sci-Fi", "Thriller");
        media.setGenres(genres);
            assertNotNull(media.getGenres());
        assertEquals(2, media.getGenres().size());
        assertTrue(media.getGenres().contains("Sci-Fi"));
    }

    @Test
    public void testBuilderCreatesCorrectObject() {
        Media media = Media.builder()
                .id(1)
                .title("The Matrix")
                .mediaType("Movie")
                .build();
        assertEquals(1, media.getId());
        assertEquals("The Matrix", media.getTitle());
        assertEquals("Movie", media.getMediaType());

    }

    @Test
    void testEmptyGenreList() {
        Media media = new Media();

        media.setGenres(List.of());

        assertNotNull(media.getGenres());
        assertTrue(media.getGenres().isEmpty());
    }

    @Test
    void testCreatorIdCanBeUpdated() {
        Media media = new Media();
        media.setCreatorId(42);

        media.setCreatorId(11);
        assertEquals(11, media.getCreatorId());
    }

    @Test
    void testAncientReleaseYear() {
        Media media = new Media();
        media.setReleaseYear(-500); // 500 v. Chr.
        assertEquals(-500, media.getReleaseYear());
    }
}
