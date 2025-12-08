package at.technikum.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testUserBuilder() {
        User user = User.builder()
                .id(1)
                .username("testuser")
                .password("password123")
                .build();

        assertEquals(1, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
    }

    @Test
    public void testUserToString() {
        User user = User.builder()
                .id(2)
                .username("anotheruser")
                .password("securepass")
                .build();

        String expectedString = "User{id=2, username='anotheruser'}";
        assertEquals(expectedString, user.toString());
    }

    @Test
    public void testSetAndGetId() {
        User user = new User();
        user.setId(5);
        assertEquals(5, user.getId());
    }

    @Test
    public void testSetAndGetUsername() {
        User user = new User();
        user.setUsername("myusername");
        assertEquals("myusername", user.getUsername());
    }

    @Test
    public void testSetAndGetPassword() {
        User user = new User();
        user.setPassword("mypassword");
        assertEquals("mypassword", user.getPassword());
    }

    @Test
    public void testBuilderMissingUsername() {
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            User.builder()
                .password("password123")
                .build();
        });

        String expectedMessage = "username and password cannot be null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testBuilderMissingPassword() {
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            User.builder()
                    .username("testuser")
                    .build();
        });
        String expectedMessage = "username and password cannot be null";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testSetIdAllowsNull() {
        User user = new User();
        user.setId(null);
        assertNull(user.getId());
    }

    @Test
    public void testTwoDifferentUsersAreNotEqual() {
        User user1 = User.builder()
                .username("user1")
                .password("password1")
                .build();

        User user2 = User.builder()
                .username("user2")
                .password("password2")
                .build();

        assertNotEquals(user1, user2);
    }

    @Test
    public void testChangePassword() {
        User user = new User();

        user. setPassword("oldpassword");
        user.setPassword("newpassword");

        assertEquals("newpassword", user.getPassword());
        assertNotEquals("oldpassword", user.getPassword());
    }
}
