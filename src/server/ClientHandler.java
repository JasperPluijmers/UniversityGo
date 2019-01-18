package server;

import go.controller.Game;
import go.model.Board;
import go.utility.Player;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class ClientHandler extends Thread implements Player {

    private Server server;
    private Socket socket;
    private BufferedReader inStream;
    private BufferedWriter outStream;
    private boolean leader;
    private int gameId;
    private GameHandler gameHandler;
    private boolean turn;
    private Game game;
    private String tempMove = null;
    private String username;
    private int colour;

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
                } else {
                    talk(ResponseBuilder.unknownCommand());
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
                    talk(ResponseBuilder.unknownCommand());
                    gameHandler.setConfig(1,7);
                }
                break;
            case "MOVE":
                    if (turn == true) {
                        if (command[3].equals("-1")) {
                            game.playMove("PASS", colour);
                        } else {
                            game.playMove("PLAY " + command[3], colour);
                        }
                        break;
                    } else {
                        talk("It is not your turn");
                    }
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
    public void wrongMove() {
        talk(ResponseBuilder.wrongMove());
    }

    @Override
    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public void requestMove(Board board) {
        turn = true;
    }

    @Override
    public void setColour(int colour) {
        this.colour = colour;
    }

    @Override
    public void updateState() {
        talk(ResponseBuilder.updateStatus(gameHandler.gameState()));
    }

    @Override
    public void finishGame(String winner, Map<Integer, Integer> score, String reason) {
        talk(ResponseBuilder.gameFinished(gameId,winner,"1:"+score.get(1)+":2:"+score.get(2),reason));
    }

    @Override
    public String getUsername() {
        return null;
    }

    public String getUserName() {
        return this.username;
    }

    public int getColour() {
        return this.colour;
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
