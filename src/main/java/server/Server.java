package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The Server creates a Serversocket which waits untill a connection is formed. A connection is put into a ClientHandler. Every two ClientHandlers are put into a GameHandler
 */
public class Server {
    public static final int DEFAULT_PORT = 2001;
    public static int port;

    private List<ClientHandler> clientHandlers = new ArrayList();
    private List<GameHandler> gameHandlers = new ArrayList<>();
    private ServerSocket serverSocket;

    public Server(int port) throws IOException {
        Server.port = port;
        serverSocket = new ServerSocket(port);
        System.out.println("Listning on port: " + port);
    }

    public Server() throws IOException {
        port = DEFAULT_PORT;
        serverSocket = new ServerSocket(port);
        System.out.println("Listning on port: " + port);
    }

    public void log(String message) {
        System.out.println("[" + new Date() + "] " + message);
    }

    public void start() {
        try {
            while (true) {
                Socket firstPlayer = serverSocket.accept();

                ClientHandler handlerOne = new ClientHandler(this, firstPlayer);
                GameHandler gameHandler = new GameHandler(gameHandlers.size());
                gameHandlers.add(gameHandler);
                gameHandler.addPlayer(handlerOne);
                Socket secondPlayer = serverSocket.accept();

                ClientHandler handlerTwo = new ClientHandler(this, secondPlayer);
                gameHandler.addPlayer(handlerTwo);

            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
