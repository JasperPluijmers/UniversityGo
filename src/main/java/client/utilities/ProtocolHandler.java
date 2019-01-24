package client.utilities;

import client.Client;

public class ProtocolHandler {

    private Client client;

    public ProtocolHandler(Client client) {
        this.client = client;
    }

    public void handleProtocol(String message) {
        String[] command = message.split("\\+");
        switch (ServerCommand.fromString(command[0])) {
            case ACKNOWLEDGE_HANDSHAKE:
                acknowledgeHandshakeSanitizer(command);
                break;
            case ACKNOWLEDGE_CONFIG:
                acknowledgeConfigSanitizer(command);
                break;
            case ACKNOWLEDGE_MOVE:
                acknowledgeMoveSanitizer(command);
                break;
            case INVALID_MOVE:
                invalidMoveSanitizer(command);
                break;
            case REQUEST_CONFIG:
                requestConfigSanitizer(command);
                break;
            case GAME_FINISHED:
                gameFinishedSanitize(command);
                break;
            default:
                System.out.println("Not in protocol" + message);
        }
    }

    private void gameFinishedSanitize(String[] command) {
        client.gameFinished(command);
    }

    private void requestConfigSanitizer(String[] command) {
        client.makeConfig();
    }

    private void invalidMoveSanitizer(String[] command) {
        System.out.println(command[1]);
        client.askMove();
    }

    private void acknowledgeMoveSanitizer(String[] command) {
        if (command.length == 4) {
            client.updateStatus(command[3]);
            client.highlightMove(command[2]);
        }
    }

    private void acknowledgeConfigSanitizer(String[] command) {
        /*if (command.length == 5) {*/
            client.processConfig(command);
            client.updateStatus(command[4]);
        //}
    }

    private void acknowledgeHandshakeSanitizer(String[] command) {
        if (command.length == 3) {
            if (checkInt(command[1]) && checkInt(command[2])) {
                int gameId = Integer.parseInt(command[1]);
                client.handleHandshake(gameId);
            }
        }
    }

    private boolean checkInt(String number) {
        try {
            Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
