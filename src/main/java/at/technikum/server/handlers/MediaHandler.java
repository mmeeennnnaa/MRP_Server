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

    private final MediaRepository mediaRepo;
    private final UserRepository userRepo;
    private final ObjectMapper mapper;

    public MediaHandler(MediaRepository mediaRepo, UserRepository userRepo, ObjectMapper mapper) {
        this.mediaRepo = mediaRepo;
        this.userRepo = userRepo;
        this.mapper = mapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final String method = exchange.getRequestMethod();
        final String path = exchange.getRequestURI().getPath();

        try {
            // --- /api/media ---
            if (path.equals("/api/media")) {
                switch (method.toUpperCase()) {
                    case "GET" -> getAll(exchange);
                    case "POST" -> createMedia(exchange);
                    default -> sendJson(exchange, 405, "{\"message\":\"Method not allowed\"}");
                }
                return;
            }

            // --- /api/media/{id} ---
            if (path.startsWith("/api/media/")) {
                int id = extractId(path);

                switch (method.toUpperCase()) {
                    case "GET" -> getOne(exchange, id);
                    case "PUT" -> updateMedia(exchange, id);
                    case "DELETE" -> deleteMedia(exchange, id);
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

    // --------------------------------------------------------------------
    // GET: ALL MEDIA
    // --------------------------------------------------------------------
    private void getAll(HttpExchange exchange) throws IOException {
        List<Media> result = mediaRepo.findAll();
        sendJson(exchange, 200, mapper.writeValueAsString(result));
    }

    // --------------------------------------------------------------------
    // GET: SINGLE MEDIA
    // --------------------------------------------------------------------
    private void getOne(HttpExchange exchange, int id) throws IOException {
        Media m = mediaRepo.findById(id);

        if (m == null) {
            sendJson(exchange, 404, "{\"message\":\"Media not found\"}");
            return;
        }

        sendJson(exchange, 200, mapper.writeValueAsString(m));
    }

    // --------------------------------------------------------------------
    // CREATE MEDIA (AUTH REQUIRED)
    // --------------------------------------------------------------------
    private void createMedia(HttpExchange exchange) throws IOException {
        User user = authorize(exchange);
        if (user == null) return;

        Media input = readBody(exchange, Media.class);

        Media newMedia = Media.builder()
                .title(input.getTitle())
                .mediaType(input.getMediaType())
                .description(input.getDescription())
                .releaseYear(input.getReleaseYear())
                .ageRestriction(input.getAgeRestriction())
                .genres(input.getGenres())
                .creatorId(user.getId())
                .build();

        Media saved = mediaRepo.save(newMedia);
        sendJson(exchange, 201, mapper.writeValueAsString(saved));
    }

    // --------------------------------------------------------------------
    // UPDATE MEDIA (MUST BE CREATOR)
    // --------------------------------------------------------------------
    private void updateMedia(HttpExchange exchange, int id) throws IOException {
        User user = authorize(exchange);
        if (user == null) return;

        Media existing = mediaRepo.findById(id);

        if (existing == null) {
            sendJson(exchange, 404, "{\"message\":\"Media not found\"}");
            return;
        }

        if (!existing.getCreatorId().equals(user.getId())) {
            sendJson(exchange, 403, "{\"message\":\"Only creator may update this entry\"}");
            return;
        }

        Media input = readBody(exchange, Media.class);

        Media updated = Media.builder()
                .id(id)
                .title(input.getTitle())
                .mediaType(input.getMediaType())
                .description(input.getDescription())
                .releaseYear(input.getReleaseYear())
                .ageRestriction(input.getAgeRestriction())
                .genres(input.getGenres())
                .creatorId(existing.getCreatorId())
                .build();

        mediaRepo.update(updated);
        sendJson(exchange, 200, mapper.writeValueAsString(updated));
    }

    // --------------------------------------------------------------------
    // DELETE MEDIA (MUST BE CREATOR)
    // --------------------------------------------------------------------
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

        mediaRepo.delete(id);
        sendJson(exchange, 204, "");
    }

    // --------------------------------------------------------------------
    // AUTH HELPER
    // --------------------------------------------------------------------
    private User authorize(HttpExchange exchange) throws IOException {
        String header = exchange.getRequestHeaders().getFirst("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            sendJson(exchange, 401, "{\"message\":\"Missing or invalid token\"}");
            return null;
        }

        String token = header.substring("Bearer ".length()).trim();
        User user = userRepo.findByToken(token);

        if (user == null) {
            sendJson(exchange, 401, "{\"message\":\"Token not recognized\"}");
            return null;
        }

        return user;
    }

    // --------------------------------------------------------------------
    // GENERIC HELPERS
    // --------------------------------------------------------------------
    private int extractId(String path) {
        return Integer.parseInt(path.substring("/api/media/".length()));
    }

    private <T> T readBody(HttpExchange exchange, Class<T> type) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return mapper.readValue(is, type);
        }
    }

    private void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes();
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
