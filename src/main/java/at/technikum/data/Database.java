package at.technikum.data;

import java.sql.*;

public class Database {

    // Verbindungsinformationen aus deiner docker-compose.yaml
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "mysecretpassword";

    public Connection getConnection() {
        try {
            // Versucht, eine Verbindung aufzubauen
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            // Wenn die DB nicht erreichbar ist, ist das ein kritischer Fehler
            throw new RuntimeException("Failed to connect to database", e);
        }
    }
}