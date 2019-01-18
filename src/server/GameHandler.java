package server;

import com.sun.deploy.util.StringUtils;
import go.controller.Game;
import go.utility.Status;

import java.util.ArrayList;
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


    public GameHandler(ClientHandler playerOne, int gameId) {
        this.playerOne = playerOne;
        this.gameId = gameId;
        this.playerOne.setGameHandler(this);
        this.players = new HashMap<>();
    }

    public void setConfig(int colour, int dimension) {
        players.put(playerOne,colour);
        this.dimension = dimension;
        playerOne.acknowledgeConfig(colour,dimension,gameState());
        this.status = Status.PLAYING;
    }

    public void run () {
        this.status = Status.WAITING;
        while (status == Status.WAITING) {
            playerOne.talk("Welcome to the go server, you are the leading player, who are you?");
            playerOne.setLeader();
            playerOne.setGameId(gameId);
            while (playerOne.getUserName() == null) {
                try {
                    this.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            playerOne.requestConfig();
            while (playerTwo == null) {
                try {
                    this.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        while (playerTwo.getName() == null) {

        }

        if (players.get(playerOne) == 1) {
            game = new Game(dimension, Arrays.asList(playerOne, playerTwo));
            playerTwo.acknowledgeConfig(2,dimension,gameState());
            game.play();
        } else {
            game = new Game(dimension, Arrays.asList(playerTwo,playerOne));
            game.play();
        }

    }

    public void addSecondPlayer(ClientHandler playerTwo) {
        this.playerTwo = playerTwo;
        this.playerTwo.setGameHandler(this);
        playerTwo.setGameId(gameId);
        playerTwo.talk("Welcome to the go server, you are not the leading player, who are you?");
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
