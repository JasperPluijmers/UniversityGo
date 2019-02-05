package client.roboresources;

import go.model.Board;
import go.model.Group;
import go.utility.BoardUpdater;
import go.utility.Colour;
import go.utility.MoveValidator;
import go.utility.Score;

import java.util.*;

public class BoardStateValue2 {

    private List<Group> groups;
    private Board board;
    private Colour colour;
    private Map<Colour, Integer> freedoms;
    private Map<Colour, Double> score;
    private double value;

    private final double SCORE_CONSTANT = 4;
    private final double FREEDOM_CONSTANT = 1;

    public BoardStateValue2(Board board, Colour colour) {
        this.board = board;
        this.colour = colour;
        makeGroups();
        biggestFreedom();
        score = Score.score(board);
        value = calculateBoardValue();
    }

    public int bestMove() {
        int bestMove = -2;
        double bestMoveValue = -100;
        for (int i = 0; i < board.getDimension() * board.getDimension(); i++) {
            if (board.getEntry(i).equals(Colour.EMPTY) && MoveValidator.validateMove(i, colour, board) && !isEye(i)) {
                double currentMoveValue = pointDif(i);
                /*System.out.println("index: " + i + ", value: " + currentMoveValue);*/
                if (currentMoveValue > bestMoveValue) {
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
        if (newBoard.getEntry(index) != colour) {
            return -10000;
        }
        if (BoardUpdater.buildGroup(index,new Group(colour), newBoard).freedoms() == 1) {
            return -10000;
        }

        BoardStateValue2 newBoardStateValue = new BoardStateValue2(newBoard, colour);

        if (newBoardStateValue.score.get(colour) < score.get(colour)) {
            return -10000;
        }

        return new BoardStateValue2(newBoard, colour).getValue() - value;
    }

    public double getValue() {
        return value;
    }

    public double calculateBoardValue() {
        /*System.out.println("Scorepoints: " + scorePoints());
        System.out.println("freedomPoints: " + freedomPoints());*/
        return scorePoints() + freedomPoints();
    }

    private double freedomPoints() {
        return FREEDOM_CONSTANT * (1 + freedoms.get(colour)) / (1 + freedoms.get(otherColour()));
    }


    private Map<Colour, Integer> biggestFreedom() {
        freedoms = new HashMap<>();
        freedoms.put(Colour.BLACK, 0);
        freedoms.put(Colour.WHITE, 0);
        for (Group group : groups) {
            if (group.getColour() != Colour.EMPTY) {
                if (group.freedoms() > freedoms.get(group.getColour())) {
                    freedoms.put(group.getColour(), group.freedoms());
                }
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

    private static Set<Integer> extendedNeighbours(int index, Board board) {
        Set<Integer> neighbours = BoardUpdater.neighbours(index, board);

        if (index % board.getDimension() != 0 && board.isField(index - board.getDimension())) {
            neighbours.add(index - board.getDimension() - 1);
        }
        if (index % board.getDimension() != 0 && board.isField(index + board.getDimension())) {
            neighbours.add(index + board.getDimension() - 1);
        }
        if (index % board.getDimension() != board.getDimension() - 1 && board.isField(index - board.getDimension())) {
            neighbours.add(index - board.getDimension() + 1);
        }
        if (index % board.getDimension() != board.getDimension() - 1 && board.isField(index + board.getDimension())) {
            neighbours.add(index + board.getDimension() + 1);
        }
        return neighbours;
    }

    private boolean isEye(int index) {
        Object[] neighbours = extendedNeighbours(index, board).toArray();
        for (Object neighbour : neighbours) {
            if (board.getEntry((int)neighbour) == Colour.EMPTY) {
                return false;
            } else if (board.getEntry((int)neighbour) == otherColour()) {
                return false;
            }
        }
        return true;
    }

}
