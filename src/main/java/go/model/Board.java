package go.model;

import go.utility.Colour;

import java.util.*;

/**
 * Board class that represents a Go board
 */
public class Board {

    private int[] boardState;
    private int dimension;
    private ArrayList<int[]> history;

    /**
     * Constructs Board of a given size
     *
     * @param size Dimension of the length of the board, the board will contain size*size fields
     */
    public Board(int size) {
        this.boardState = new int[size * size];
        this.dimension = size;
        history = new ArrayList<>();
        updateHistory();
    }

    /**
     * Constructs a board with a certain boardstate and history
     *
     * @param boardState
     * @param history
     */
    public Board(int[] boardState, ArrayList<int[]> history) {
        this.boardState = boardState.clone();
        this.history = history;
        this.dimension = (int) Math.sqrt(this.boardState.length);
    }

    /**
     * Makes a deepcopy of the board
     *
     * @return new Board object that is a copy of the current Board
     */
    public Board deepCopy() {
        return new Board(this.boardState, this.history);
    }

    /**
     * Checks if a field is an EMPTY field
     * @param index
     * @return true if the field is EMPTY, false if the field is not EMPTY
     */
    public boolean isempty(int index) {
        return getEntry(index) == Colour.EMPTY;
    }

    /**
     * Updates the history of the board
     */
    public void updateHistory() {
        history.add(getBoardState());
    }

    /**
     * String representation that shows the board in a TUI
     * @return
     */
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

    /**
     * Creates a string representation that complies to the protocol
     * @return A string of length dimension * dimension containing 0, 1 and 2
     */
    public String stringRep() {
        return Arrays.toString(boardState).replace("[", "").replace("]", "").replace(" ", "").replace(",", "");
    }

    /**
     * updates the board from a string that complies to the protocol
     * @param stringRep
     */
    public void fromString(String stringRep) {
        for (int i = 0; i < boardState.length; i++) {
            setEntry(i, Colour.getByInt(Integer.parseInt(stringRep.split("")[i])));
        }
        updateHistory();
    }

    /**
     * Checks if field is a valid field in the board
     * @param index
     * @return
     */
    public boolean isField(int index) {
        return index >= 0 && index < dimension * dimension;
    }


    public int[] getBoardState() {
        return boardState.clone();
    }

    public int getDimension() {
        return dimension;
    }

    public Colour getEntry(int index) {
        return Colour.getByInt(boardState[index]);
    }

    public void setEntry(int index, Colour colour) {
        this.boardState[index] = colour.getValue();
    }

    public ArrayList<int[]> getHistory() {
        return this.history;
    }

}
