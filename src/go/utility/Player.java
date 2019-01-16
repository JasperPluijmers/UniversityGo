package go.utility;

import go.model.Board;

public interface Player {
    public String playMove();

    public String playMove(Board board);
}
