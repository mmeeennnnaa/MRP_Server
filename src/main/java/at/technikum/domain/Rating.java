package at.technikum.domain;

public class Rating {

    private String userId;
    private String mediaId;
    private int value;

    public Rating() {
    }

    public Rating(String userId, String mediaId, int value) {
        this.userId = userId;
        this.mediaId = mediaId;
        this.value = value;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
