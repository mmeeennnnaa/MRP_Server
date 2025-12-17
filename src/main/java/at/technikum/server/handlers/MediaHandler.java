package at.technikum.server.handlers;

import at.technikum.domain.Media;
import at.technikum.domain.User;
import at.technikum.persistence.MediaRepository;
import at.technikum.persistence.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class MediaHandler implements HttpHandler {

    private final MediaRepository mediaRepo; // media aus db lesen und saven
    private final UserRepository userRepo; // user und token prüfen
    private final ObjectMapper mapper; // json <-> java

    // KONSTRUKTOR; bei: new MediaHandler(...)

    public MediaHandler(MediaRepository mediaRepo, UserRepository userRepo, ObjectMapper mapper) {
        this.mediaRepo = mediaRepo; //Dependency Injection
        this.userRepo = userRepo;
        this.mapper = mapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException { // bei jedem request von server aufgerufen; exchange: Methode (GET/POST/PUT/DELETE), url, header, body, response
        final String method = exchange.getRequestMethod();
        final String path = exchange.getRequestURI().getPath();

        try {
            // --- /api/media ---
            if (path.equals("/api/media")) { // get und post
                switch (method.toUpperCase()) {
                    case "GET" -> getAll(exchange); // alle media anzeigen
                    case "POST" -> createMedia(exchange); // neues erstellen, auth required
                    default -> sendJson(exchange, 405, "{\"message\":\"Method not allowed\"}");
                }
                return;
            }

            // --- /api/media/{id} ---
            if (path.startsWith("/api/media/")) {
                int id = extractId(path); // holt id aus url

                switch (method.toUpperCase()) {
                    case "GET" -> getOne(exchange, id); // einzelnes media anzeigen
                    case "PUT" -> updateMedia(exchange, id); // nur ersteller kann ändern
                    case "DELETE" -> deleteMedia(exchange, id); // nur ersteller kann deleten
                    default -> sendJson(exchange, 405, "{\"message\":\"Method not allowed\"}");
                }
                return;
            }

            sendJson(exchange, 404, "{\"message\":\"Unknown route\"}");

        } catch (NumberFormatException nfe) {
            sendJson(exchange, 400, "{\"message\":\"Media ID must be numeric\"}");
        } catch (Exception ex) {
            ex.printStackTrace();
            sendJson(exchange, 500, "{\"message\":\"Unexpected server error\"}");
        }
    }


    // GET: ALL MEDIA

    private void getAll(HttpExchange exchange) throws IOException {
        List<Media> result = mediaRepo.findAll(); // holt alle media aus db
        sendJson(exchange, 200, mapper.writeValueAsString(result)); // wandelt liste -> json und schickt antwort
    }


    // GET: SINGLE MEDIA

    private void getOne(HttpExchange exchange, int id) throws IOException {
        Media m = mediaRepo.findById(id);

        if (m == null) {
            sendJson(exchange, 404, "{\"message\":\"Media not found\"}");
            return;
        }

        sendJson(exchange, 200, mapper.writeValueAsString(m));
    }


    // CREATE MEDIA (AUTH REQUIRED)

    private void createMedia(HttpExchange exchange) throws IOException {
        User user = authorize(exchange); // topken vorhanden und gültig?
        if (user == null) return;

        Media input = readBody(exchange, Media.class); // json -> media objekt

        Media newMedia = Media.builder()
                .title(input.getTitle())
                .mediaType(input.getMediaType())
                .description(input.getDescription())
                .releaseYear(input.getReleaseYear())
                .ageRestriction(input.getAgeRestriction())
                .genres(input.getGenres())
                .creatorId(user.getId()) // media gehört eingeloggtem user
                .build();

        Media saved = mediaRepo.save(newMedia); // speichern in db
        sendJson(exchange, 201, mapper.writeValueAsString(saved));
    }


    // UPDATE MEDIA (MUST BE CREATOR)

    private void updateMedia(HttpExchange exchange, int id) throws IOException { // id in url
        User user = authorize(exchange);  //Übergibt exchange an authorize -> checkt user und token aus db
        if (user == null) return;

        Media existing = mediaRepo.findById(id); // gibt es media mit dieser id?

        if (existing == null) {
            sendJson(exchange, 404, "{\"message\":\"Media not found\"}");
            return;
        }

        if (!existing.getCreatorId().equals(user.getId())) { // user der media erstellt hat != eingeloggter user rn
            sendJson(exchange, 403, "{\"message\":\"Only creator may update this entry\"}");
            return;
        }

        Media input = readBody(exchange, Media.class); // liest json aus request und wandelt in media objekt

        // neues objekt kein in place update
        Media updated = Media.builder()
                .id(id) // id aus url nicht body
                .title(input.getTitle())
                .mediaType(input.getMediaType())
                .description(input.getDescription())
                .releaseYear(input.getReleaseYear())
                .ageRestriction(input.getAgeRestriction())
                .genres(input.getGenres())
                .creatorId(existing.getCreatorId())
                .build();

        mediaRepo.update(updated); // SQL UPDATE
        sendJson(exchange, 200, mapper.writeValueAsString(updated));
    }


    // DELETE MEDIA (MUST BE CREATOR)

    private void deleteMedia(HttpExchange exchange, int id) throws IOException {
        User user = authorize(exchange);
        if (user == null) return;

        Media existing = mediaRepo.findById(id);

        if (existing == null) {
            sendJson(exchange, 404, "{\"message\":\"Media not found\"}");
            return;
        }

        if (!existing.getCreatorId().equals(user.getId())) {
            sendJson(exchange, 403, "{\"message\":\"Only creator may delete this media\"}");
            return;
        }

        mediaRepo.delete(id); //SQL: DELETE FROM media WHERE id = ?
        sendJson(exchange, 204, "");
    }


    // AUTH HELPER

    private User authorize(HttpExchange exchange) throws IOException {
        String header = exchange.getRequestHeaders().getFirst("Authorization"); // holt: Authorization: Bearer admin-mrpToken

        if (header == null || !header.startsWith("Bearer ")) { // kein token
            sendJson(exchange, 401, "{\"message\":\"Missing or invalid token\"}");
            return null;
        }

        String token = header.substring("Bearer ".length()).trim(); // trimt Bearer
        User user = userRepo.findByToken(token); // prüft token in db

        if (user == null) {
            sendJson(exchange, 401, "{\"message\":\"Token not recognized\"}");
            return null;
        }

        return user; // success
    }


 // GENERIC HELPERS

    private int extractId(String path) { // id aus url
        return Integer.parseInt(path.substring("/api/media/".length()));
    }

    private <T> T readBody(HttpExchange exchange, Class<T> type) throws IOException { // json -> java obj, nutzt ObjectMapper
        try (InputStream is = exchange.getRequestBody()) {
            return mapper.readValue(is, type);
        }
    }

    private void sendJson(HttpExchange exchange, int status, String json) throws IOException { // baut http antwort: setzt atatuscode, header und body
        byte[] bytes = json.getBytes();
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
