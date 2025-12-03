package at.technikum;

import at.technikum.data.Database;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Server starting...");

        Database db = new Database();

        // Teste die Verbindung (wie in deinem Snippet, aber sauber getrennt)
        try (Connection conn = db.getConnection()) {
            System.out.println("Connection to Database successful!");
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }
}