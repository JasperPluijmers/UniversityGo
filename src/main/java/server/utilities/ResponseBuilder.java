package server.utilities;

import go.utility.Colour;

public class ResponseBuilder {
    public static String acknowledgeHandshake(int gameId, boolean isLeader) {
        return "ACKNOWLEDGE_HANDSHAKE+"+gameId+ "+" + (isLeader ? 1 : 0);
    }

    public static String acknowledgeConfig(String username, Colour colour, int dimension, String gameState) {
        return "ACKNOWLEDGE_CONFIG+" + username + "+" + colour.getValue() + "+" + dimension + "+" + gameState;
    }

    public static String updateStatus(String gameState) {
        return "UPDATE_STATUS+" + gameState;
    }

    public static String wrongMove() {
        return "INVALID_MOVE+Move not valid, please try again";
    }

    public static String requestConfig() {
        return "REQUEST_CONFIG+What color and board size would you like to play?";
    }

    public static String unknownCommand(String message) {
        return "UNKNOWN_COMMAND+" + message;
    }

    public static String gameFinished(int gameId, String winner, String score, String message) {
        return "GAME_FINISHED+" + gameId + "+" + winner + "+" + score + "+" + message;
    }

    public static String acknowledgeMove(int gameId, int move, Colour colour, String state) {
        return "ACKNOWLEDGE_MOVE+" + gameId + "+" + move + ";" + colour.getValue() + "+" + state;
    }
}
