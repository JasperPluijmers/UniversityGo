package server.utilities;

import server.ClientHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ProtocolHandler {

    private ClientHandler clientHandler;
    private static Set<Integer> colourValues;

    public ProtocolHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        colourValues = new HashSet<>();
        colourValues.add(0);
        colourValues.add(1);
        colourValues.add(2);
    }

    public void handleProtocol(String message) {
        String[] command = message.split("\\+");
        switch (ClientCommand.fromString(command[0])) {
            case HANDSHAKE:
                sanitizeHandshake(command);
                break;
            case SET_CONFIG:
                sanitizeSetConfig(command);
                break;
            case MOVE:
                sanitizeMove(command);
                break;
            case EXIT:
                clientHandler.handleQuit();
                break;
            case SET_REMATCH:
                sanitizeSetRematch(command);
                break;
            default:
                clientHandler.handleUnknownCommand("Message not in protocol: " + message);

        }
    }

    private void sanitizeSetRematch(String[] command) {
        if (command.length == 2) {
            if (checkInt(command[1])) {
                int value = Integer.parseInt(command[1]);
                if (value == 0 || value == 1) {
                    clientHandler.handleSetRematch(value);
                }
            }
        }
    }

    private void sanitizeMove(String[] command) {
        if (command.length == 4) {
            if (checkInt(command[3]) && Integer.parseInt(command[3]) > -2) {
                clientHandler.handleMove(Integer.parseInt(command[3]));
                return;
            } else {
                clientHandler.handleWrongMove();
                return;
            }
        }
        clientHandler.handleUnknownCommand("Did not recognize protocol for: MOVE");
    }

    private boolean checkInt(String number) {
        try {
            Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public void sanitizeSetConfig(String[] command) {
        int colour;
        int boardSize;
        System.out.println(Arrays.toString(command));
        if (command.length == 4) {
            if (checkInt(command[1])) {

                if (checkInt(command[2])) {
                    colour = Integer.parseInt(command[2]);

                    if (checkInt(command[3])) {
                        boardSize = Integer.parseInt(command[3]);
                        if (colourValues.contains(colour) && boardSize > 0) {
                            clientHandler.handleSetConfig(colour, boardSize);
                            return;
                        }
                    }
                }
            }
        }
        clientHandler.handleUnknownCommand("Did not recognize protocol for: SET_CONFIG");
    }

    public void sanitizeHandshake(String[] command) {
        if (command.length == 2) {
            clientHandler.handleHandshake(command[1]);
        } else {
            clientHandler.handleUnknownCommand("Handshake format is wrong, perhaps a + in the username?");
        }

    }


}
