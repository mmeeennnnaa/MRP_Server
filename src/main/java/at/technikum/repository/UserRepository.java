package at.technikum.repository;

import at.technikum.domain.User;
import at.technikum.data.Database;
import java.sql.*;

public class UserRepository {
    private final Database database;

    public UserRepository(Database database) {
        this.database = database;
    }

   public void save(User user) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving user", e);
        }
    }
}
