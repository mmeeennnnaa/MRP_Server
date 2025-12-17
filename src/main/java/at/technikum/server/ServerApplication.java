package at.technikum.server;

import at.technikum.data.Database; //Zugriff auf PostgreSQL
import at.technikum.persistence.UserRepository;
import at.technikum.persistence.MediaRepository;
import at.technikum.server.handlers.LoginHandler;
import at.technikum.server.handlers.UserHandler;
import at.technikum.server.handlers.MediaHandler;
import com.sun.net.httpserver.HttpServer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.InetSocketAddress;

// SERVER / HTTP

public class ServerApplication { // konfig server und startet ihn

    public void start() throws IOException {

        // created neuen http server
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        Database database = new Database(); // erstellt db zugriff -> verb daten kommen aus properties
        UserRepository userRepository = new UserRepository(database); // userHandler braucht Zugriff auf User-Daten -> repo kapselt SQL
        ObjectMapper objectMapper = new ObjectMapper(); // von allen handlern genutzt
        MediaRepository mediaRepository = new MediaRepository(database); // db zugriff für media

        server.createContext("/api/users/register", new UserHandler(userRepository, objectMapper)); // wenn auf register ein request kommt -> userhandler wird aufgerufen
        server.createContext("/api/users/login", new LoginHandler(userRepository, objectMapper)); // login-endpoint : POST -> login => gibt token zrk
        server.createContext("/api/media", new MediaHandler(mediaRepository, userRepository, objectMapper)); // alle media requests: GET / POST / PUT / DELETE (token geschützt)
        server.setExecutor(null);
        server.start(); // requests können rein
        System.out.println("Server started on http://localhost:8080");
    }
}
