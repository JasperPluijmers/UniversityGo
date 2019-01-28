package server;


import go.controller.Game;
import go.utility.Colour;
import go.utility.Status;
import server.utilities.ResponseBuilder;

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

    private int rematchCount;

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
            leadingPlayer.talk(ResponseBuilder.acknowledgeHandshake(gameId, leadingPlayer.leader));

            leadingPlayer.setGameId(gameId);
            leadingPlayer.requestConfig();

        } else if (players.size() == 1) {
            players.put(player,Colour.EMPTY);
            secondaryPlayer = player;
            secondaryPlayer.setGameId(gameId);
            this.secondaryPlayer.setGameHandler(this);
            leadingPlayer.talk(ResponseBuilder.acknowledgeHandshake(gameId, leadingPlayer.leader));

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
        startNewGame();
        game.play();
    }

    public void startNewGame() {
        if (players.get(leadingPlayer) == Colour.BLACK) {
            game = new Game(dimension, Arrays.asList(leadingPlayer, secondaryPlayer));
        } else {
            game = new Game(dimension, Arrays.asList(secondaryPlayer, leadingPlayer));
        }

        for (ClientHandler player : players.keySet()) {
            player.acknowledgeConfig(this.dimension, gameState());
        }
    }

    public void run() {
        game.play();
    }


    public void quit(ClientHandler clientHandler) {

        if (clientHandler.equals(leadingPlayer) && status == Status.PLAYING) {
            secondaryPlayer.finishGame();
        } else if (status == Status.PLAYING) {
            leadingPlayer.finishGame();
        }
        status = Status.FINISHED;
    }

    public String gameState() {
        return status + ";" + game.getState().getCurrentColour() + ";" + game.getState().getBoard().stringRep();
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void rematch(int value, ClientHandler player) {
        System.out.println("value: " + value);
        switch (value) {
            case 0:
                player.closeSocket();
                otherPlayer(player).acknowledgeRematch(value);
                otherPlayer(player).closeSocket();
            case 1:
                if (rematchCount != 1) {
                    rematchCount = 1;
                } else {
                    rematchCount = 0;
                    startNewGame();
                    for (ClientHandler players : players.keySet()) {
                        players.acknowledgeRematch(value);
                        players.acknowledgeConfig(this.dimension, gameState());
                    }
                    game.play();
                }
        }
    }

    private ClientHandler otherPlayer(ClientHandler player) {
        if (player.equals(leadingPlayer)) {
            return secondaryPlayer;
        } else {
            return leadingPlayer;
        }
    }

}
