package at.technikum.server.handlers;

import at.technikum.domain.User;
import at.technikum.persistence.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public class UserHandler implements HttpHandler {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public UserHandler(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
           handleRegister(exchange);
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }
    private void handleRegister(HttpExchange exchange) throws IOException {
       try{

           InputStream requestBody = exchange.getRequestBody();
           User user = objectMapper.readValue(requestBody, User.class);
           User savedUser = userRepository.save(user);
           String responseData = objectMapper.writeValueAsString(savedUser);

           exchange.getResponseHeaders().set("Content-Type", "application/json");
           exchange.sendResponseHeaders(201, responseData.length());

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseData.getBytes());
        }
    } catch (Exception e){
        e.printStackTrace();
        String errorResponse= "{\"error\": \"User could not be created\"}";
        exchange.sendResponseHeaders(500, errorResponse.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(errorResponse.getBytes());

        }
    }
    }
}

