package go.utility;

import go.model.Board;
import go.model.Group;

import java.util.HashSet;
import java.util.Set;

/**
 * Class made for updating a board when a move is made.
 */
public class BoardUpdater {

    /**
     * Updates a board that has just been played a move on, does not play or check validity of the move!
     * Method makes all Groups that are not EMPTY, if they have no freedoms they get removed.
     * The group containing the last played move is checked only at the end as Go prioritizes capturing other groups
     * before being captured.
     *
     * @param move  Move that needs to be checked
     * @param board Board that the move is played on
     */
    public static void updateBoard(int move, Board board) {
        //Creates checkedfield so no double checking
        Set<Integer> checkedFields = new HashSet<>();

        //loop over all fields
        for (int i = 0; i < board.getDimension() * board.getDimension(); i++) {
            //Only check non-checked fields
            if (!checkedFields.contains(i)) {

                //Create empty group, then build the corresponding group
                Group currentGroup = new Group(board.getEntry(i));
                currentGroup = buildGroup(i, currentGroup, board);
                //Add all fields of the group to the checked fields
                checkedFields.addAll(currentGroup.getGroupMembers());

                //Check for freedoms of the group, if they are 0
                // and group does not contain the last move,
                // remove the group from the board
                if (currentGroup.freedoms() == 0 && !currentGroup.getGroupMembers().contains(move)) {
                    for (int groupMember : currentGroup.getGroupMembers()) {
                        board.setEntry(groupMember, Colour.EMPTY);
                    }

                }
            }
        }
        //Now check for the group with the last move.
        Group currentGroup = new Group(board.getEntry(move));

        if (buildGroup(move, currentGroup, board).freedoms() == 0) {
            for (int groupMember : currentGroup.getGroupMembers()) {
                board.setEntry(groupMember, Colour.EMPTY);
            }
        }

    }

    /**
     * Takes an index, a Group object and a Board to fill the Group
     * object with its neighbours and groupmembers by checking
     * the neighbours of the index and either adding them to the
     * neighbours of the Group or adding them as groupmembers. If added
     * as groupmember the method is called again on that index.
     *
     * @param index Index that is contained in the group
     * @param group Group object that is being built, initially it can be an empty Group
     * @param board Board that the group is calculated on
     * @return Filled Group object with all groupmembers and neighbours of that group
     */
    public static Group buildGroup(int index, Group group, Board board) {
        //Neighbours of the current index
        Set<Integer> neighbours = neighbours(index, board);
        //Add the current index to the group
        group.addGroupMember(index);

        //Loop over the neighbours of the current index
        for (Integer i : neighbours) {
            //Check the status of the neighbour.,
            // if it is the same colour and is not yet contained in the groupmembers
            if (board.getEntry(i) == board.getEntry(index)
                    && !group.getGroupMembers().contains(i)) {
                //Add the neighbour to the group members
                group.addGroupMember(i);
                //Restart this method on the neighbout
                group = buildGroup(i, group, board);
                //If the neighbour of the current index is not of the same colour
            } else if (!group.getNeighbours().get(board.getEntry(i)).contains(i)) {
                //Add it to the neighbours of the group
                group.addNeighbour(i, board.getEntry(i));
            }
        }
        return group;
    }

    /**
     * Calculates the neighbours of an index.
     *
     * @param index The index that needs its neighbours checked
     * @param board The board on which we are playing
     * @return A set of Integer which are the neighbour of the index
     */

    private static Set<Integer> neighbours(int index, Board board) {
        Set<Integer> neighbours = new HashSet<>();

        if (index % board.getDimension() != 0) {
            neighbours.add(index - 1);
        }
        if (index % board.getDimension() != board.getDimension() - 1) {
            neighbours.add(index + 1);
        }
        if (board.isField(index - board.getDimension())) {
            neighbours.add(index - board.getDimension());
        }
        if (board.isField(index + board.getDimension())) {
            neighbours.add(index + board.getDimension());
        }
        return neighbours;
    }

/*    public static Set<Integer> extendedNeighbours(int index, Board board) {
        Set<Integer> neighbours = new HashSet<>();

        if (index % board.getDimension() != 0) {
            neighbours.add(index - 1);
        }
        if (index % board.getDimension() != board.getDimension() - 1) {
            neighbours.add(index + 1);
        }
        if (board.isField(index - board.getDimension())) {
            neighbours.add(index - board.getDimension());

            neighbours.add(index - board.getDimension() - 1);
            neighbours.add(index - board.getDimension() + 1);
        }
        if (board.isField(index + board.getDimension())) {
            neighbours.add(index + board.getDimension());
            neighbours.add(index + board.getDimension() - 1);
            neighbours.add(index + board.getDimension() + 1);
        }
        return neighbours;
    }

    public static void main(String[] args) {
        System.out.println(extendedNeighbours(0, new Board(4)));
        System.out.println(extendedNeighbours(1, new Board(4)));
        System.out.println(extendedNeighbours(5, new Board(4)));
        System.out.println(extendedNeighbours(12, new Board(4)));
        System.out.println(extendedNeighbours(13, new Board(4)));
        System.out.println(extendedNeighbours(15, new Board(4)));
    }*/

}

