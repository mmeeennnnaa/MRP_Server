package at.technikum.server;

import at.technikum.data.Database;
import at.technikum.persistence.UserRepository;
import at.technikum.server.handlers.LoginHandler;
import at.technikum.server.handlers.UserHandler;
import com.sun.net.httpserver.HttpServer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.InetSocketAddress;


public class ServerApplication {

    public void start() throws IOException {
        // HTTP Server erstellen
        HttpServer server = HttpServer.create(new InetSocketAddress(10001), 0);

        Database database = new Database();
        UserRepository userRepository = new UserRepository(database);
        ObjectMapper objectMapper = new ObjectMapper();

        server.createContext("/users", new UserHandler(userRepository, objectMapper));
        server.createContext("/users/login", new LoginHandler(userRepository, objectMapper));
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 10001");
    }
}
