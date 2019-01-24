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
        this.state  = new GameState(new Board(dimension), players);
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
        for (Player player : state.getPlayers()) {
            player.updateState();
        }
        nextTurn();
    }

    public void nextTurn() {
        switch (state.getStatus()) {
            case PLAYING:
                state.currentPlayer().requestMove(state.getBoard());
                break;
            case FINISHED:
                Map<Integer, Integer> finalScore = score();
                int winner = finalScore.get(1) > finalScore.get(2) ? 1 : 2;
                for (Player player : state.getPlayers()) {
                    player.finishGame(state.getPlayers().get(winner - 1).getUsername(),finalScore,"Two passes");
                }
                break;
        }
    }

    public void acknowledgeMove(int move, Colour colour) {
        for (Player player : state.getPlayers()) {
            player.acknowledgeMove(move,colour);
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

            if(MoveValidator.validateMove(moveNumber, colour, state.getBoard())) {
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

    public Map<Integer, Integer> score() {
        Set<Integer> checkedFields = new HashSet<>();
        HashMap<Integer, Integer> scores = new HashMap<>();
        scores.put(1, 0);
        scores.put(2, 0);
        for (int i = 0; i < dimension * dimension; i++) {
            if (state.getBoard().getEntry(i) == Colour.BLACK && !checkedFields.contains(i)) {
                Group group = BoardUpdater.freedoms(i, new Group(Colour.EMPTY), state.getBoard());
                checkedFields.addAll(group.getGroupMembers());
                if (group.getNeighbours().get(1).size() == 0) {
                    scores.put(2, scores.get(2) + group.getGroupMembers().size());
                }
                if (group.getNeighbours().get(2).size() == 0) {
                    scores.put(1, scores.get(1) + group.getGroupMembers().size());
                }
            }
            if (state.getBoard().getEntry(i) == Colour.BLACK) {
                scores.put(1, scores.get(1) + 1);
            }
            if (state.getBoard().getEntry(i) == Colour.WHITE) {
                scores.put(2, scores.get(2) + 1);
            }
        }
        return scores;
    }

}
