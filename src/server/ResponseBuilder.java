package server;

public class ResponseBuilder {
    public static String acknowledgeHandshake(int gameId, boolean isLeader) {
        return "ACKNOWLEDGE_HANDSHAKE+"+gameId+ "+" +isLeader;
    }

    public static String acknowledgeConfig(String username, int colour, int dimension, String gameState) {
        return "ACKNOWLEDGE_CONFIG+" + username + "+" + colour + "+" + dimension + "+" + gameState;
    }

    public static String updateStatus(String gameState) {
        return "UPDATE_STATUS" + "+" + gameState;
    }

    public static String wrongMove() {
        return "INVALID_MOVE+Move not valid, please try again";
    }

    public static String requestConfig() {
        return "REQUEST_CONFIG+What color and board size would you like to play?";
    }

    public static String unknownCommand() {
        return "UNKNOWN_COMMAND+Command not recognized.";
    }

    public static String gameFinished(int gameId, String winner, String score, String message) {
        return "GAME_FINISHED" + "+" + gameId + "+" + winner + "+" + score + "+" + message;
    }
}
