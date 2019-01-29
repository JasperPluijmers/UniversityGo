package go.utility;

import go.model.Board;

import java.util.Arrays;

public class MoveValidator {
    public static boolean validateMove(int move, Colour colour, Board board) {
        Board boardCopy = board.copy();

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
