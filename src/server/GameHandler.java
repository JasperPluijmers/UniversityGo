package server;


import go.controller.Game;
import go.utility.Player;
import go.utility.Status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GameHandler extends Thread {

    private ClientHandler playerOne;
    private ClientHandler playerTwo;
    private int gameId;
    private Status status;
    private Game game;
    private int dimension;

    private Map<ClientHandler, Integer> players;


    public GameHandler(int gameId) {
        this.gameId = gameId;
        this.players = new HashMap<>();
        this.status = Status.WAITING;
    }

    public void addPlayer(ClientHandler player) {
        if (players.size() == 0) {
            players.put(player,-1);
            playerOne = player;
            playerOne.setLeader();
            playerOne.setGameId(gameId);
            playerOne.requestConfig();
            this.playerOne.setGameHandler(this);
        } else if (players.size() == 1) {
            players.put(player,-1);
            playerTwo = player;
            playerTwo.setGameId(gameId);
            this.playerTwo.setGameHandler(this);
            if (dimension != 0) {
                setupSecondPlayer();
            }
        }
    }

    public void setConfig(int colour, int dimension) {
        players.put(playerOne,colour);
        this.dimension = dimension;
        playerOne.acknowledgeConfig(colour,dimension,gameState());
        if (players.size() == 2) {
            setupSecondPlayer();
        }
    }

    public void setupSecondPlayer() {
        playerTwo.setColour(players.get(playerOne) == 1 ? 0 : 1);
        playerTwo.acknowledgeConfig(players.get(playerOne) == 1 ? 0 : 1,dimension,gameState());
        players.put(playerTwo,playerTwo.getColour());
        status = Status.PLAYING;
        this.start();
    }

    public void run() {
        if (players.get(playerOne) == 1) {
            game = new Game(dimension, Arrays.asList(playerOne, playerTwo));
            game.play();
        } else {
            game = new Game(dimension, Arrays.asList(playerTwo,playerOne));
            game.play();
        }
    }


    public void quit(ClientHandler clientHandler) {

    }

    public String gameState() {
        if (status == Status.WAITING) {
            char[] repeat = new char[dimension*dimension];
            Arrays.fill(repeat,'0');
            return "WAITING;1;" + new String(repeat);
        } else {
            return status + ";" + game.getState().getCurrentColour() + ";" + game.getBoard().stringRep();
        }
    }

}
