package go.utility;

import go.model.Board;
import go.model.Group;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Score {

    public static Map<Colour, Double> score(Board board) {
        Set<Integer> checkedFields = new HashSet<>();
        HashMap<Colour, Double> scores = new HashMap<>();
        scores.put(Colour.BLACK, 0.0);
        scores.put(Colour.WHITE, 0.0);
        for (int i = 0; i < board.getDimension() * board.getDimension(); i++) {
            if (board.getEntry(i) == Colour.EMPTY && !checkedFields.contains(i)) {
                Group group = BoardUpdater.freedoms(i, new Group(Colour.EMPTY), board);
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
