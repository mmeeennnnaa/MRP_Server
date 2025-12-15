package at.technikum.domain;
import at.technikum.domain.Rating;


import java.util.ArrayList;
import java.util.List;

public class Media {

    private Integer id;
    private String title;
    private String mediaType;
    private String description;
    private Integer releaseYear;
    private Integer ageRestriction;
    private List<String> genres = new ArrayList<>();
    private Integer creatorId;
    private List<Rating> ratings = new ArrayList<>();


    // ---------- CONSTRUCTOR ----------
    public Media() {}

    // ---------- GETTERS ----------
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

    // ---------- SETTERS ----------
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
        this.genres = new ArrayList<>(genres);
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void addRating(Rating rating) {
        this.ratings.add(rating);
    }


    // ---------- BUILDER ----------
    public static MediaBuilder builder() {
        return new MediaBuilder();
    }

    public static class MediaBuilder {

        private final Media instance = new Media();

        public MediaBuilder id(Integer id) {
            instance.id = id;
            return this;
        }

        public MediaBuilder title(String title) {
            instance.title = title;
            return this;
        }

        public MediaBuilder mediaType(String mediaType) {
            instance.mediaType = mediaType;
            return this;
        }

        public MediaBuilder description(String description) {
            instance.description = description;
            return this;
        }

        public MediaBuilder releaseYear(Integer releaseYear) {
            instance.releaseYear = releaseYear;
            return this;
        }

        public MediaBuilder ageRestriction(Integer ageRestriction) {
            instance.ageRestriction = ageRestriction;
            return this;
        }

        public MediaBuilder genres(List<String> genres) {
            instance.genres = new ArrayList<>(genres);
            return this;
        }

        public MediaBuilder creatorId(Integer creatorId) {
            instance.creatorId = creatorId;
            return this;
        }

        public Media build() {
            return instance;
        }
    }
}
