package at.technikum.server.handlers;

import at.technikum.domain.User;
import at.technikum.persistence.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UserHandler implements HttpHandler {

    private final UserRepository users; // db zugriff für user
    private final ObjectMapper mapper; //JSON ↔ Java

    public UserHandler(UserRepository users, ObjectMapper mapper) {
        this.users = users; //Übergibt Abhängigkeiten an die Klasse
        this.mapper = mapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException { // exchange enthält http method, url, header, body, response

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1); // only POST allowed
            return;
        }

        try {
            User incoming = parseRequest(exchange); // liest request body

            if (!isValid(incoming)) {
                writeJson(exchange, 400, "{\"message\":\"Username and password must not be empty\"}");
                return;
            }

            // check for duplicates
            if (users.findByUsername(incoming.getUsername()) != null) {
                writeJson(exchange, 409, "{\"message\":\"Username already exists\"}");
                return;
            }

            User saved = users.save(incoming); // repo speichert user in db, db gibt id ( handler kennt kein sql )

            // build safe response (no password zurückschicken!)
            String json = String.format("""
                {
                  "id": %d,
                  "username": "%s"
                }
                """, saved.getId(), saved.getUsername());

            writeJson(exchange, 201, json); // user erfolgreich erstellt

        } catch (Exception ex) {
            ex.printStackTrace();
            writeJson(exchange, 500, "{\"message\":\"Unable to register user\"}");
        }
    }

    // ---------------------------- HELPER METHODS ----------------------------

    private User parseRequest(HttpExchange exchange) {
        try (InputStream is = exchange.getRequestBody()) {
            return mapper.readValue(is, User.class); // Objectmapper macht die arbeit
        } catch (IOException e) {
            return null;
        }
    }

    private boolean isValid(User user) { // falls kein json oder kaputtes json
        if (user == null) return false;
        if (user.getUsername() == null || user.getUsername().isBlank()) return false; // isblank verhindert leere strings und leerzeichen
        if (user.getPassword() == null || user.getPassword().isBlank()) return false;
        return true;
    }

    private void writeJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] data = json.getBytes();
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, data.length); // http statuscode

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(data); // schickt antwort an client
        }
    }
}
