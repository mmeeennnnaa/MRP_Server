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

        // only POST allowed
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            User requestUser = readRequestBody(exchange);

            // validate request
            if (requestUser == null ||
                    requestUser.getUsername() == null ||
                    requestUser.getPassword() == null) {

                writeJson(exchange, 400,
                        "{\"message\":\"Username and password are required\"}");
                return;
            }

            // find user
            User dbUser = users.findByUsername(requestUser.getUsername());

            // check credentials
            if (dbUser == null ||
                    !dbUser.getPassword().equals(requestUser.getPassword())) {

                writeJson(exchange, 401,
                        "{\"message\":\"Invalid username or password\"}");
                return;
            }

            // generate token
            String token = generateToken(dbUser.getUsername());

            // set token on user object
            dbUser.setToken(token);

            // persist token
            users.saveToken(dbUser.getId(), token);

            // response
            String responseJson = "{ \"token\": \"" + token + "\" }";
            writeJson(exchange, 200, responseJson);

        } catch (Exception e) {
            e.printStackTrace();
            writeJson(exchange, 500,
                    "{\"message\":\"Internal server error\"}");
        }
    }

    // ---------------- helper methods ----------------

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
