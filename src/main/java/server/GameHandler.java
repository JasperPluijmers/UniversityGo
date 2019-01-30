package server;


import go.controller.Game;
import go.utility.Colour;
import go.utility.Score;
import go.utility.Status;
import server.utilities.ResponseBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The GameHandler class sets up a game with two players. All communication between the two players goes through the GameHandler.
 * The first player added to the GameHandler is the leader and gets to set up the configuration. If the second player connects and the configuration
 * has been set the game begins. After the game there is an option for a rematch.
 */
public class GameHandler extends Thread {

    private ClientHandler leadingPlayer;
    private ClientHandler secondaryPlayer;
    private int gameId;
    private Status status;
    private Game game;
    private int dimension;

    private Map<ClientHandler, Colour> players;


    /**
     * Constructs a GameHandler object
     *
     * @param gameId given by the server
     */
    public GameHandler(int gameId) {
        this.gameId = gameId;
        this.players = new HashMap<>();
        this.status = Status.WAITING;
    }

    /**
     * Adds a player to this GameHandler
     *
     * @param player that needs to be added to gameHandler
     */
    public void addPlayer(ClientHandler player) {
        player.setGameHandler(this);
    }

    /**
     * Starts config for a player. If leading player the prefered configurations are asked otherwise configurations are set.
     *
     * @param player The Player that tries to set the config
     */
    public void configPlayer(ClientHandler player) {
        if (players.size() == 0) {
            players.put(player, Colour.EMPTY);
            leadingPlayer = player;
            leadingPlayer.setLeader();
            leadingPlayer.talk(ResponseBuilder.acknowledgeHandshake(gameId, leadingPlayer.getLeader()));

            leadingPlayer.setGameId(gameId);
            leadingPlayer.requestConfig();

        } else if (players.size() == 1) {
            players.put(player, Colour.EMPTY);
            secondaryPlayer = player;
            secondaryPlayer.setGameId(gameId);
            this.secondaryPlayer.setGameHandler(this);
            leadingPlayer.talk(ResponseBuilder.acknowledgeHandshake(gameId, leadingPlayer.getLeader()));

            if (dimension != 0) {
                setupSecondPlayer();
            }
        }
    }

    /**
     * Sets the config to be used for the construction of the Game object
     *
     * @param colour    Preferred colour of the leading player
     * @param dimension Preferred size of the length of the board
     */
    public void setConfig(Colour colour, int dimension) {
        players.put(leadingPlayer, colour);
        System.out.println("col:" + players.get(leadingPlayer));
        this.dimension = dimension;
        if (players.size() == 2) {
            setupSecondPlayer();
        }
    }

    /**
     * Setups the second player, then starts the game.
     */
    public void setupSecondPlayer() {
        secondaryPlayer.setColour(players.get(leadingPlayer) == Colour.BLACK ? Colour.WHITE : Colour.BLACK);
        players.put(secondaryPlayer, secondaryPlayer.getColour());
        status = Status.PLAYING;
        startNewGame();
    }

    /**
     * Starts a new game with the given configuration, then orders ClientHandlers to acknowledge the config and commences first turn of the game
     */
    public void startNewGame() {
        if (players.get(leadingPlayer) == Colour.BLACK) {
            game = new Game(dimension, Arrays.asList(leadingPlayer, secondaryPlayer));
        } else {
            game = new Game(dimension, Arrays.asList(secondaryPlayer, leadingPlayer));
        }

        for (ClientHandler player : players.keySet()) {
            player.acknowledgeConfig(this.dimension, gameState(), otherPlayer(player).getUsername());
        }
        game.play();
    }

    /**
     * Handles the sudden disconnection of a Player by notifying the other Player and finishing the game.
     *
     * @param clientHandler The client that disconnects
     */
    public void quit(ClientHandler clientHandler) {

        if (clientHandler.equals(leadingPlayer) && status == Status.PLAYING) {
            secondaryPlayer.unexpectedFinishGame();
        } else if (status == Status.PLAYING) {
            leadingPlayer.unexpectedFinishGame();
        }
        status = Status.FINISHED;
    }

    /**
     * Constructs and returns a string representation of the gamestate
     *
     * @return
     */
    public String gameState() {
        return status + ";" + game.getState().getCurrentColour() + ";" + game.getState().getBoard().stringRep();
    }

    /**
     * Handles the rematch functionality. If a player does not want a rematch, notifies the other player and disconnects everyone.
     * If a player wants a rematch, check if the other players also wants a rematch and flag itself as wanting a rematch.
     * If both players want a rematch start a new game with the same configuration.
     *
     * @param value  0 if player does not want a rematch, 1 if player wants a rematch
     * @param player player that notifies preference of rematching
     */
    public void rematch(int value, ClientHandler player) {
        System.out.println("value: " + value);
        switch (value) {
            case 0:
                player.closeSocket();
                otherPlayer(player).acknowledgeRematch(value);
                otherPlayer(player).closeSocket();
            case 1:
                if (!otherPlayer(player).getWantsRematch()) {
                    player.setWantsRematch(true);
                } else {
                    for (ClientHandler players : players.keySet()) {
                        players.acknowledgeRematch(value);
                        players.setWantsRematch(false);
                    }
                    startNewGame();
                }
        }
    }

    /**
     * Returns the other ClientHandler object in the game
     *
     * @param player not the other player?
     * @return
     */
    private ClientHandler otherPlayer(ClientHandler player) {
        if (player.equals(leadingPlayer)) {
            return secondaryPlayer;
        } else {
            return leadingPlayer;
        }
    }

    /**
     * Calculates score of the current board
     *
     * @return
     */
    public Map<Colour, Double> score() {
        return Score.score(game.getState().getBoard());
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
