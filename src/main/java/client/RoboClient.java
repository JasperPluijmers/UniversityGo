package client;

import client.roboresources.BoardStateValue;
import client.utilities.ResponseBuilder;
import go.model.Board;
import go.utility.MoveValidator;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoboClient extends Client {

    public RoboClient(String name, InetAddress host, int port, boolean hasGui) {
        super(name, host, port, hasGui);
    }

    @Override
    public void askMove() {
/*        try {
            this.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        determineMove();
    }

    public void determineMove() {
        BoardStateValue stateValue = new BoardStateValue(super.getBoard(), super.getColour());
        if (getLastMove() == -1 && stateValue.checkWin()) {
            talk(ResponseBuilder.move(super.getGameId(), super.getName(), "-1"));
            return;
        }
        int bestMove = stateValue.bestMove();
        if (bestMove == -2) {
            talk(ResponseBuilder.move(super.getGameId(), super.getName(), "" + randomMove()));
            return;
        } else {
            talk(ResponseBuilder.move(super.getGameId(), super.getName(), "" + bestMove));
            return;
        }
    }

    public int randomMove() {
        List<Integer> validMoves = new ArrayList<>();
        Board board = super.getBoard();
        for (int i = 0; i < board.getDimension() * board.getDimension(); i++) {
            if (MoveValidator.validateMove(i, super.getColour(), board)) {
                validMoves.add(i);
            }
        }
        if (validMoves.size() == 0) {
            return -1;
        } else {
            return validMoves.get(new Random().nextInt(validMoves.size()));
        }
    }
}
