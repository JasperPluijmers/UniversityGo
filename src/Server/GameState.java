package Server;

import java.util.*;

public class GameState {

    private Board board;
    private int[] players = {1, 2};
    public int currentPlayer;
    private Status status;

    public GameState(Board board) {
        this.board = board;
        currentPlayer = 0;
        status = Status.WAITING;
    }

    public void updateCurrent() {
        currentPlayer = (currentPlayer + 1) % players.length;
    }

    public void updateBoard() {
        Map<Set<Integer>,Set<Integer>> freedomMap = new HashMap<>();
        for (int i = 0; i < board.dimension * board.dimension; i++) {
            if (board.getEntry(i) != 0) {
                List<Set<Integer>> freedomList = board.freedoms(i, new HashSet<Integer>(), new HashSet<Integer>());
                freedomMap.put(freedomList.get(0),freedomList.get(1));
            }
        }
        System.out.println(freedomMap);
    }

}
