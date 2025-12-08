package at.technikum.domain;

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

    public void setId(Integer id) {
        this.id = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }
    public void setAgeRestriction(Integer ageRestriction) {
        this.ageRestriction = ageRestriction;
    }
    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
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
        public MediaBuilder genres(List<String> genres) {
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
