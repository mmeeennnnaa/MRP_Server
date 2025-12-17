package at.technikum.persistence;

import at.technikum.data.Database;
import at.technikum.domain.User;

import java.sql.*;

public class UserRepository {

    private final Database db;

    public UserRepository(Database db) { //Eine Klasse, die alle Datenbankzugriffe für ein bestimmtes Domain-Objekt kapselt
        this.db = db; // db von aussen übergeben
    }

    // -----------------------------
    // CREATE USER
    // -----------------------------
    public User save(User user) {
        final String sql = """
            INSERT INTO users (username, password)
            VALUES (?, ?)
            """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.executeUpdate(); // führt INSERT aus

            try (ResultSet keys = ps.getGeneratedKeys()) { // holt vonn db erzeugte id
                if (keys.next()) {
                    User created = User.builder() // neues obj/user
                            .id(keys.getInt(1))
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .token(null)
                            .build();
                    return created;
                }
            }

            throw new SQLException("No generated user ID returned.");

        } catch (SQLException ex) {
            throw new RuntimeException("Could not save new user", ex);
        }
    }

    // -----------------------------
    // FIND BY USERNAME
    // -----------------------------
    public User findByUsername(String username) {
        final String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery(); //Für SELECT, Gibt ein ResultSet zurück

            if (rs.next()) { // Wenn ein Datensatz existiert:
                return map(rs); // DB-Zeile → User-Objekt
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load user by username: " + username, ex);
        }

        return null;
    }

    // -----------------------------
    // UPDATE TOKEN
    // -----------------------------
    public void saveToken(int userId, String token) {
        final String sql = "UPDATE users SET token = ? WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, token); // token wird user zugeordnet
            ps.setInt(2, userId);
            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException("Could not store token for user id=" + userId, ex);
        }
    }

    // -----------------------------
    // FIND BY TOKEN
    // -----------------------------
    public User findByToken(String token) {
        final String sql = "SELECT * FROM users WHERE token = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) { // falls gültiger token
                return map(rs);
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load user by token", ex);
        }

        return null;
    }

    // -----------------------------
    // HELPER: MAP RESULTSET → USER
    // -----------------------------
    private User map(ResultSet rs) throws SQLException { // db zeile in ein userobjekt
        return User.builder()
                .id(rs.getInt("id"))
                .username(rs.getString("username"))
                .password(rs.getString("password"))
                .token(rs.getString("token"))
                .build();
    }
}
