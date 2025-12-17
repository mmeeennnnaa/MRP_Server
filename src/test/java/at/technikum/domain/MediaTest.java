package at.technikum.domain;

import at.technikum.domain.Media;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MediaTest {

    @Test
    void mediaShouldInitializeAllSettableProperties() {
        Media media = new Media();
        //basisfelder eines media objekts
        media.setId(42);
        media.setTitle("Arrival");
        media.setMediaType("Film");
        media.setDescription("Alien linguistics");
        media.setReleaseYear(2016);
        media.setAgeRestriction(12);

        //prüfen ob alle getter die korrekten werte zurückgeben
        assertAll(
                () -> assertEquals(42, media.getId()),
                () -> assertEquals("Arrival", media.getTitle()),
                () -> assertEquals("Film", media.getMediaType()),
                () -> assertEquals("Alien linguistics", media.getDescription()),
                () -> assertEquals(2016, media.getReleaseYear()),
                () -> assertEquals(12, media.getAgeRestriction())
        );
    }

    @Test
    void genresListShouldBeModifiableAndIndependent() {
        Media media = new Media();
        // genre liste
        List<String> genresInput = new ArrayList<>();
        genresInput.add("Horror");
        genresInput.add("Thriller");

        media.setGenres(genresInput);

        assertEquals(2, media.getGenres().size());
        assertTrue(media.getGenres().contains("Horror"));

        // media darf nicht die gleiche referenz der liste verwenden -> muss eigene kopie anlegen
        genresInput.add("Comedy");
        assertEquals(2, media.getGenres().size());
    }

    @Test
    void builderShouldConstructMediaWithSelectedValues() { // testet builder -> alternative art ein objekt zu erzeugen
        Media created = Media.builder()
                .id(11)
                .title("Ghost in the Shell")
                .mediaType("Anime")
                .ageRestriction(16)
                .releaseYear(1995)
                .genres(List.of("Sci-Fi", "Cyberpunk"))
                .build();
        // alle gesetzten werte überprüfen, builder in API zb beim erstellen neuer media einträge genutzt
        assertAll(
                () -> assertEquals(11, created.getId()),
                () -> assertEquals("Ghost in the Shell", created.getTitle()),
                () -> assertEquals("Anime", created.getMediaType()),
                () -> assertEquals(16, created.getAgeRestriction()),
                () -> assertEquals(1995, created.getReleaseYear()),
                () -> assertEquals(2, created.getGenres().size())
        );
    }

    @Test
    void creatorCanBeUpdatedIndependently() {
        Media media = new Media();

        // prüfe ob creatorID korret gespeichert und veränderbar ist
        media.setCreatorId(1);
        assertEquals(1, media.getCreatorId());

        media.setCreatorId(99);
        assertEquals(99, media.getCreatorId());
    }

    @Test
    void descriptionShouldAllowNullOrEmpty() {
        Media media = new Media();
        // beschreibung darf optional sein
        media.setDescription(null);
        assertNull(media.getDescription());

        media.setDescription("");
        assertEquals("", media.getDescription());
    }

    @Test
    void ratingsListShouldStartEmpty() {
        Media media = new Media();

        assertNotNull(media.getRatings());
        assertTrue(media.getRatings().isEmpty());
    }

    @Test
    void idCanBeAssignedLater() {
        Media media = new Media();

        assertNull(media.getId());

        media.setId(10);
        assertEquals(10, media.getId());

        media.setId(20);
        assertEquals(20, media.getId());
    }



}
