package at.technikum;

import at.technikum.server.ServerApplication;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        ServerApplication server = new ServerApplication();
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}