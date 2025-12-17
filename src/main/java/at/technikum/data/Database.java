package at.technikum.data;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {

    private final String url;
    private final String user;
    private final String password;

    public Database() {
        Properties props = new Properties();

        try (InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (is == null) {
                throw new IllegalStateException("application.properties not found");
            }

            props.load(is);

            this.url = props.getProperty("db.url");
            this.user = props.getProperty("db.user");
            this.password = props.getProperty("db.password");

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load database configuration", e);
        }
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new IllegalStateException("Database connection error", e);
        }
    }
}
