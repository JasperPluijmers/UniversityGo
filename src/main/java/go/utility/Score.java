package go.utility;

import go.model.Board;
import go.model.Group;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Helper class with single method used to calculate the score of a board
 */
public class Score {
    /**
     * This method is used to calculate the score of a board, it takes a Board object as parameter and first calculates the Group
     * of empty fields. If the empty Group does not have any neighbours of one colour, the other colour controls the Group and gets
     * the points.
     * If a stone is BLACK or WHITE the colour gets one added to the score. Afterwards a half point is added to white so no draws are possible.
     *
     * @param board Board that the score needs to be calculated for
     * @return a Map that maps Colour.BLACK and Colour.WHITE to their respective score in Double format.
     */
    public static Map<Colour, Double> score(Board board) {
        //Initiate sets and maps
        Set<Integer> checkedFields = new HashSet<>();
        HashMap<Colour, Double> scores = new HashMap<>();
        scores.put(Colour.BLACK, 0.0);
        scores.put(Colour.WHITE, 0.0);

        for (int i = 0; i < board.getDimension() * board.getDimension(); i++) {

            if (board.getEntry(i) == Colour.EMPTY && !checkedFields.contains(i)) {
                Group group = BoardUpdater.buildGroup(i, new Group(Colour.EMPTY), board);
                checkedFields.addAll(group.getGroupMembers());

                if (group.getNeighbours().get(Colour.WHITE).size() == 0) {
                    scores.put(Colour.BLACK, scores.get(Colour.BLACK) + group.getGroupMembers().size());
                }

                if (group.getNeighbours().get(Colour.BLACK).size() == 0) {
                    scores.put(Colour.WHITE, scores.get(Colour.WHITE) + group.getGroupMembers().size());
                }
            }
            if (board.getEntry(i) == Colour.BLACK) {
                scores.put(Colour.BLACK, scores.get(Colour.BLACK) + 1);
            }
            if (board.getEntry(i) == Colour.WHITE) {
                scores.put(Colour.WHITE, scores.get(Colour.WHITE) + 1);
            }


        }
        scores.put(Colour.WHITE, scores.get(Colour.WHITE) + 0.5);
        return scores;
    }
}
