package at.technikum.persistence;

import at.technikum.domain.User;
import at.technikum.data.Database;
import java.sql.*;

public class UserRepository {
    private final Database database;

    public UserRepository(Database database) {
        this.database = database;
    }

    public User save(User user) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = database.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.executeUpdate();
           //Die generierte ID abholen!
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return User.builder()
                            .id(generatedKeys.getInt(1)) // Die ID von Postgres (Spalte 1)
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .build();
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving user", e);
        }
    }

    public User findbyUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = database.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return User.builder()
                        .id(rs.getInt("id"))
                        .username(rs.getString("username"))
                        .password(rs.getString("password"))
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding user by username", e);
        }
        return null;
    }
}