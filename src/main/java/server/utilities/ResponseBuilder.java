package server.utilities;

import go.utility.Colour;

/**
 * Builds responses based on the protocol
 * specified on: https://github.com/JasperPluijmers/GoProtocol.
 */
public class ResponseBuilder {
    public static String acknowledgeHandshake(int gameId, boolean isLeader) {
        String leader;
        if (isLeader) {
            leader = "1";
        } else {
            leader = "0";
        }
        return "ACKNOWLEDGE_HANDSHAKE+" + gameId + "+" + leader;
    }

    public static String acknowledgeConfig(
            String username, Colour colour,
                                           int dimension, String gameState, String opponent) {
        return "ACKNOWLEDGE_CONFIG+" + username +
                "+" + colour.getValue() + "+" + dimension + "+" + gameState + "+" + opponent;
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

    public static String requestRematch() {
        return "REQUEST_REMATCH";
    }

    public static String acknolwedgeRematch(int value) {
        return "ACKNOWLEDGE_REMATCH+" + value;
    }
}
