package at.technikum.server.handlers;

import at.technikum.domain.User;
import at.technikum.persistence.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LoginHandler implements HttpHandler {

    private final UserRepository users;
    private final ObjectMapper mapper;

    public LoginHandler(UserRepository users, ObjectMapper mapper) {
        this.users = users;
        this.mapper = mapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // Allow only POST
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            User requestUser = readRequestBody(exchange);

            if (requestUser == null ||
                    requestUser.getUsername() == null ||
                    requestUser.getPassword() == null) {

                writeJson(exchange, 400, "{\"message\":\"Both username and password must be provided\"}");
                return;
            }

            User dbUser = users.findByUsername(requestUser.getUsername());

            // Validate login
            if (dbUser == null || !dbUser.getPassword().equals(requestUser.getPassword())) {
                writeJson(exchange, 401, "{\"message\":\"Invalid username or password\"}");
                return;
            }

            // Generate token
            String token = generateToken(dbUser.getUsername());

            // Persist token
            users.saveToken(dbUser.getId(), token);

            String response = "{ \"token\": \"" + token + "\" }";
            writeJson(exchange, 200, response);

        } catch (Exception ex) {
            ex.printStackTrace();
            writeJson(exchange, 500, "{\"message\":\"Internal server error during login\"}");
        }
    }

    // --------------------- Helper Methods ---------------------

    private User readRequestBody(HttpExchange exchange) {
        try (InputStream is = exchange.getRequestBody()) {
            return mapper.readValue(is, User.class);
        } catch (IOException e) {
            return null;
        }
    }

    private String generateToken(String username) {
        return username + "-mrpToken";
    }

    private void writeJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes();
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
