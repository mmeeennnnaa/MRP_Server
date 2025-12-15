package at.technikum.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
 //properties datei
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/mrpdb";
    private static final String DB_USER = "mrp";
    private static final String DB_PASS = "mrp123";

    public Connection getConnection() {
        return createConnection();
    }

    private Connection createConnection() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (SQLException sqlEx) {
            String msg = "Database connection error: " + sqlEx.getMessage();
            throw new IllegalStateException(msg, sqlEx);
        }
    }
}
