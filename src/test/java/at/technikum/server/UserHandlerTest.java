package at.technikum.server;

import at.technikum.server.ServerApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class UserHandlerTest {
    private static final String TEST_USER = "user" + System.currentTimeMillis();
    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void setup() {
        try {
            ServerApplication server = new ServerApplication();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void registerUserWithValidData_ShouldReturn201AndId() throws Exception {
        String username = TEST_USER;

        HttpResponse<String> response = TestSetup.registerUser(username, "password123");

        assertEquals(201, response.statusCode());
        String body = response.body();
        assertTrue(body.contains("\"id\""));
        assertTrue(body.contains(username));
    }
}