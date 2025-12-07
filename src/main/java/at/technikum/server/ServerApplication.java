package at.technikum.server;

import at.technikum.data.Database;
import at.technikum.persistence.UserRepository;
import at.technikum.persistence.MediaRepository;
import at.technikum.server.handlers.LoginHandler;
import at.technikum.server.handlers.UserHandler;
import at.technikum.server.handlers.MediaHandler;
import com.sun.net.httpserver.HttpServer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.InetSocketAddress;


public class ServerApplication {

    public void start() throws IOException {
        // HTTP Server erstellen
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        Database database = new Database();
        UserRepository userRepository = new UserRepository(database);
        ObjectMapper objectMapper = new ObjectMapper();
        MediaRepository mediaRepository = new MediaRepository(database);

        server.createContext("/api/users/register", new UserHandler(userRepository, objectMapper));
        server.createContext("/api/users/login", new LoginHandler(userRepository, objectMapper));
        server.createContext("/api/media", new MediaHandler(mediaRepository, userRepository, objectMapper));
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on http://localhost:8080");
    }
}
