package client;

import client.utilities.ResponseBuilder;

import java.io.IOException;
import java.net.InetAddress;

public class RandomRoboClient extends RoboClient {

    public RandomRoboClient(String name, InetAddress host, int port, boolean hasGui) throws IOException {
        super(name, host, port, hasGui);
    }

    @Override
    public void determineMove() {
        talk(ResponseBuilder.move(super.getGameId(), super.getUserName(), "" + super.randomMove()));
    }

}
