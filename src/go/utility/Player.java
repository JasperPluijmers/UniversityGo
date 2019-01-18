package go.utility;

import go.model.Board;

public interface Player {

    public String playMove(Board board);

    public void wrongMove();
}
