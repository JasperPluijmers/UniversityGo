package go.utility;

import go.model.Board;
import go.model.Group;

import java.util.HashSet;
import java.util.Set;

public class BoardUpdater {

    public static void updateBoard(int move, Board board) {
        Set<Integer> checkedFields = new HashSet<>();
        for (int i = 0; i < board.dimension * board.dimension; i++) {
            if (!checkedFields.contains(i)) {
                Group currentGroup = new Group(board.getEntry(i));
                currentGroup = freedoms(i, currentGroup, board);
                checkedFields.addAll(currentGroup.getGroupMembers());
                if (currentGroup.freedoms() == 0 && !currentGroup.getGroupMembers().contains(move)) {
                    for (int groupMember : currentGroup.getGroupMembers()) {
                        board.setEntry(groupMember, 0);
                    }
                }
            }
        }
        Group currentGroup = new Group(board.getEntry(move));
        if (freedoms(move, currentGroup, board).freedoms() == 0) {
            for (int groupMember : currentGroup.getGroupMembers()) {
                board.setEntry(groupMember, 0);
            }
        }
    }

    public static Group freedoms(int index, Group group, Board board) {

        Set<Integer> neighbours = neighbours(index, board);
        group.addGroupMember(index);

        for (Integer i : neighbours) {
            //Check the status of the neighbour.
            if (board.getEntry(i) == board.getEntry(index) && !group.getGroupMembers().contains(i)) {
                group.addGroupMember(i);
                group = freedoms(i, group, board);
            } else if (!group.getNeighbours().get(board.getEntry(i)).contains(i)) {
                group.addNeighbour(i, board.getEntry(i));
            }
        }
        return group;
    }

    public static Set<Integer> neighbours(int index, Board board) {
        Set<Integer> neighbours = new HashSet<Integer>();
        //check if left column
        if (index % board.dimension == 0) {
            neighbours.add(index + 1);
            if (board.isField(index - board.dimension)) {
                neighbours.add(index - board.dimension);
            }
            if (board.isField(index + board.dimension)) {
                neighbours.add(index + board.dimension);
            }
        } else if (index % board.dimension == board.dimension - 1) {
            neighbours.add(index - 1);
            if (board.isField(index - board.dimension)) {
                neighbours.add(index - board.dimension);
            }
            if (board.isField(index + board.dimension)) {
                neighbours.add(index + board.dimension);
            }
        } else {
            neighbours.add(index - 1);
            neighbours.add(index + 1);

            if (board.isField(index - board.dimension)) {
                neighbours.add(index - board.dimension);
            }
            if (board.isField(index + board.dimension)) {
                neighbours.add(index + board.dimension);
            }
        }
        return neighbours;
    }
}