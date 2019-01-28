package client;

import client.utilities.ResponseBuilder;
import go.model.Board;
import go.utility.MoveValidator;

import java.net.InetAddress;

public class RoboClient extends Client {

    public RoboClient(String name, InetAddress host, int port, boolean hasGui) {
        super(name, host, port, hasGui);
    }

    @Override
    public void askMove() {
        try {
            this.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        determineMove();
    }

    private void determineMove() {
        Board board = super.getBoard();
        for (int i = 0; i < board.dimension*board.dimension; i++) {
            if (MoveValidator.validateMove(i,super.getColour(),board)) {
                talk(ResponseBuilder.move(super.getGameId(), super.getName(),""+i));
                return;
            }
        }
        talk(ResponseBuilder.move(super.getGameId(), super.getName(),"-1"));
    }
}
