package at.technikum.persistence;

import at.technikum.data.Database;
import at.technikum.domain.Media;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class MediaRepository {
    private final Database database;

    public MediaRepository(Database database) {
        this.database = database;
    }

    public Media save(Media media) {
        String sql = "INSERT INTO media (type, title, description, release_year, age_restriction, genre, creator_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = database.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, media.getType());
            statement.setString(2, media.getTitle());
            statement.setString(3, media.getDescription());
            statement.setInt(4, media.getReleaseYear());
            statement.setInt(5, media.getAgeRestriction());
            statement.setString(6, media.getGenre());
            statement.setInt(7, media.getCreatorId());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return Media.builder()
                            .id(generatedKeys.getInt(1))
                            .title(media.getTitle())
                            .type(media.getType())
                            .description(media.getDescription())
                            .releaseYear(media.getReleaseYear())
                            .ageRestriction(media.getAgeRestriction())
                            .genre(media.getGenre())
                            .creatorId(media.getCreatorId())
                            .build();
                } else {
                    throw new SQLException("Creating media failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving media", e);
        }
    }

    public List<Media> findAll() {
        List<Media> mediaList = new ArrayList<>();
        String sql = "SELECT * FROM media";

        try (Connection conn = database.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                mediaList.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding media", e);
        }
        return mediaList;
    }

    public Media findById(int id) {
        String sql = "SELECT * FROM media WHERE id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding media by id", e);
        }
        return null;
    }

    public void update(Media media) {
        String sql = "UPDATE media SET type = ?, title = ?, description = ?, release_year = ?, age_restriction = ?, genre = ?, creator_id = ? WHERE id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, media.getType());
            statement.setString(2, media.getTitle());
            statement.setString(3, media.getDescription());
            statement.setInt(4, media.getReleaseYear());
            statement.setInt(5, media.getAgeRestriction());
            statement.setString(6, media.getGenre());
            statement.setInt(7, media.getCreatorId());
            statement.setInt(8, media.getId());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating media", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM media WHERE id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting media", e);
        }
    }
    //Hilfsmethode um ResultSet in Media zu mappen
    private Media mapResultSet(ResultSet rs) throws SQLException {
        return Media.builder()
                .id(rs.getInt("id"))
                .type(rs.getString("type"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .releaseYear(rs.getInt("release_year"))
                .ageRestriction(rs.getInt("age_restriction"))
                .genre(rs.getString("genre"))
                .creatorId(rs.getInt("creator_id"))
                .build();
    }
}
