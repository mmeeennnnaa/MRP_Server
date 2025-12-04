package at.technikum.server;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.io.OutputStream;


public class ServerApplication {

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(10001), 0);
        server.createContext("/echo", exchange -> {
            String response = "Server is running!";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });

        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started on port 10001...");
    }
}
