package at.technikum.server.handlers;

import at.technikum.domain.User;
import at.technikum.persistence.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LoginHandler implements HttpHandler { // MUSS eine methode handle haben

    private final UserRepository users; // zugriff auf user daten in db
    private final ObjectMapper mapper; // JSON ↔ Java

    // KONSTRUKTOR ( wenn: new LoginHandler(...) )

    public LoginHandler(UserRepository users, ObjectMapper mapper) { // Dependency Injection
        this.users = users;
        this.mapper = mapper;
    }

    @Override // bei jedem login angerufen
    public void handle(HttpExchange exchange) throws IOException {

        // only POST allowed
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) { //Ist die HTTP-Methode POST?
            exchange.sendResponseHeaders(405, -1); // 405: method not allowed -> login darf nur post sein nie get
            return;
        }

        try {
            User requestUser = readRequestBody(exchange); // lies json aus request -> wandelt in user objekt

            // validate request
            if (requestUser == null || // leeres json
                    requestUser.getUsername() == null ||
                    requestUser.getPassword() == null) {

                writeJson(exchange, 400, // bad request
                        "{\"message\":\"Username and password are required\"}");
                return;
            }

            // find user
            User dbUser = users.findByUsername(requestUser.getUsername());

            // check credentials
            if (dbUser == null || // user existiert nicht
                    !dbUser.getPassword().equals(requestUser.getPassword())) { // pw falsch

                writeJson(exchange, 401,
                        "{\"message\":\"Invalid username or password\"}");
                return;
            }

            // generate token
            String token = generateToken(dbUser.getUsername());

            // set token on user object
            dbUser.setToken(token);

            // save token in db
            users.saveToken(dbUser.getId(), token);

            // response
            String responseJson = "{ \"token\": \"" + token + "\" }";
            writeJson(exchange, 200, responseJson); //success

        } catch (Exception e) {
            e.printStackTrace();
            writeJson(exchange, 500,
                    "{\"message\":\"Internal server error\"}");
        }
    }

    // HELPER

    private User readRequestBody(HttpExchange exchange) { //wandelt json -> user-obj
        try (InputStream is = exchange.getRequestBody()) {
            return mapper.readValue(is, User.class); //mapper = ObjectMapper; readValue(...): lies json und bau user obj
        } catch (IOException e) {
            return null;
        }
    }

    private String generateToken(String username) { //created token
        return username + "-mrpToken";
    }

    private void writeJson(HttpExchange exchange, int status, String json) throws IOException { // schreibt http antwort; besteht aus statuscode header und body(json)
        byte[] bytes = json.getBytes();
        exchange.getResponseHeaders().add("Content-Type", "application/json"); // setzt http header
        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes); // postman zeigt answer
        }
    }
}
