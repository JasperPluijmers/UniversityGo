package client;


import client.roboresources.BoardStateValue2;
import client.utilities.ResponseBuilder;
import go.model.Board;
import go.utility.MoveValidator;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoboClient2 extends Client {

    private boolean firstMove = true;
    public RoboClient2(String name, InetAddress host, int port, boolean hasGui) throws IOException {
        super(name, host, port, hasGui);
    }

    @Override
    public void askMove() {
        determineMove();
    }

    public void determineMove() {
        if (firstMove) {
            if (MoveValidator.validateMove(72, super.getColour(), super.getBoard())) {
                talk(ResponseBuilder.move(super.getGameId(), super.getUserName(), 72 + ""));
                firstMove = false;
                return;
            } else if (MoveValidator.validateMove(61, super.getColour(), super.getBoard())) {
                talk(ResponseBuilder.move(super.getGameId(), super.getUserName(), 61 + ""));
                firstMove = false;
                return;
            }
        }
        BoardStateValue2 stateValue = new BoardStateValue2(super.getBoard(), super.getColour());
        if (getLastMove() == -1 && stateValue.checkWin()) {
            talk(ResponseBuilder.move(super.getGameId(), super.getUserName(), "-1"));
            return;
        }
        int bestMove = stateValue.bestMove();
        if (bestMove == -2) {
            talk(ResponseBuilder.move(super.getGameId(), super.getUserName(), "-1"));
            return;
        } else {
            talk(ResponseBuilder.move(super.getGameId(), super.getUserName(), "" + bestMove));
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
