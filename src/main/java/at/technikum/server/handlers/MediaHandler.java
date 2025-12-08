package at.technikum.server.handlers;

import at.technikum.domain.Media;
import at.technikum.domain.User;
import at.technikum.persistence.MediaRepository;
import at.technikum.persistence.UserRepository;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class MediaHandler implements HttpHandler {
    private MediaRepository mediaRepository;
    private UserRepository userRepository;
    private ObjectMapper objectMapper;


    public MediaHandler(MediaRepository mediaRepository, UserRepository userRepository, ObjectMapper objectMapper) {
        this.mediaRepository = mediaRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String Method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (path.equals("/api/media")) {
                if ("GET".equals(Method)) {
                    handleGetAll(exchange);
                } else if ("POST".equals(Method)) {
                    handleCreate(exchange);
                } else {
                    exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                }
            } else if (path.startsWith("/api/media/")) {
                String idString = path.substring("/api/media/".length());
                int mediaId = Integer.parseInt(idString);

                if ("PUT".equals(Method)) {
                    handleUpdate(exchange, mediaId);
                } else if ("DELETE".equals(Method)) {
                    handleDelete(exchange, mediaId);
                } else if ("GET".equals(Method)) {
                    handleGetOne(exchange, mediaId);
                } else {
                    sendResponse(exchange, 405, "Method Not Allowed");
                }
            } else {
                sendResponse(exchange, 404, "Not Found");
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Invalid media ID format");
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }

    private void handleGetAll(HttpExchange exchange) throws IOException {
        List<Media> allMedia = mediaRepository.findAll();
        String resonse = objectMapper.writeValueAsString(allMedia);
        sendResponse(exchange, 200, resonse);
    }

    private void handleGetOne(HttpExchange exchange, int id) throws IOException {
        Media media = mediaRepository.findById(id);
        if (media != null) {
            sendResponse(exchange, 200, objectMapper.writeValueAsString(media));
        } else {
            sendResponse(exchange, 404, "Media not found");
        }
    }
    private void handleCreate(HttpExchange exchange) throws IOException {
        User user = checkAuth(exchange);
        if (user == null) return;

        InputStream requestBody = exchange.getRequestBody();
        Media mediaInput = objectMapper.readValue(requestBody, Media.class);

        Media mediaToSave = Media.builder()
                .title(mediaInput.getTitle())
                .mediaType(mediaInput.getMediaType())
                .description(mediaInput.getDescription())
                .releaseYear(mediaInput.getReleaseYear())
                .ageRestriction(mediaInput.getAgeRestriction())
                .genres(mediaInput.getGenres())
                .creatorId(user.getId())
                .build();
        Media savedMedia = mediaRepository.save(mediaToSave);
        sendResponse(exchange, 201, objectMapper.writeValueAsString(savedMedia));
    }

    private void handleUpdate(HttpExchange exchange, int mediaId) throws IOException {
        User user = checkAuth(exchange);
        if (user == null) return;

        Media existingMedia = mediaRepository.findById(mediaId);
        if (existingMedia == null) {
            sendResponse(exchange, 404, "Media not found");
            return;
        }
        if (!existingMedia.getCreatorId().equals(user.getId())) {
            sendResponse(exchange, 403, "Forbidden: You are not the creator of this media");
            return;
        }

        Media updateData = objectMapper.readValue(exchange.getRequestBody(), Media.class);

        Media mediaToUpdate = Media.builder()
                .id(mediaId)
                .title(updateData.getTitle())
                .mediaType(updateData.getMediaType())
                .description(updateData.getDescription())
                .releaseYear(updateData.getReleaseYear())
                .ageRestriction(updateData.getAgeRestriction())
                .genres(updateData.getGenres())
                .creatorId(existingMedia.getCreatorId())
                .build();

        mediaRepository.update(mediaToUpdate);
        sendResponse(exchange, 200, objectMapper.writeValueAsString(mediaToUpdate));
    }

    private void handleDelete(HttpExchange exchange, int mediaId) throws IOException {
        User user = checkAuth(exchange);
        if (user == null) return;

        Media existingMedia = mediaRepository.findById(mediaId);
        if (existingMedia == null) {
            sendResponse(exchange, 404, "Media not found");
            return;
        }
        if (!existingMedia.getCreatorId().equals(user.getId())) {
            sendResponse(exchange, 403, "Forbidden: You are not the creator of this media");
            return;
        }

        mediaRepository.delete(mediaId);
        sendResponse(exchange, 204, "Media deleted successfully");
    }

    //Hilfsmethoden
    private User checkAuth(HttpExchange exchange) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendResponse(exchange, 401, "Unauthorized: Missing or invalid token");
            return null;
        }
        String token = authHeader.substring(7);
        String [] parts = token.split("-");
        if (parts.length < 2) {
            sendResponse(exchange, 401, "Unauthorized: Invalid token format");
            return null;
        }
        String username = parts[0];
        User user = userRepository.findByUsername(username);
        if (user == null) {
            sendResponse(exchange, 401, "Unauthorized: User not found");
            return null;
        }
        return user;
    }

    private void sendResponse(HttpExchange exchange, int status, String message) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, message.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(message.getBytes());
        }
    }
}

