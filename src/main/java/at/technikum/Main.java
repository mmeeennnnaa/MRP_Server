package at.technikum;

import at.technikum.data.Database;
import at.technikum.server.ServerApplication;

import java.io.IOException;
import java.sql.Connection;

public class Main {

    public static void main(String[] args) {

        System.out.println("Starting MRP Server...");

        if (!testDatabaseConnection()) {
            System.out.println("Database connection failed. Server startup aborted.");
            return;
        }

        startServer();
    }

    // ---------------------------------------------------------------------
    // Test database connectivity once at startup
    // ---------------------------------------------------------------------
    private static boolean testDatabaseConnection() {
        try {
            Database db = new Database();
            try (Connection conn = db.getConnection()) {
                System.out.println("✔ Database connection established");
            }
            return true;
        } catch (Exception e) {
            System.out.println("✘ Could not connect to the database");
            e.printStackTrace();
            return false;
        }
    }

    // ---------------------------------------------------------------------
    // Start HTTP server
    // ---------------------------------------------------------------------
    private static void startServer() {
        ServerApplication server = new ServerApplication();

        try {
            server.start();
            System.out.println("✔ MRP Server is running");
        } catch (IOException e) {
            System.out.println("✘ Failed to start server");
            e.printStackTrace();
        }
    }
}
