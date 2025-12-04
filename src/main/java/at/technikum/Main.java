package at.technikum;

import at.technikum.data.Database;
import at.technikum.persistence.UserRepository;
import at.technikum.domain.User;

public class Main {
    public static void main(String[] args) {
        System.out.println("App is starting...");
        Database database = new Database();
        UserRepository userRepository = new UserRepository(database);

        System.out.println("Teste Insert...");
        User neuerUser = User.builder()
                .username("testuser")
                .password("12345")
                .build();

        userRepository.insert(neuerUser);
        System.out.println("Neuer User eingefügt: " + neuerUser);
    }
}