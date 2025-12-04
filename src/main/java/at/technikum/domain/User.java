package at.technikum.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class User {
    private Integer id;
    private String username;
    private String password;

        public User() {
    }
    public Integer getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username +
                '}';
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }
    public static class UserBuilder {
        private final User user = new User();
        public UserBuilder id(Integer id) {
            user.id = id;
            return this;
        }
        public UserBuilder username(String username) {
            user.username = username;
            return this;
        }
        public UserBuilder password(String password) {
            user.password = password;
            return this;
        }
        public User build() {
            if(user.username == null || user.password == null) {
                throw new IllegalStateException("username and password cannot be null");
            }
            return user;
        }
    }
}
