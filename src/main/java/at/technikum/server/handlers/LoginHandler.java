package at.technikum.server.handlers;

import at.technikum.domain.User;
import at.technikum.persistence.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public class LoginHandler implements HttpHandler {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public LoginHandler(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            InputStream requestBody = exchange.getRequestBody();
            User loginRequest = objectMapper.readValue(requestBody, User.class);
            User userFromDb = userRepository.findByUsername(loginRequest.getUsername());

            if (userFromDb != null && userFromDb.getPassword().equals(loginRequest.getPassword())) {

                String token = userFromDb.getUsername() + "-mrpToken";

                String response = "{\"token\": \"" + token + "\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                String response = "{\"error\": \"invalid request\"}";
                exchange.sendResponseHeaders(401, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        } else{
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }
}