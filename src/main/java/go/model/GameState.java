package go.model;

import go.utility.Player;
import go.utility.Status;

import java.util.List;

/**
 * State of a game of Go that knows a board, the players, whose turn it is and if the last move was a pass.
 * At any moment this object has to complete state of a game of Go.
 */
public class GameState {

    private Board board;
    private List<Player> players;
    private int currentPlayer;
    private Status status;
    private boolean passed;

    public GameState(Board board, List<Player> players) {
        this.players = players;
        this.board = board;
        this.currentPlayer = 0;
        this.status = Status.WAITING;
        this.passed = false;
    }

    public Board getBoard() {
        return this.board;
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

    public Player currentPlayer() {
        return players.get(currentPlayer);
    }

    public void nextPlayer() {
        currentPlayer = (currentPlayer + 1) % players.size();
    }

    public int getCurrentColour() {
        return currentPlayer + 1;
    }

    public List<Player> getPlayers() {
        return players;
    }

}
