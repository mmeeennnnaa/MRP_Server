package at.technikum.domain;

import at.technikum.domain.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void builderShouldCreateValidUserObject() {
        User u = User.builder()
                .id(1)
                .username("mina")
                .password("secure123")
                .token("token-xyz")
                .build();

        assertAll(
                () -> assertEquals(1, u.getId()),
                () -> assertEquals("mina", u.getUsername()),
                () -> assertEquals("secure123", u.getPassword()),
                () -> assertEquals("token-xyz", u.getToken())
        );
    }

    @Test
    void fieldsCanBeModifiedAfterCreation() {
        User u = new User();

        u.setId(500);
        u.setUsername("initialUser");
        u.setPassword("initPass");
        u.setToken("initToken");

        assertAll(
                () -> assertEquals(500, u.getId()),
                () -> assertEquals("initialUser", u.getUsername()),
                () -> assertEquals("initPass", u.getPassword()),
                () -> assertEquals("initToken", u.getToken())
        );

        u.setUsername("updatedUser");
        u.setPassword("updatedPass");
        u.setToken("updatedToken");

        assertAll(
                () -> assertEquals("updatedUser", u.getUsername()),
                () -> assertEquals("updatedPass", u.getPassword()),
                () -> assertEquals("updatedToken", u.getToken())
        );
    }

    @Test
    void builderShouldThrowIfUsernameMissing() {
        Exception error = assertThrows(
                IllegalStateException.class,
                () -> User.builder()
                        .password("pw123")
                        .build()
        );

        assertTrue(error.getMessage().contains("username"));
    }

    @Test
    void builderShouldThrowIfPasswordMissing() {
        Exception error = assertThrows(
                IllegalStateException.class,
                () -> User.builder()
                        .username("mina")
                        .build()
        );

        assertTrue(error.getMessage().contains("password"));
    }

    @Test
    void tokenCanBeSetIndependently() {
        User u = new User();

        u.setToken("firstToken");
        assertEquals("firstToken", u.getToken());

        u.setToken("secondToken");
        assertEquals("secondToken", u.getToken());
        assertNotEquals("firstToken", u.getToken());
    }

    @Test
    void idCanBeNullWithoutIssues() {
        User u = new User();
        u.setId(null);

        assertNull(u.getId());
    }

    @Test
    void toStringShouldExposeOnlyIdAndUsername() {
        User u = User.builder()
                .id(77)
                .username("ghostUser")
                .password("invisiblePass")
                .token("hiddenToken")
                .build();

        String output = u.toString();

        assertTrue(output.contains("ghostUser"));
        assertTrue(output.contains("77"));

        assertFalse(output.contains("invisiblePass"));
        assertFalse(output.contains("hiddenToken"));
    }

    @Test
    void differentUsersShouldNotBeEqualEvenWithSimilarValues() {
        User u1 = User.builder()
                .username("someone")
                .password("pass")
                .build();

        User u2 = User.builder()
                .username("someone")
                .password("pass")
                .build();

        assertNotSame(u1, u2);
    }

    @Test
    void builderWorksWithOnlyRequiredFields() {
        User u = User.builder()
                .username("minimalUser")
                .password("minimalPass")
                .build();

        assertAll(
                () -> assertNull(u.getId()),
                () -> assertEquals("minimalUser", u.getUsername()),
                () -> assertEquals("minimalPass", u.getPassword()),
                () -> assertNull(u.getToken())
        );
    }

    @Test
    void usernameCanBeChangedMultipleTimes() {
        User u = new User();

        u.setUsername("firstName");
        assertEquals("firstName", u.getUsername());

        u.setUsername("secondName");
        assertEquals("secondName", u.getUsername());

        u.setUsername("thirdName");
        assertEquals("thirdName", u.getUsername());
    }


}

