import server.Server;

import java.io.IOException;
/**
 * Class for testing purposes, please ignore.
 */
public class startServer {

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
