package at.technikum.domain;

public class User {

    private Integer id;
    private String username;
    private String password;
    private String token;

    // --- CONSTRUCTORS ---
    public User() {
        // empty constructor required for JSON mapping
    }

    // --- GETTERS ---
    public Integer getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getToken() {
        return this.token;
    }

    // --- SETTERS ---
    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // --- TOSTRING ---
    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "'}";
    }


    // --- BUILDER ---
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {

        private Integer id;
        private String username;
        private String password;
        private String token;

        public UserBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder token(String token) {
            this.token = token;
            return this;
        }

        public User build() {
            if (username == null || password == null) {
                throw new IllegalStateException("User requires both username and password");
            }

            User u = new User();
            u.id = this.id;
            u.username = this.username;
            u.password = this.password;
            u.token = this.token;

            return u;
        }
    }
}
