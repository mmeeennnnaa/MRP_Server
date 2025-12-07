package at.technikum.domain;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.w3c.dom.ls.LSException;

import java.util.List;

public class Media
{
    private Integer id;
    private String title;
    private String mediaType;
    private String description;
    private Integer releaseYear;
    private Integer ageRestriction;
    private List<String> genres;
    private Integer creatorId;

    public Media() {
    }

    public Integer getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getMediaType() {
        return mediaType;
    }
    public String getDescription() {
        return description;
    }
    public Integer getReleaseYear() {
        return releaseYear;
    }
    public Integer getAgeRestriction() {
        return ageRestriction;
    }
    public List<String> getGenres() {
        return genres;
    }
    public Integer getCreatorId() {
        return creatorId;
    }

    public static MediaBuilder builder() {
        return new MediaBuilder();
    }
    public static class MediaBuilder {
        private final Media media = new Media();

        public MediaBuilder id(Integer id) {
            media.id = id;
            return this;
        }
        public MediaBuilder title(String title) {
            media.title = title;
            return this;
        }
        public MediaBuilder mediaType(String mediaType) {
            media.mediaType = mediaType;
            return this;
        }
        public MediaBuilder description(String description) {
            media.description = description;
            return this;
        }
        public MediaBuilder releaseYear(Integer releaseYear) {
            media.releaseYear = releaseYear;
            return this;
        }
        public MediaBuilder ageRestriction(Integer ageRestriction) {
            media.ageRestriction = ageRestriction;
            return this;
        }
        public MediaBuilder genre(List<String> genres) {
            media.genres = genres;
            return this;
        }
        public MediaBuilder creatorId(Integer creatorId) {
            media.creatorId = creatorId;
            return this;
        }
        public Media build() {
            return media;
        }
    }
}
