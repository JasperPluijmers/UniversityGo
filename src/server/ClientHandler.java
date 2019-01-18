package server;

import go.model.Board;
import go.utility.Player;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread implements Player {

    private Server server;
    private Socket socket;
    private BufferedReader inStream;
    private BufferedWriter outStream;
    private boolean leader;
    private int gameId;
    private GameHandler gameHandler;
    private boolean turn;
    private String tempMove = null;
    private String username;

    public ClientHandler(Server server, Socket clientSocket) {
        this.server = server;
        this.socket = clientSocket;

        try {
            this.inStream = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.outStream = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.start();
    }

    public void run () {
        try {
            server.log("Client detected...");

            String inbound;
            while ((inbound = inStream.readLine()) != null) {
                if (isProtocol(inbound)) {
                    handleProtocol(inbound);
                }
            }
        } catch (IOException e) {
            //stop game, make quit message
        }

    }

    public void talk(String message) {
        try {
            this.outStream.write(message+'\n');
            this.outStream.flush();
            System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean isProtocol(String message) {
        return message.matches(".*\\+.*");
    }
    public void handleProtocol(String message) {
        String[] command = message.split("\\+");
        switch (command[0]) {
            case "HANDSHAKE":
                if (username == null) {
                    talk(ResponseBuilder.acknowledgeHandshake(gameId, leader));
                    this.username = command[1];
                }
                break;
            case "SET_CONFIG":
                try {
                    gameHandler.setConfig(Integer.parseInt(command[2]), Integer.parseInt(command[3]));
                } catch (NumberFormatException e) {
                    talk("Config format not accepted, default settings used, you are black and boardsize is [7x7]");
                    gameHandler.setConfig(1,7);
                }
                break;
            case "MOVE":
                tempMove = "PLAY " + command[3];
                System.out.println(tempMove);
                break;
            case "PASS":
                tempMove = "PASS";
                break;
            case "EXIT":
                gameHandler.quit(this);
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("not in protocol" + message);

        }
    }

    public void requestConfig() {
        talk(ResponseBuilder.requestConfig());
    }

    public void acknowledgeConfig(int colour, int dimension, String gameState) {
        talk(ResponseBuilder.acknowledgeConfig(username, colour, dimension, gameState));
    }

    @Override
    public String playMove(Board board) {
        turn = true;
        talk(ResponseBuilder.updateStatus(gameHandler.gameState()));
        while (tempMove == null) {
            try {
                currentThread().sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            return tempMove;
        } finally {
            tempMove = null;
        }
    }




    @Override
    public void wrongMove() {
        talk(ResponseBuilder.wrongMove());
    }

    public String getUserName() {
        return this.username;
    }

    public void setGameHandler(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    public void setLeader() {
        this.leader = true;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
