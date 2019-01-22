package server.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Server {
    public static final int DEFAULT_PORT = 3001;
    public static int port;

    private List<ClientHandler> clientHandlers = new ArrayList();
    private List<GameHandler> gameHandlers = new ArrayList<>();

    public Server(int port) {
        this.port = port;

    }

    public Server() {
        this.port = DEFAULT_PORT;
    }

    public void log (String message) {
        System.out.println("[" + new Date() + "] " + message);
    }

    public void start () {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Listning on port: " + port);

            while (true) {
                Socket firstPlayer = serverSocket.accept();

                ClientHandler handlerOne = new ClientHandler(this, firstPlayer);
                GameHandler gameHandler = new GameHandler(gameHandlers.size());
                gameHandlers.add(gameHandler);
                gameHandler.addPlayer(handlerOne);
                Socket secondPlayer = serverSocket.accept();

                ClientHandler handlerTwo = new ClientHandler(this, secondPlayer);
                gameHandler.addPlayer (handlerTwo);

            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
