package go.model;

import go.utility.Player;
import go.utility.Status;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    private Board board;
    private List<Player> players;
    public int currentPlayer;
    private Status status;
    private int turnNumber;
    private boolean passed;

    public GameState(Board board, List<Player> players) {
        this.players = players;
        this.board = board;
        this.currentPlayer = 0;
        this.status = Status.WAITING;
        this.turnNumber = 0;
        this.passed = false;
    }

    public boolean getPassed() {
        return this.passed;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status newStatus) {
        this.status = newStatus;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public void nextTurn() {
        this.turnNumber++;
    }

    public Player currentPlayer() {
        return players.get(currentPlayer);
    }

    public void updateCurrent() {
        currentPlayer = (currentPlayer + 1) % players.size();
    }

}
