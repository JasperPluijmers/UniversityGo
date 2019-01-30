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

/**
 * The ClientHandler class deals with all communication
 * between a client and the server. On detecting an input the input is
 * ran through the ProtocolHandler which routes commands back to the ClientHandler.
 */
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
    private String username;
    private Colour colour;
    private ProtocolHandler protocolHandler;
    private boolean wantsRematch;

    /**
     * Constructs a ClientHandler with a given Server and Socket.
     * Opens a communication channels to the socket and
     * starts this communication by starting a new thread on this object.
     *
     * @param server Server object
     * @param clientSocket Socket
     */
    public ClientHandler(Server server, Socket clientSocket) {
        this.server = server;
        this.socket = clientSocket;

        try {
            this.inStream = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.outStream = new BufferedWriter(
                    new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.protocolHandler = new ProtocolHandler(this);
        this.start();
    }

    /**
     * Method that runs in a thread, reads incoming messages. Checks if they correspond to the
     * protocol and routes it to the ProtocolHandler
     * or sends an unknown command if not part of the protocol.
     */
    public void run() {
        try {
            server.log("client detected...");

            String inbound;
            while ((inbound = inStream.readLine()) != null) {
                server.log(gameId + "-recieved: " + inbound);
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

    /**
     * Checks if a String is part of the protocol
     * by checking if it corresponds to the regex pattern.
     *
     * @param message String to be checked
     * @return true if corresponds to protocol format
     */
    private boolean isProtocol(String message) {
        return message.matches(".*\\+.*");
    }

    /**
     * Sends a String over the Socket.
     *
     * @param message String sent over the Socket
     */
    public void talk(String message) {
        try {
            this.outStream.write(message + '\n');
            this.outStream.flush();
            server.log(gameId + "-sent: " + message);
        } catch (IOException e) {
            disconnect();
        }

    }

    /**
     * Closes the socket connection
     */
    public void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Communicates a disconnection of the socket with the GameHandler.
     */
    private void disconnect() {
        gameHandler.quit(this);
    }

    /**
     * Communicates if the client wants a rematch to the GameHandler.
     *
     * @param value 1 if client wants a rematch, 0 if he does not.
     */
    public void handleSetRematch(int value) {
        gameHandler.rematch(value, this);
    }

    /**
     * Sends wrong move command to the client.
     */
    public void handleWrongMove() {
        talk(ResponseBuilder.wrongMove());
    }

    /**
     * Plays a move in the Game.
     *
     * @param move can either be -1 for pass or a valid index for the board
     */
    public void handleMove(int move) {
        if (turn) {
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

    /**
     * Communicates a quit to the GameHandler, then tries to close the socket connection.
     */
    public void handleQuit() {
        gameHandler.quit(this);
        closeSocket();
    }

    /**
     * Communicates config preferences to the GameHandler.
     *
     * @param colour    preferred colour of the client
     * @param boardSize preferred boardsize of the client
     */
    public void handleSetConfig(int colour, int boardSize) {
        gameHandler.setConfig(Colour.getByInt(colour), boardSize);
    }

    /**
     * Sets username and communicates handshake to the GameHandler.
     *
     * @param username preferred username of the client
     */
    public void handleHandshake(String username) {
        if (this.username == null) {
            this.username = username;
            gameHandler.configPlayer(this);
        }
    }

    /**
     * Sends unknown command command over the socket.
     *
     * @param message Error message to be sent with the unknown command command
     */
    public void handleUnknownCommand(String message) {
        talk(ResponseBuilder.unknownCommand(message));
    }


    /**
     * Sends a request for config over the Socket.
     */
    public void requestConfig() {
        talk(ResponseBuilder.requestConfig());
    }

    /**
     * Sends acknowledgement of the config over the socket.
     *
     * @param dimension Size of length of the board the game is going to be played on
     * @param gameState Current gamestate
     * @param opponent  Username of the copponent
     */
    public void acknowledgeConfig(int dimension, String gameState, String opponent) {
        talk(ResponseBuilder.acknowledgeConfig(username, this.colour, dimension, gameState, opponent));
    }

    /**
     * Sends wrongmove command over the socket.
     */
    @Override
    public void wrongMove() {
        talk(ResponseBuilder.wrongMove());
    }

    /**
     * Sets acknowledgement of a move over the Socket.
     *
     * @param move   Move that has been played, can be -1 for pass or a valid index on the board
     * @param colour Colour of the move that has been played, can be 1 for black or 2 for white
     */
    @Override
    public void acknowledgeMove(int move, Colour colour) {
        talk(ResponseBuilder.acknowledgeMove(gameId, move, colour, gameHandler.gameState()));
    }

    /**
     * Calculate and sends score to the client when game
     * is finished due to a disconnection or a forfeit.
     */
    public void unexpectedFinishGame() {
        Map<Colour, Double> finalScore = gameHandler.score();
        gameHandler.setStatus(Status.FINISHED);
        talk(ResponseBuilder.gameFinished(
                gameId, username, finalScore.get(Colour.BLACK)
                + ";" + finalScore.get(Colour.WHITE), "Other player quit the game"));
    }

    /**
     * Sends score to the client when a game is finished due to two passes.
     *
     * @param winner Name of the winner
     * @param score Map of the score
     * @param reason Reason the game finished
     */
    @Override
    public void finishGame(String winner, Map<Colour, Double> score, String reason) {
        talk(ResponseBuilder.gameFinished(gameId, winner, score.get(Colour.BLACK) + ";" + score.get(Colour.WHITE), reason));
        talk(ResponseBuilder.requestRematch());
    }

    /**
     * Sets flag that corresponds to if the client is able to make a move.
     *
     * @param board Current Board of the game
     */
    @Override
    public void requestMove(Board board) {
        turn = true;
    }

    /**
     * Sends Acknowledgement of a rematch command.
     *
     * @param value 1 if a rematch is going to be played, 0 if no rematch is going to be played.
     */
    public void acknowledgeRematch(int value) {
        talk(ResponseBuilder.acknolwedgeRematch(value));
    }

    @Override
    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public void setColour(Colour colour) {
        this.colour = colour;
    }


    @Override
    public String getUsername() {
        return this.username;
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


    public boolean getWantsRematch() {
        return wantsRematch;
    }

    public void setWantsRematch(boolean wantsRematch) {
        this.wantsRematch = wantsRematch;
    }

    public boolean getLeader() {
        return leader;
    }
}
