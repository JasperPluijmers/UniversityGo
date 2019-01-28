package client.utilities;

public class ResponseBuilder {

    public static String handshake(String username) {
        return "HANDSHAKE" + "+" + username;
    }
    public static String setConfig(int gameId, int colour, int boardSize) {
        return "SET_CONFIG" + "+" + gameId + "+" + colour + "+" + boardSize;
    }

    public static String move(int gameId, String userName, String index) {
        return "MOVE" + "+" + gameId + "+" + userName + "+" + index;
    }

    public static String exit(int gameId, String username) {
        return "EXIT+" + gameId + "+" + username;
    }

    public static String setRematch(int value) {
        return "SET_REMATCH+" + value;
    }
}
