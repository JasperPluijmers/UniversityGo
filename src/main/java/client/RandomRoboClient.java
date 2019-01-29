package client;

import client.roboresources.BoardStateValue;
import client.utilities.ResponseBuilder;
import go.model.Board;
import go.utility.MoveValidator;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomRoboClient extends RoboClient {

    public RandomRoboClient(String name, InetAddress host, int port, boolean hasGui) {
        super(name, host, port, hasGui);
    }

    @Override
    public void determineMove() {
        talk(ResponseBuilder.move(super.getGameId(), super.getName(), "" + super.randomMove()));
    }

}
