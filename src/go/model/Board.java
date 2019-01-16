package go.model;

import java.util.*;

public class Board {

    private int[] boardState;
    public int dimension;
    private ArrayList<int[]> history;

    public Board(int size) {
        this.boardState = new int[size * size];
        this.dimension = size;
        history = new ArrayList<>();
        updateHistory();
    }

    public Board(int[] boardState, ArrayList<int[]> history) {
        this.boardState = boardState.clone();
        this.history = history;
        this.dimension = (int)Math.sqrt(this.boardState.length);
    }

    public Board copy() {
        return new Board(this.boardState,this.history);
    }

    public boolean isempty(int index) {
        return getEntry(index) == 0;
    }

    public int getEntry(int index) {
        return boardState[index];
    }

    public void setEntry(int index, int colour) {
        this.boardState[index] = colour;
    }

    public ArrayList<int[]> getHistory() {
        return this.history;
    }

    public void updateHistory() {
        history.add(getBoardState());
    }
    public int[] getBoardState() {
        return boardState.clone();
    }
    public String toString() {
        String boardString = "";
        for (int i = 0; i < dimension * dimension; i++) {
            if (i == (dimension * dimension) - 1) {
                boardString += boardState[i];
            } else if (i % this.dimension == this.dimension - 1) {
                boardString += boardState[i] + "\n";
                for (int j = 0; j < dimension; j++) {
                    boardString += "| ";
                }
                boardString += "\n";
            } else {
                boardString += boardState[i] + "-";
            }
        }
        return boardString;
    }

    public boolean isField(int index) {
        return index >= 0 && index < dimension * dimension;
    }
}
