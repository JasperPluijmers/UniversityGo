package go.utility;

import go.model.Board;

import java.util.Arrays;

/**
 * Helper class that has a single method that checks if a move is valid
 */
public class MoveValidator {

    /**
     * Checks if a certain move is valid.
     * @param move Index of the move that needs to be checked
     * @param colour Colour of the move being played
     * @param board The board on which the move is played
     * @return Returns true if a valid move, false if not a valid move
     */
    public static boolean validateMove(int move, Colour colour, Board board) {
        Board boardCopy = board.deepCopy();

        if (!boardCopy.isField(move)) {
            //System.out.println(String.format("%d is not within the bounds of the board", move));
            return false;
        }

        if (!boardCopy.isempty(move)) {
            //System.out.println(String.format("Field %d is already occupied by %s", move, board.getEntry(move)));
            return false;
        }
        boardCopy.setEntry(move, colour);
        BoardUpdater.updateBoard(move, boardCopy);

        for (int[] i : boardCopy.getHistory()) {
            if (Arrays.equals(i, boardCopy.getBoardState())) {
                //System.out.println("Ko, boardstate has been seen before");
                return false;
            }
        }
        return true;
    }
}
