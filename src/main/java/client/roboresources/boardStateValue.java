package client.roboresources;

import go.model.Board;
import go.model.Group;
import go.utility.Colour;

import java.util.List;

public class boardStateValue {

    private List<Group> groups;
    private double score;
    private Board board;
    private Colour colour;

    public boardStateValue(Board board, Colour colour) {
        this.board = board;
        this.colour = colour;
    }

}
