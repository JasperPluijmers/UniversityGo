package client.roboresources;

import go.model.Board;
import go.model.Group;
import go.utility.BoardUpdater;
import go.utility.Colour;
import go.utility.MoveValidator;
import go.utility.Score;

import java.util.*;

public class BoardStateValue {

    private List<Group> groups;
    private Board board;
    private Colour colour;
    private Map<Colour, Integer> freedoms;
    private Map<Colour, Double> score;
    private double value;

    private final double SCORE_CONSTANT = 1;
    private final double FREEDOM_CONSTANT = 1;

    public BoardStateValue(Board board, Colour colour) {
        this.board = board;
        this.colour = colour;
        makeGroups();
        totalFreedoms();
        score = Score.score(board);
        value = calculateBoardValue();
    }

    public int bestMove() {
        int bestMove = -2;
        double bestMoveValue = 0;
        for (int i = 0; i < board.getDimension() * board.getDimension(); i++) {
            if (board.getEntry(i).equals(Colour.EMPTY) && MoveValidator.validateMove(i, colour, board)) {
                double currentMoveValue = pointDif(i);
                if (currentMoveValue > bestMoveValue) {
                    System.out.println("index: " + i + ", value: " + currentMoveValue);
                    bestMove = i;
                    bestMoveValue = currentMoveValue;
                }
            }
        }
        return bestMove;
    }


    public boolean checkWin() {
        return score.get(colour) > score.get(otherColour());
    }

    private double pointDif(int index) {
        /*System.out.println("Index:" + index);*/
        Board newBoard = board.deepCopy();
        newBoard.setEntry(index, colour);
        BoardUpdater.updateBoard(index, newBoard);
        return new BoardStateValue(newBoard, colour).getValue() - value;
    }

    public double getValue() {
        return value;
    }

    public double calculateBoardValue() {
        System.out.println("Scorepoints: " + scorePoints());
        System.out.println("freedomPoints: " + freedomPoints());
        return scorePoints() * freedomPoints();
    }

    private double freedomPoints() {
        return FREEDOM_CONSTANT * (1 + freedoms.get(colour)) / (1 + freedoms.get(otherColour()));
    }

    private Map<Colour, Integer> totalFreedoms() {
        freedoms = new HashMap<>();
        freedoms.put(Colour.BLACK, 0);
        freedoms.put(Colour.WHITE, 0);
        for (Group group : groups) {
            if (group.getColour() != Colour.EMPTY) {
                freedoms.put(group.getColour(), group.freedoms());
            }
        }
        return freedoms;
    }

    private double scorePoints() {
        return SCORE_CONSTANT * (1 + score.get(colour)) / (1 + score.get(otherColour()));
    }

    private Colour otherColour() {
        return colour == Colour.BLACK ? Colour.WHITE : Colour.BLACK;
    }

    private void makeGroups() {
        groups = new ArrayList<>();
        Set<Integer> checkedFields = new HashSet<>();

        for (int i = 0; i < board.getDimension() * board.getDimension(); i++) {
            if (!checkedFields.contains(i)) {
                Group currentGroup = BoardUpdater.buildGroup(i, new Group(board.getEntry(i)), board);
                groups.add(currentGroup);
                checkedFields.addAll(currentGroup.getGroupMembers());
            }
        }
    }
}
