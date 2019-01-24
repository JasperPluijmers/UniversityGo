package server;


import go.controller.Game;
import go.utility.Colour;
import go.utility.Status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GameHandler extends Thread {

    private ClientHandler leadingPlayer;
    private ClientHandler secondaryPlayer;
    private int gameId;
    private Status status;
    private Game game;
    private int dimension;

    private Map<ClientHandler, Colour> players;


    public GameHandler(int gameId) {
        this.gameId = gameId;
        this.players = new HashMap<>();
        this.status = Status.WAITING;
    }

    public void addPlayer(ClientHandler player) {
        player.setGameHandler(this);
    }

    public void configPlayer(ClientHandler player) {
        if (players.size() == 0) {
            players.put(player,Colour.EMPTY);
            leadingPlayer = player;
            leadingPlayer.setLeader();
            leadingPlayer.setGameId(gameId);
            leadingPlayer.requestConfig();

        } else if (players.size() == 1) {
            players.put(player,Colour.EMPTY);
            secondaryPlayer = player;
            secondaryPlayer.setGameId(gameId);
            this.secondaryPlayer.setGameHandler(this);

            if (dimension != 0) {
                setupSecondPlayer();
            }
        }
    }

    public void setConfig(Colour colour, int dimension) {
        players.put(leadingPlayer,colour);
        System.out.println("col:"+players.get(leadingPlayer));
        this.dimension = dimension;
        if (players.size() == 2) {
            setupSecondPlayer();
        }
    }

    public void setupSecondPlayer() {
        secondaryPlayer.setColour(players.get(leadingPlayer) == Colour.BLACK ? Colour.WHITE : Colour.BLACK);
        players.put(secondaryPlayer, secondaryPlayer.getColour());
        status = Status.PLAYING;
        if (players.get(leadingPlayer) == Colour.BLACK) {
            game = new Game(dimension, Arrays.asList(leadingPlayer, secondaryPlayer));
        } else {
            game = new Game(dimension, Arrays.asList(secondaryPlayer, leadingPlayer));
        }

        for (ClientHandler player : players.keySet()) {
            player.acknowledgeConfig(this.dimension, gameState());
        }
        this.start();
    }

    public void run() {
        game.play();
    }


    public void quit(ClientHandler clientHandler) {

        if (clientHandler.equals(leadingPlayer) && status == Status.PLAYING) {
            secondaryPlayer.finishGame();
        } else if (status == Status.PLAYING){
            leadingPlayer.finishGame();
        }
        status = Status.FINISHED;
    }

    public String gameState() {
        return status + ";" + game.getState().getCurrentColour() + ";" + game.getState().getBoard().stringRep();
    }

}
