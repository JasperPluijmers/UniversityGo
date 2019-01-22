package go.utility;

import go.controller.Game;
import go.model.Board;

import java.util.Map;

public interface Player {

    public void wrongMove();

    public void setGame(Game game);

    public void requestMove(Board board);

    public void setColour(int colour);

    public void updateState();

    public void finishGame(String winner, Map<Integer, Integer> score, String reason);

    public String getUsername();

    public void acknowledgeMove(int move, int colour);
}
