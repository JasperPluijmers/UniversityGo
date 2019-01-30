package go.utility;

import go.controller.Game;
import go.model.Board;

import java.util.Map;

public interface Player {

    void wrongMove();

    void setGame(Game game);

    void requestMove(Board board);

    void setColour(Colour colour);

    void finishGame(String winner, Map<Colour, Double> score, String reason);

    String getUsername();

    void acknowledgeMove(int move, Colour colour);
}
