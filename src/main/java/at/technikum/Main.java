package at.technikum;

import at.technikum.data.Database;
import at.technikum.server.ServerApplication; // startet HTTP Server

import java.io.IOException;
import java.sql.Connection;

public class Main { // public: von überall starten

    public static void main(String[] args) {

        System.out.println("Starting MRP Server...");

        if (!testDatabaseConnection()) {
            System.out.println("Database connection failed. Server startup aborted.");
            return;
        }

        startServer(); // falls db fkt -> starts http server unten
    }


    // oben aufgerufen und testet nur connection mit db, startet nichts !

    private static boolean testDatabaseConnection() {
        try {
            Database db = new Database(); // erstellt database objekt mit: url, benutzer und pw
            try (Connection conn = db.getConnection()) { // db verb
                System.out.println("✔ Database connection established");
            }
            return true; // db fkt
        } catch (Exception e) {
            System.out.println("✘ Could not connect to the database");
            e.printStackTrace(); // genaue fehlerursache (debug)
            return false;
        }
    }


    // Start HTTP server

    private static void startServer() {
        ServerApplication server = new ServerApplication(); //erstellt server
        try {
            server.start(); //server auf port 8080; requests mögl
            System.out.println("✔ MRP Server is running");
        } catch (IOException e) { //server start fehler
            System.out.println("✘ Failed to start server");
            e.printStackTrace();
        }
    }
}
