package server;

import go.controller.Game;
import go.model.Board;
import go.utility.Colour;
import go.utility.Player;
import go.utility.Status;
import server.utilities.ProtocolHandler;
import server.utilities.ResponseBuilder;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class ClientHandler extends Thread implements Player {

    private Server server;
    private Socket socket;
    private BufferedReader inStream;
    private BufferedWriter outStream;
    public boolean leader;
    private int gameId;
    private GameHandler gameHandler;
    private boolean turn;
    private Game game;
    private String tempMove = null;
    private String username;
    private Colour colour;
    private ProtocolHandler protocolHandler;

    public ClientHandler(Server server, Socket clientSocket) {
        this.server = server;
        this.socket = clientSocket;

        try {
            this.inStream = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.outStream = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.protocolHandler = new ProtocolHandler(this);
        this.start();
    }

    public void run () {
        try {
            server.log("client detected...");

            String inbound;
            while ((inbound = inStream.readLine()) != null) {
                if (isProtocol(inbound)) {
                    protocolHandler.handleProtocol(inbound);
                } else {
                    talk(ResponseBuilder.unknownCommand("Command not recognized"));
                }
            }
        } catch (IOException e) {
            disconnect();
        }

    }

    public void talk(String message) {
        try {
            this.outStream.write(message+'\n');
            this.outStream.flush();
            System.out.println(message);
        } catch (IOException e) {
            disconnect();
        }

    }

    public void handleSetRematch(int value) {
        gameHandler.rematch(value, this);
    }
    private void disconnect() {
        gameHandler.quit(this);
    }

    public void handleWrongMove() {
        talk(ResponseBuilder.wrongMove());
    }

    public void handleMove(int move) {
        if (turn == true) {
            if (move == -1) {
                if (game.playMove("PASS", colour)) {
                    turn = false;
                }
            } else {
                if (game.playMove("PLAY " + move, colour)) {
                    turn = false;
                }
            }
        }
    }

    public void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void handleQuit() {
        gameHandler.quit(this);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void handleSetConfig(int colour, int boardSize) {
        gameHandler.setConfig(Colour.getByInt(colour),boardSize);
    }

    public void handleHandshake(String username) {
        if (this.username == null) {
            this.username = username;
            this.username = username;
            gameHandler.configPlayer(this);
        }
    }

    public void handleUnknownCommand(String message) {
        talk(ResponseBuilder.unknownCommand(message));
    }
    public boolean isProtocol(String message) {
        return message.matches(".*\\+.*");
    }

    public void requestConfig() {
        talk(ResponseBuilder.requestConfig());
    }

    public void acknowledgeConfig(int dimension, String gameState) {
        talk(ResponseBuilder.acknowledgeConfig(username, this.colour, dimension, gameState));
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
    public void setColour(Colour colour) {
        this.colour = colour;
    }


    //game finish due to quit or disconnect
    public void finishGame() {
        Map<Colour, Integer> finalScore = game.score();
        gameHandler.setStatus(Status.FINISHED);
        talk(ResponseBuilder.gameFinished(gameId, username,finalScore.get(Colour.BLACK)+ ";2;" + finalScore.get(Colour.WHITE),"Other player quit the game"));
    }


    //normal gamefinish
    @Override
    public void finishGame(String winner, Map<Colour, Integer> score, String reason) {
        talk(ResponseBuilder.gameFinished(gameId, winner, "1;" + score.get(Colour.BLACK) + ";2;" + score.get(Colour.WHITE), reason));
        talk(ResponseBuilder.requestRematch());
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void acknowledgeMove(int move, Colour colour) {
        talk(ResponseBuilder.acknowledgeMove(gameId, move, colour, gameHandler.gameState()));
    }

    public Colour getColour() {
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


    public void acknowledgeRematch(int value) {
        talk(ResponseBuilder.acknolwedgeRematch(value));
    }
}
