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
}