package go.controller;

import go.model.Group;
import go.utility.*;
import go.model.Board;
import go.model.GameState;

import java.util.*;

public class Game {

    private GameState state;
    private int dimension;

    public Game(int dimension, List<Player> players) {
        this.dimension = dimension;
        this.state = new GameState(new Board(dimension), players);
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setGame(this);
            players.get(i).setColour(Colour.getByInt(i + 1));
        }
    }

    public GameState getState() {
        return this.state;
    }

    public void play() {
        state.setStatus(Status.PLAYING);
        nextTurn();
    }

    public void nextTurn() {
        switch (state.getStatus()) {
            case PLAYING:
                state.currentPlayer().requestMove(state.getBoard());
                break;
            case FINISHED:
                Map<Colour, Double> finalScore = Score.score(state.getBoard());
                int winner = finalScore.get(Colour.BLACK) > finalScore.get(Colour.WHITE) ? 1 : 2;
                for (Player player : state.getPlayers()) {
                    player.finishGame(state.getPlayers().get(winner - 1).getUsername(), finalScore, "Two passes");
                }
                break;
        }
    }

    public void acknowledgeMove(int move, Colour colour) {
        for (Player player : state.getPlayers()) {
            player.acknowledgeMove(move, colour);
        }
    }

    public boolean playMove(String move, Colour colour) {
        if (move.equals("PASS")) {
            if (state.getPassed() == true) {
                state.setStatus(Status.FINISHED);
            } else {
                state.setPassed(true);
            }
            state.nextPlayer();
            acknowledgeMove(-1, colour);
            nextTurn();
            return true;
        } else if (move.matches("(PLAY )\\d*")) {
            int moveNumber = Integer.parseInt(move.split(" ")[1]);

            if (MoveValidator.validateMove(moveNumber, colour, state.getBoard())) {
                state.getBoard().setEntry(moveNumber, colour);
                state.getBoard().updateHistory();

                BoardUpdater.updateBoard(moveNumber, state.getBoard());

                state.nextPlayer();
                state.setPassed(false);

                acknowledgeMove(moveNumber, colour);
                nextTurn();
                return true;
            } else {
                state.currentPlayer().wrongMove();
            }
        } else {
            System.out.println("Command not recognized");
        }
        return false;
    }


}
