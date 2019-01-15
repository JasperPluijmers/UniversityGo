package Server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Board {

    private int[] boardState;
    public int dimension;

    public Board(int size) {
        this.boardState = new int[size * size];
        this.dimension = size;
    }

    public boolean isempty(int index) {
        return this.boardState[index] == 0;
    }

    public int getEntry(int index) {
        return boardState[index];
    }

    public void setEntry(int index, int colour) {
        this.boardState[index] = colour;
    }

    public boolean playMove(int index, int colour) {
        if (isempty(index)) {
            boardState[index] = colour;
            return true;
        } else {
            System.out.println(String.format("Field %d is already occupied by %d",index,boardState[index]));
            return false;
        }
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
                boardString +=  boardState[i] + "-";
            }
        }
        return boardString;
    }

    public List<Set<Integer>> freedoms(int index, Set<Integer> teamStones, Set<Integer> freedoms) {

        Set<Integer> neighbours = neighbours(index);
        List<Set<Integer>> returnValues = new ArrayList<>();
        List<Set<Integer>>  newValues;
        teamStones.add(index);

        for (Integer i: neighbours){

            if (isField(i)) {
                //Check the status of the neighbour.
                if (boardState[i] == 0) {
                    //If the status of a neighbour is 0, add a freedom.
                    freedoms.add(i);
                } else if (boardState[index] == boardState[i]) {
                    //if the status of a neighbour is the same try again with that stone
                    if (!teamStones.contains(i)) {
                        newValues = freedoms(i, teamStones, freedoms);
                        teamStones.addAll(newValues.get(0));
                        freedoms.addAll(newValues.get(1));
                    }
                }
            }
        }
        returnValues.add(teamStones);
        returnValues.add(freedoms);
        return returnValues;
    }




    public boolean isField(int index) {
        return index >= 0 && index < dimension * dimension;
    }

    public static void main(String[] args) {
        Board board = new Board(7);
        board.playMove(2,1);
        board.playMove(3,1);
        board.playMove(3,2);
        System.out.println(board.toString());
        board.playMove(17,2);
        System.out.println(board.toString());
    }

    public Set<Integer> neighbours(int index) {
        Set<Integer> neighbours = new HashSet<Integer>();
        //check if left column
        if (index % dimension == 0) {
            neighbours.add(index + 1 );
            if (isField(index - dimension)) {
                neighbours.add(index - dimension);
            }
            if (isField(index + dimension)) {
                neighbours.add(index + dimension);
            }
        } else if (index % dimension == dimension - 1) {
            neighbours.add(index - 1);
            if (isField(index - dimension)) {
                neighbours.add(index - dimension);
            }
            if (isField(index + dimension)) {
                neighbours.add(index + dimension);
            }
        } else {
            neighbours.add(index - 1);
            neighbours.add(index + 1);

            if (isField(index - dimension)) {
                neighbours.add(index - dimension);
            }
            if (isField(index + dimension)) {
                neighbours.add(index + dimension);
            }
        }
        return neighbours;
    }

}
