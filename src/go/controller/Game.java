package go.controller;

import go.model.Group;
import go.utility.BoardUpdater;
import go.utility.MoveValidator;
import go.utility.Player;
import go.model.Board;
import go.model.GameState;
import go.utility.Status;

import java.util.*;

public class Game {

    private GameState state;
    private Board board;
    private int dimension;

    public Game(int dimension, List<Player> players) {
        this.dimension = dimension;
        this.board = new Board(dimension);
        this.state  = new GameState(board, players);
        for ( int i = 0; i < players.size(); i++) {
            players.get(i).setGame(this);
            players.get(i).setColour(i+1);
        }
    }

    public GameState getState() {
        return this.state;
    }

    public void play() {
        state.setStatus(Status.PLAYING);
        nextTurn();
        /*state.setStatus(Status.PLAYING);
        while (state.getStatus() == Status.PLAYING) {
            System.out.println(board.stringRep());
            String move = state.currentPlayer().playMove(board);
            System.out.println(move);
            this.playMove(move, state.currentPlayer + 1);
        }
        System.out.println(score());*/
    }

    public void nextTurn() {
        switch (state.getStatus()) {
            case PLAYING:
                for (Player player : state.getPlayers()) {
                    player.updateState();
                }
                state.currentPlayer().requestMove(board);
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

    public void playMove(String move, int colour) {
        if (move.equals("PASS")) {
            if (state.getPassed() == true) {
                state.setStatus(Status.FINISHED);
            } else {
                state.setPassed(true);
            }
            state.updateCurrent();
        } else if (move.matches("(PLAY )\\d*")) {
            int moveNumber = Integer.parseInt(move.split(" ")[1]);

            if(MoveValidator.validateMove(moveNumber, colour, board)) {
                board.setEntry(moveNumber, colour);
                board.updateHistory();

                BoardUpdater.updateBoard(moveNumber, board);

                state.updateCurrent();
                state.setPassed(false);
            } else {
                state.currentPlayer().wrongMove();
            }
        } else {
            System.out.println("Command not recognized");
        }
        nextTurn();
    }

    public Map<Integer, Integer> score() {
        Set<Integer> checkedFields = new HashSet<>();
        HashMap<Integer, Integer> scores = new HashMap<>();
        scores.put(1, 0);
        scores.put(2, 0);
        for (int i = 0; i < dimension * dimension; i++) {
            if (board.getEntry(i) == 0 && !checkedFields.contains(i)) {
                Group group = BoardUpdater.freedoms(i, new Group(0), board);
                checkedFields.addAll(group.getGroupMembers());
                if (group.getNeighbours().get(1).size() == 0) {
                    scores.put(2, scores.get(2) + group.getGroupMembers().size());
                }
                if (group.getNeighbours().get(2).size() == 0) {
                    scores.put(1, scores.get(1) + group.getGroupMembers().size());
                }
            }
            if (board.getEntry(i) == 1) {
                scores.put(1, scores.get(1) + 1);
            }
            if (board.getEntry(i) == 2) {
                scores.put(2, scores.get(2) + 1);
            }
        }
        return scores;
    }

    public Board getBoard() {
        return this.board;
    }


}
